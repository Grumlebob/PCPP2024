-module(chatroom).
-export([start/1, init/1, start_reg/1]).

% 1. State of the actor
-record(cr_state, {name,users}).

% 2. Function(s) to create turnstile actors
start(ChatroomName) -> 
    spawn(?MODULE, init, [ChatroomName]).

start_reg(ChatroomName) -> 
    PID = spawn(?MODULE, init, [ChatroomName]),
    register(ChatroomName, PID).

% 3. Function(s) to initialize the state and the actors behavior upon receiving messages
init(ChatroomName) ->
    State = #cr_state{name=ChatroomName, users=[]},
    loop(State).

% 4. Function defining the behavior upon receiving messages
loop(State) ->
    receive
        {subscribe, {Ref, UserPID}} ->
            handle_subscribe(UserPID, Ref, State);
        {unsubscribe, {Ref, UserPID}} ->
            handle_unsubscribe(UserPID, Ref, State);
        {broadcast, {Message, UserPID}} ->
            handle_send_message(Message, UserPID, State)
    end.


% 5. Message handlers 
handle_subscribe(UserPID, Ref, State) ->
    Users = State#cr_state.users,
    case lists:member(UserPID, Users) of
        false ->
            NewState = State#cr_state{users= [UserPID|Users]},
            UserPID ! {subscription_ok, Ref},
            loop(NewState);
        true ->
            UserPID ! {already_subscribed, Ref},
            loop(State)
    end.

handle_unsubscribe(UserPID, Ref, State) ->
    Users = State#cr_state.users,
    case lists:member(UserPID, Users) of
        false ->            
            UserPID ! {user_not_subscribed, Ref},
            loop(State);
        true ->
            NewUserList = [User || User <- State#cr_state.users, User /= UserPID],
            NewState = State#cr_state{users=NewUserList},
            UserPID ! {unsubscription_ok, Ref},
            loop(NewState)
    end.

handle_send_message(Message, UserPID, State) ->
    Users = State#cr_state.users,
    Send = fun (PID) -> 
                   PID ! {message_from_chatroom, {UserPID, Message}}
           end,
    case lists:member(UserPID, Users) of
        true ->
            UsersWOSender = [User || User <- Users, User /= UserPID],
            lists:foreach(Send,UsersWOSender);
        false ->
            user_not_subscribed
    end,
    loop(State).