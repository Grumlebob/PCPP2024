-module(tester).
-include("defs.hrl").
-export([test_all/0, test_fixed_workers/0, test_min_max_workers/0, 
         test_worker_crash/0, test_elasticity/0]).

%%% Test All Features %%%
test_all() ->
    io:format("=== Running all tests ===~n"),
    
    FixedWorkersResult = test_fixed_workers(),
    print_test_result("Fixed Workers Test", FixedWorkersResult),
    timer:sleep(2000), % Delay to avoid overlapping logs
    
    MinMaxWorkersResult = test_min_max_workers(),
    print_test_result("Min/Max Workers Test", MinMaxWorkersResult),
    timer:sleep(2000), % Delay
    
    WorkerCrashResult = test_worker_crash(),
    print_test_result("Worker Crash Test", WorkerCrashResult),
    timer:sleep(2000), % Delay
    
    ElasticityResult = test_elasticity(),
    print_test_result("Elasticity Test", ElasticityResult).

%%% Test Fixed Number of Workers %%%
% Test for 13.1.1: Basic worker management
test_fixed_workers() ->
    io:format("Testing fixed workers...~n"),
    % Use same min/max to enforce fixed number of workers
    Pid = spawn(server, start, [3, 3]),
    Tasks = [
        #task{function=fun (X, Y) -> X + Y end, arguments=[1, 2]},
        #task{function=fun (X, Y) -> X * Y end, arguments=[3, 4]},
        #task{function=fun (X, Y) -> X - Y end, arguments=[5, 6]}
    ],
    Pid ! {compute, self(), Tasks},
    % All tasks should complete successfully
    receive_results(length(Tasks), length(Tasks)).

%%% Test Minimum and Maximum Workers %%%
% Test for 13.1.2: Dynamic worker scaling
test_min_max_workers() ->
    io:format("Testing min/max workers...~n"),
    % Test with min=2, max=5 to verify scaling
    Pid = spawn(server, start, [2, 5]),
    Tasks = [
        #task{function=fun (X) -> X end, arguments=[1]},
        #task{function=fun (X) -> X end, arguments=[2]},
        #task{function=fun (X) -> X end, arguments=[3]},
        #task{function=fun (X) -> X end, arguments=[4]},
        #task{function=fun (X) -> X end, arguments=[5]},
        #task{function=fun (X) -> X end, arguments=[6]}
    ],
    Pid ! {compute, self(), Tasks},
    receive_results(length(Tasks), length(Tasks)).

%%% Test Worker Crash Handling %%%
% Test for 13.1.3: Fault tolerance
test_worker_crash() ->
    io:format("Testing worker crash handling...~n"),
    % Include a task that will cause division by zero
    T4 = #task{function=fun (X, Y) -> (X + Y) / 0 end, arguments=[42, 42]},  % Crashing Task
    T1 = #task{function=fun (X, Y) -> X + Y end, arguments=[42, 42]},
    T2 = #task{function=fun (X, Y) -> X - Y end, arguments=[42, 42]},
    T3 = #task{function=fun (X, Y) -> X * Y end, arguments=[42, 42]},
    Tasks = [T4, T2, T3, T1],
    Pid = spawn(server, start, [4, 5]),
    Pid ! {compute, self(), Tasks},
    % Expect 3 successes (one task will crash)
    receive_results(3, length(Tasks)).

%%% Test Elasticity %%%
% Test for 13.1.4: Elastic scaling
test_elasticity() ->
    io:format("Testing elasticity...~n"),
    % Test with min=2, max=5 to verify scaling up and down
    Pid = spawn(server, start, [2, 5]),
    Tasks = lists:duplicate(10, #task{function=fun (X) -> X end, arguments=[1]}),
    Pid ! {compute, self(), Tasks},
    % All tasks should complete and workers should scale back to minimum
    receive_results(length(Tasks), length(Tasks)).

%%% Helper Functions %%%
print_test_result(TestName, Result) ->
    case Result of
        true -> io:format("✓ PASSED: ~s~n", [TestName]);
        false -> io:format("✗ FAILED: ~s~n", [TestName])
    end.

% Receive results with expected successes and total tasks
receive_results(ExpectedSuccesses, TotalTasks) ->
    receive_results(ExpectedSuccesses, TotalTasks, 0).

% Internal receive_results implementation
receive_results(0, _TotalTasks, Received) ->
    io:format("Received all ~p expected successful results~n", [Received]),
    true;
receive_results(Expected, TotalTasks, Received) when Expected > 0 ->
    io:format("Waiting for results: Expected=~p, Total=~p, Received=~p~n",
              [Expected, TotalTasks, Received]),
    receive
        {result, Task, Result} ->
            io:format("Task ~p completed with result ~p~n", [Task, Result]),
            receive_results(Expected - 1, TotalTasks, Received + 1);
        Other ->
            io:format("Received unexpected message: ~p~n", [Other]),
            receive_results(Expected, TotalTasks, Received)
    after 5000 ->
        io:format("Timeout waiting for results. ~p successes still expected.~n", [Expected]),
        false
    end.