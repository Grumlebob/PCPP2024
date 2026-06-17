-module(server).
-export([start/2, loop/1]).
%13.1.1 Explanation of all the elements in the actor: See comments in the code.

% Change history:
% 13.1.1: Initial worker creation. Field: workers
% 13.1.2: Added min_workers and max_workers. Field: min_workers, max_workers
-record(state, {
    min_workers,         % Minimum number of workers to maintain
    max_workers,         % Maximum number of workers allowed
    idle_workers = [],   % Workers not currently processing tasks
    busy_workers = [],   % Workers currently processing tasks
    pending_tasks = [],  % Tasks in queue, because no worker is available
    worker_count = 0     % Current total number of spawned workers, including idle and busy
}).

% 13.1.2:
% Simple understanding: 
% The server spawns the minimum number of workers.
% It then updates its state, to be able to spawn at most max_workers.
start(MinWorkers, MaxWorkers) ->
    % 13.1.1: Initial worker creation
    InitialWorkers = spawn_workers(MinWorkers),
    io:format("Server started with ~p workers.~n", [MinWorkers]),
    State = #state{
        min_workers = MinWorkers,
        max_workers = MaxWorkers,
        idle_workers = InitialWorkers,
        busy_workers = [],
        pending_tasks = [],
        worker_count = MinWorkers
    },
    loop(State).

%The main loop of the server
% Simple understanding:
% In all of the cases: Server updates its state. Dynamic topology actor, but only a single layer. "Adaptive load balancing"
% compute: Gives task to idle worker, or queues it if no idle workers (or spawns until max workers)
% work_done: Worker is removed if over min_workers, takes pending task or goes idle.
% down (automatic message from monitor): Worker is replaced, and crashing task is dismissed. "Fault tolerance"
% stop: Kill the server.
loop(State) ->
    receive
        {compute, SenderPid, Tasks} ->
            io:format("Received tasks: ~p~n", [Tasks]),
            NewState = assign_tasks(SenderPid, Tasks, State),
            loop(NewState);

        {work_done, WorkerPid} ->
            io:format("Worker ~p completed a task.~n", [WorkerPid]),
            NewState = handle_work_done(WorkerPid, State),
            loop(NewState);

        {'DOWN', _Ref, process, WorkerPid, Reason} ->
            io:format("Worker ~p crashed with reason: ~p~n", [WorkerPid, Reason]),
            NewState = handle_worker_crash(WorkerPid, Reason, State),
            loop(NewState);

        stop ->
            io:format("Stopping server and all workers.~n"),
            stop_workers(State#state.idle_workers ++ State#state.busy_workers),
            ok
    end.

% Simple understanding:
% Case 0: Tasks are empty, we just return state. 
% Case 1: No idle workers and below max workers: Create a new worker and assign the first task to it
% Case 2: No idle workers and at max workers: Queue the tasks
% Case 3: Use an available idle worker
assign_tasks(_SenderPid, [], State) ->
    % No tasks to assign; just return the current state
    State;
assign_tasks(SenderPid, Tasks, State = #state{
    idle_workers = Idle, busy_workers = Busy, pending_tasks = Pending, max_workers = MaxWorkers, worker_count = WorkerCount
}) ->
    case Idle of
        % 13.1.2.b: Create new worker if no idle ones and below max, assign first task
        [] when WorkerCount < MaxWorkers ->
            % we receive a list of tasks, worker only takes one.
            [Task | RemainingTasks] = Tasks,
            NewWorkerPid = spawn_monitored_worker(),
            NewWorkerPid ! {compute, SenderPid, Task},
            io:format("Spawned new worker ~p for task: ~p~n", [NewWorkerPid, Task]),
            State#state{
                worker_count = WorkerCount + 1,
                busy_workers = [NewWorkerPid | Busy],
                % the remaining unhandled tasks are put in pending
                pending_tasks = Pending ++ [{SenderPid, T} || T <- RemainingTasks]
            };
        % 13.1.1.b & 13.1.2.c: Queue tasks if at max workers
        [] ->
            io:format("No idle workers; queuing tasks: ~p~n", [Tasks]),
            % Put all tasks in pending
            State#state{pending_tasks = Pending ++ [{SenderPid, T} || T <- Tasks]};
        % 13.1.1.a & 13.1.2.a: Use available idle worker
        [WorkerPid | Rest] ->
            [Task | RemainingTasks] = Tasks,
            WorkerPid ! {compute, SenderPid, Task},
            io:format("Assigned task ~p to worker ~p~n", [Task, WorkerPid]),
            assign_tasks(SenderPid, RemainingTasks, State#state{
                idle_workers = Rest,
                busy_workers = [WorkerPid | Busy]
            })
    end.

% Change history:
% 13.1.1: Fixed workers, either idle or busy
% 13.1.4: Added elasticity, by removing excess workers down to min_workers.
% Simple understanding:
% Case 1: Remove excess workers when idle
% Case 2: Make worker idle if no pending tasks
% Case 3: Assign pending task if available
handle_work_done(WorkerPid, State = #state{
    idle_workers = Idle, busy_workers = Busy, pending_tasks = Pending, min_workers = MinWorkers, worker_count = WorkerCount
}) ->
    case Pending of
        % 13.1.4: Remove excess workers when idle
        [] when WorkerCount > MinWorkers ->
            WorkerPid ! stop,
            State#state{
                busy_workers = lists:delete(WorkerPid, Busy),
                worker_count = WorkerCount - 1
            };
        % 13.1.1.b: Make worker idle if no pending tasks
        [] ->
            State#state{
                idle_workers = [WorkerPid | Idle],
                busy_workers = lists:delete(WorkerPid, Busy)
            };
        % 13.1.1.a: Assign pending task if available
        [{SenderPid, NextTask} | Rest] ->
            WorkerPid ! {compute, SenderPid, NextTask},
            State#state{
                pending_tasks = Rest,
                busy_workers = Busy
            }
    end.

% 13.1.3:
% Simple understanding: 
% When worker crashes, a new worker is spawned monitored. The crashing task is dismissed.
handle_worker_crash(CrashedWorkerPid, _Reason, State = #state{
    idle_workers = Idle,
    busy_workers = Busy,
    worker_count = WorkerCount
}) ->
    NewWorkerPid = spawn_monitored_worker(),
    % Replace crashed worker.
    NewIdleList = lists:delete(CrashedWorkerPid, Idle),
    NewBusy = [NewWorkerPid | lists:delete(CrashedWorkerPid, Busy)],

    % We assign worker its role / task.
    handle_work_done(NewWorkerPid, State#state{
        idle_workers = NewIdleList,
        busy_workers = NewBusy,
        worker_count = WorkerCount
    }).

% 13.1.4:
stop_workers([]) -> ok;
stop_workers([Worker | Rest]) ->
    Worker ! stop,
    stop_workers(Rest).


% ---- Internal Helper Functions -------

% Simple understanding: 
% Spawn N workers, monitoring each one
spawn_workers(N) ->
    [spawn_monitored_worker() || _ <- lists:seq(1, N)].

% Simple undestanding: 
% A new worker is spawned with its start being the init function, and monitored by the server ie. self()
spawn_monitored_worker() ->
    {Pid, _Ref} = spawn_monitor(worker, init, [self()]),
    Pid.