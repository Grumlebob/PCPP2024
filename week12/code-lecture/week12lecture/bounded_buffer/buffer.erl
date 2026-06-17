% raup@itu.dk * 2024-11-16

-module(buffer).
-export([start/1, start_reg/2, init/1, put/2, get/1, print_state/1, 
        deadlock_situation/0, non_deadlock_situation1/0, non_deadlock_situation2/0]).

% 1. State of the actor
-record(buffer_state, {buffer, max_size, 
                       pending_puts, pending_gets}).

% 2. Function(s) to create turnstile actors
start(Max_Size) ->
    spawn(?MODULE, init, [Max_Size]).

start_reg(Buffer_atom, Max_Size) ->
    PID = spawn(?MODULE, init, [Max_Size]),
    register(Buffer_atom, PID).

% 3. Function(s) to initialize the state and the actors behavior upon receiving messages
init(Max_Size) ->
    State = #buffer_state{buffer=[], max_size = Max_Size, pending_puts=[], pending_gets=[]},
    loop(State).

% 4. Function defining the behavior upon receiving messages
loop(State) ->
    receive
        {put, {Ref, Elem, SenderPID}} -> 
            handle_put(Ref, Elem, SenderPID, State);
        {get, {Ref, SenderPID}} -> 
            handle_get(Ref, SenderPID, State);
        print_state -> 
            handle_print_state(State)
    end.


% 5. Message handlers 

%% Put cases

%%% The buffer is empty and there are pending get requests
handle_put(Ref, Elem, SenderPID, State) 
  when length(State#buffer_state.buffer) == 0,
       length(State#buffer_state.pending_gets) > 0 ->
    [{GetRef, GetSenderPID} | TPendingGets] = State#buffer_state.pending_gets,
    GetSenderPID ! {get_ok, {GetRef, Elem}},
    SenderPID ! {put_ok, Ref},
    NewState = State#buffer_state{pending_gets=TPendingGets},
    loop(NewState);

%%% There is space to put the element and no pending get requests
handle_put(Ref, Elem, SenderPID, State) 
  when length(State#buffer_state.buffer) < State#buffer_state.max_size,
       length(State#buffer_state.pending_gets) == 0 ->
    NewBuffer = State#buffer_state.buffer ++ [Elem],
    NewState = State#buffer_state{buffer=NewBuffer},
    SenderPID ! {put_ok, Ref},
    loop(NewState);

%% NOTE: Is it possible to have a non empty buffer with pending get requests?

%%% There is no space
handle_put(Ref, Elem, SenderPID, State) 
  when length(State#buffer_state.buffer) == State#buffer_state.max_size ->
    NewPendingPuts = State#buffer_state.pending_puts ++ [{Ref, SenderPID, Elem}],
    NewState = State#buffer_state{pending_puts=NewPendingPuts},
    loop(NewState).


%% Get cases

%%% The buffer is full and there are put requests
handle_get(Ref, SenderPID, State) 
  when length(State#buffer_state.buffer) == State#buffer_state.max_size,
       length(State#buffer_state.pending_puts) > 0 ->
    [{RefPut, PutSenderPID, PendingElem} | TPendingPuts] = State#buffer_state.pending_puts,
    [Elem | TBuffer] = State#buffer_state.buffer,
    SenderPID ! {get_ok, {Ref, Elem}},
    NewState = State#buffer_state{buffer=TBuffer, pending_puts=TPendingPuts},
    handle_put(RefPut, PendingElem, PutSenderPID, NewState); %% Will have space to put the element

%% NOTE: Is it possible to have a non full buffer with pending get requests?

%%% The buffer is not empty and there are no put requests
handle_get(Ref, SenderPID, State) 
  when length(State#buffer_state.buffer) > 0,
       length(State#buffer_state.pending_puts) == 0 ->
    [Elem | TBuffer] = State#buffer_state.buffer,
    SenderPID ! {get_ok, {Ref, Elem}},
    NewState = State#buffer_state{buffer=TBuffer},
    loop(NewState); %% Will have space to put the element

%%% The buffer is empty
handle_get(Ref, SenderPID, State) 
  when length(State#buffer_state.buffer) == 0 ->
    NewPendingGets = State#buffer_state.pending_gets ++ [{Ref, SenderPID}],
    NewState = State#buffer_state{pending_gets=NewPendingGets},
    loop(NewState).

%% Print state

handle_print_state(State) ->
    io:format("~p~n", [State#buffer_state.buffer]),
    loop(State).



% 6. API 
put(BufferPID, Elem) ->
    Ref = make_ref(),
    BufferPID ! {put, {Ref, Elem, self()}},
    receive
        {put_ok, Ref} -> 
            put_ok
    end.

get(BufferPID) ->
    Ref = make_ref(),
    BufferPID ! {get, {Ref, self()}},
    receive
        {get_ok, {Ref, Elem}}->
            {get_ok, Elem}
    end.

print_state(BufferPID) ->
    BufferPID ! print_state.


% Examples for the lecture
deadlock_situation() ->
    BufferPID = buffer:start(1),
    A1 = 
        fun () -> 
            buffer:get(BufferPID),
            buffer:put(BufferPID, something),
            io:format("Done~n") 
        end,
    A2 = 
        fun () -> 
            buffer:get(BufferPID),
            buffer:put(BufferPID, something),
            io:format("Done~n") 
        end,
    lists:foreach(fun (A) -> spawn(A) end, [A1,A2]).

non_deadlock_situation1() ->
    BufferPID = buffer:start(1),
    A1 = 
        fun () -> 
            buffer:get(BufferPID),
            buffer:put(BufferPID, something),
            io:format("Done~n") 
        end,
    A2 = 
        fun () -> 
            buffer:put(BufferPID, something),
            buffer:get(BufferPID),
            io:format("Done~n") 
        end,
    lists:foreach(fun (A) -> spawn(A) end, [A1,A2]).

non_deadlock_situation2() ->
    BufferPID = buffer:start(1),
    Producer = fun () -> buffer:put(BufferPID, something), io:format("I'm done producing~n") end,
    Consumer = fun () -> buffer:get(BufferPID), io:format("I'm done consuming~n") end,
    Tasks = [Consumer, Consumer, Producer, Producer],
    lists:foreach(fun (Task) -> spawn(Task) end,Tasks).
