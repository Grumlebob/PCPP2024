-module(user).
-export([start/1, init/1, start_reg/1,
         subscribe_to_chatroom/2,
         unsubscribe_from_chatroom/1,
         send_message_to_broadcast/2]).

% 1. State of the actor
-record(user_state, {username, chatroom}).

% 2. Function(s) to create turnstile actors
start(Username) ->
    spawn(?MODULE, init, [Username]).

start_reg(Username) ->
    PID = spawn(?MODULE, init, [Username]),
    register(Username, PID).


% 3. Function(s) to initialize the state and the actors behavior upon receiving messages
init(Username) ->
    State = #user_state{username=Username, chatroom=undefined},
    loop(State).


% 4. Function defining the behavior upon receiving messages
loop(State) ->
    receive

        {subscribe, ChatroomPID} ->
            handle_subscribe(ChatroomPID, State);

        {unsubscribe} ->
            handle_unsubscribe(State);

        {message_to_broadcast, Message} ->
            handle_message_to_broadcast(Message, State);

        {message_from_chatroom, {SenderPID, Message}} ->
            handle_message_from_chatroom(Message, SenderPID, State);

        M ->
            io:format("[Log of ~s] I do not know this type of message 😕, so I directly discard it.~n~p~n", [State#user_state.username, M]),
            loop(State)
    end.



% 5. Message handlers 
handle_subscribe(ChatroomPID, State) ->
    Ref = make_ref(),
    ChatroomPID ! {subscribe, {Ref, self()}},
    receive
        {subscription_ok, Ref} ->
            NewState = State#user_state{chatroom=ChatroomPID},
            io:format("Successfully subscribed in the chatroom!~n"),
            loop(NewState);
        {already_subscribed, Ref} ->
            io:format("The chatroom says I am already subscribed.~n"),
            loop(State)
    end.

handle_unsubscribe(State) when State#user_state.chatroom == undefined ->
    io:format("You are currently not subscribed to any chatroom~n"),
    loop(State);

handle_unsubscribe(State) ->
    ChatroomPID = State#user_state.chatroom,    
    Ref = make_ref(),
    ChatroomPID ! {unsubscribe, {Ref, self()}},
    receive
        {unsubscription_ok, Ref} ->
            NewState = State#user_state{chatroom=ChatroomPID},
            io:format("Successfully unsubscribed from the chatroom!~n"),
            loop(NewState);
        {user_not_subscribed, Ref} ->
            io:format("The chatroom says I am not subscribed.~n"),
            loop(State)
    end.

handle_message_to_broadcast(Message, State) ->
    ChatroomPID = State#user_state.chatroom,
    ChatroomPID ! {broadcast, {Message, self()}},
    loop(State).

handle_message_from_chatroom(Message, SenderPID, State) ->
    io:format("[Log of ~s] Message from ~p: ~p~n", 
              [State#user_state.username, SenderPID, Message]),
    loop(State).
    


% 6. API
subscribe_to_chatroom(UserPID, ChatroomPID) ->
    UserPID ! {subscribe, ChatroomPID}.

unsubscribe_from_chatroom(UserPID) ->
    UserPID ! {unsubscribe}.

send_message_to_broadcast(UserPID, Message) ->
    UserPID ! {message_to_broadcast, Message}.
