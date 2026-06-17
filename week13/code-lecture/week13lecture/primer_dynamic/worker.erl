% raup@itu.dk * 2024-11-19

-module(worker).
-export([start/1, init/1]).

% State
-record(worker_state, {primer_server}).

% Start
start(PrimerPID) ->
    spawn(?MODULE, init, [PrimerPID]).

% Initialization 
init(PrimerPID) ->
    State = #worker_state{primer_server=PrimerPID},
    loop(State).
    

% Behavior upon receiving messages
loop(State) ->
    receive
        {is_prime, Number} ->
            handle_is_prime(Number, State)
    end.

% 5. Message handlers

handle_is_prime(Number, State = #worker_state{primer_server=PrimerPID}) ->
    SJO_PID = single_job_worker:start(),
    SJO_PID ! {check_prime, Number, PrimerPID},
    loop(State).
