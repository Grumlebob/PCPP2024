% raup@itu.dk * 2024-11-15

-module(turnstile).
-export([start/1, init/1]).

% State of the actor
-record(turnstile_state, {counter_server}).

% Function to create turnstile actors
start(CounterPID) ->
    spawn(?MODULE, init, [CounterPID]).

% Function to initialize the state and the actors behavior upon receiving messages
init(CounterPID) ->
    State = #turnstile_state{counter_server = CounterPID},
    loop(State).

% Function defining the behavior upon receiving messages
loop(State) ->
    receive
        person_crossing ->
            State#turnstile_state.counter_server ! increment,
            loop(State)                
    end.
