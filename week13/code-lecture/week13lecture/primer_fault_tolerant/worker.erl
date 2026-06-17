% raup@itu.dk * 2024-11-20

-module(worker).
-export([start/1, init/1]).

% State
-record(worker_state, {primer_server, active_workers}).

% Start
start(PrimerPID) ->
    spawn(?MODULE, init, [PrimerPID]).

% Initialization 
init(PrimerPID) ->
    State = #worker_state{primer_server=PrimerPID,
                          active_workers=maps:new()},
    loop(State).
    

% Behavior upon receiving messages
loop(State) ->
    receive
        {is_prime, Number} ->
            handle_is_prime(Number, State);
        {'DOWN', _Ref, process, PID, Reason} ->
            handle_exit(PID, Reason, State)
    end.

% 5. Message handlers

handle_is_prime(Number, 
                State = #worker_state{primer_server=PrimerPID,
                                      active_workers=ActiveWorkers}) ->
    {SJO_PID, _SJO_Ref} = single_job_worker:start_monitor(),
    SJO_PID ! {check_prime, Number, PrimerPID},
    NewActiveWorkers = maps:put(SJO_PID, Number, ActiveWorkers),
    NewState = State#worker_state{active_workers=NewActiveWorkers},
    loop(NewState).

handle_exit(PID, normal, 
            State = #worker_state{active_workers = ActiveWorkers}) ->
    NewActiveWorkers = maps:remove(PID, ActiveWorkers),
    NewState = State#worker_state{active_workers=NewActiveWorkers},
    loop(NewState);

handle_exit(PID, Reason, 
            State = #worker_state{active_workers = ActiveWorkers}) ->
    io:format("Worker ~p failed due to ~p~n", [PID, Reason]),
    Number = maps:get(PID, ActiveWorkers),
    NewActiveWorkers = maps:remove(PID, ActiveWorkers),    
    NewState = State#worker_state{active_workers=NewActiveWorkers},
    handle_is_prime(Number, NewState).
