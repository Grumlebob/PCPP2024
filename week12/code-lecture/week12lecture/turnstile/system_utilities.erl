% raup@itu.dk * 2024-11-17

-module(system_utilities).
-export([example_execution/1]).

example_execution(N) ->
    % start actors
    %% NOTE: You can call functions from other modules in the same directory
    %%       by prefixing the module name and colon, i.e., 
    %%       module_name:function(...) such as counter:start()
    CounterPID = counter:start(),
    Turnstile1 = turnstile:start(CounterPID),
    Turnstile2 = turnstile:start(CounterPID),
    
    % List of people crossing each turnstile (identified by an integer)
    People = lists:seq(1,N),
    % We create a list of tuples {TurnstilePID, PersonID} for the two turnstiles
    T1People = lists:zip(lists:duplicate(N,Turnstile1), People),
    T2People = lists:zip(lists:duplicate(N,Turnstile2), People),
    % This is an anonymous function which sends the person_crossing message
    % to the corresponding turnstile actor
    Send = 
        fun ({PID,_}) -> 
            PID ! person_crossing
        end,
    % We iterate both lists an execute the Send function for each element
    lists:foreach(Send, T1People),
    lists:foreach(Send, T2People),
    % Finally, we contact directly counter actor to print the total number
    % of people who crossed both tunstiles
    CounterPID ! print_total.
