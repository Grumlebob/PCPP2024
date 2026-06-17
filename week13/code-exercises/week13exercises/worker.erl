% raup@itu.dk * 2024-11-22

-module(worker).
-export([start/1, init/1]).
-include("defs.hrl").

% 1. State
-record(worker_state, {server}).

% 2. Start
start(ServerPID) ->
    spawn(?MODULE, init, [ServerPID]).

% 3. Initialization
init(ServerPID) ->
    State = #worker_state{server=ServerPID},
    loop(State).

% 4. Behavior upon receiving messages
loop(State) ->
    receive
        {compute, SenderPID, Task} ->
            handle_compute(SenderPID, Task, State);
        stop ->
            exit(normal)
    end.

% 5. Message handlers
handle_compute(SenderPID, 
               Task = #task{function = F, arguments=Args}, 
               State = #worker_state{server = ServerPID}) ->
    Result = apply(F, Args),
    SenderPID ! {result, Task, Result},
    ServerPID ! {work_done, self()},
    loop(State).
