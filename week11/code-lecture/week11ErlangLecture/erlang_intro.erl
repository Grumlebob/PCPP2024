% raup@itu.dk * 2024-11-05

%% module definition
-module(erlang_intro).

%% exporting functions
-export([square_plus_2/1,
         apply_after_squared/2,
         what_type_is_vehicle_if/1,
         what_type_is_vehicle_guard/1,
         %% this_does_not_work/0,
         pattern_matching_case/1,
         equal/2,
         len/1,
         try_catcher/1,
         catcher/1,
         factorial/1,
         factorial_tail/1
        ]).

%% import records
-include("header.hrl").


%%%%%%%%%%%%%%%
%% functions %%
%%%%%%%%%%%%%%%

square_plus_2(X) ->
    Y = X*X,
    2+Y.



%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%% higher-order functions %%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%

apply_after_squared(X, F) ->
    F(X*X).



%%%%%%%%%%%%%%%%%%%%
%% if-statements  %%
%%%%%%%%%%%%%%%%%%%%

what_type_is_vehicle_if(Vehicle) ->
    if Vehicle#vehicle.type == car ->
            io:format("It is a car~n");
       Vehicle#vehicle.type == plane, Vehicle#vehicle.color == white ->
            io:format("It is a white plane~n");
       Vehicle#vehicle.type == plane ->
            io:format("It is a plane~n");
       true -> % this is the else
            io:format("Vehicle type unknown~n")
    end.



%%%%%%%%%%%%
%% guards %%
%%%%%%%%%%%%

what_type_is_vehicle_guard(Vehicle) when Vehicle#vehicle.type == car ->
    io:format("It is a car~n");
what_type_is_vehicle_guard(Vehicle) when Vehicle#vehicle.type == plane, Vehicle#vehicle.color == white ->
    io:format("It is a white plane~n");
what_type_is_vehicle_guard(Vehicle) when Vehicle#vehicle.type == plane ->
    io:format("It is a plane~n");
what_type_is_vehicle_guard(_Vehicle) ->
    io:format("Vehicle type unknown~n").


%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%% incorrect if-statement %%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%

%% this_does_not_work() ->
%%     if 1 =/= 1 ->
%%             ok
%%     end.




%%%%%%%%%%%%%%%%%%%%%%
%% pattern matching %%
%%%%%%%%%%%%%%%%%%%%%%

pattern_matching_case(X) ->
    case X of 
        {Y,_Z} ->
            io:format("The first element in the 2-tuple is ~p~n", [Y]);
        [Y|_Z] ->
            io:format("The head of the list is ~p~n", [Y]);
        #vehicle{color=Y} ->
            io:format("The color of the ~p is ~p~n", [X#vehicle.type, Y]);
        X ->
            io:format("The value of the input is ~p and we do not know the type ~n", [X])
    end.

equal(X,X) -> true;
equal(_,_) -> false.
    
len([]) -> 0;
len([_H|T]) -> 1 + len(T);
len(_Other) -> 
    io:format("Please input a list~n"),
    exit(badarg).
    



%%%%%%%%%%%%%%%%%%%%%%%%
%% Exception handling %%
%%%%%%%%%%%%%%%%%%%%%%%%

%% try-catch-after block

try_catcher(F) when is_function(F,0) ->
    try F() of %% "of" is optional and you can have several statements in sequence separeted by ","
        _ ->
            ok
    catch
        exit:Exit ->
            io:format("The function has thrown an exit error: ~p~n",[Exit]),
            {exit, Exit};
        error:specific_error ->
            io:format("The function has thrown a specfic error~n"),
            {error, specific_error};
        error:Error ->
            io:format("The function has thrown an error error: ~p~n",[Error]),
            {error, Error};
        throw:Throw ->
            io:format("The function has thrown a throw error: ~p~n",[Throw]),
            {error, Throw};
        _:AnyOtherException -> % sink case (not recommended for exceptions, you should only handle the exceptions you are aware of)
            io:format("The function has thrown an error of any other type: ~p~n",[AnyOtherException]),
            {any_other_exception, AnyOtherException}
    after
        io:format("This is similar (but not equivalent) to a finally block in Java's try-catch-finally~n")
    end.


%% catch function
    
catcher(F) ->
    case catch(F()) of
        {'EXIT', Reason} ->
            Reason;
        _ -> ok
    end.


%%%%%%%%%%%%%%
%% No loops %%
%%%%%%%%%%%%%%

%% standard recursion

factorial(0) ->
    1;
factorial(N) -> 
    N*factorial(N-1). 


%% tail-recursion

factorial_tail(N) ->
    factorial_tail(N,1).

factorial_tail(0, Acc) ->
    Acc;
factorial_tail(N, Acc) -> 
    factorial_tail(N-1,Acc*N). 
