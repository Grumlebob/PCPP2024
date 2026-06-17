% raup@itu.dk * 2024-11-15

-module(counter).
-export([start/0, init/1]).

% State of the actor
-record(counter_state, {total}).

% Function to create counter actors
start() ->
    spawn(?MODULE, init, [0]).

% Function to initialize the state and the actors behavior upon receiving messages
init(InitialValue) ->
    InitialState = #counter_state{total = InitialValue},
    loop(InitialState).

% Function defining the behavior upon receiving messages
loop(State) ->
    receive
        increment ->
            CurrentTotal = State#counter_state.total,
            NewState = State#counter_state{total = CurrentTotal + 1},
            io:format("A visitor arrived 🤗~n"),
            loop(NewState);
        print_total ->
            io:format("The counter value is ~p~n",
                      [State#counter_state.total]),
            loop(State)
    end.