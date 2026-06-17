% raup@itu.dk * 2024-11-20

-module(primer).
-export([start/1, init/1, check_prime/2, check_primes/3]).

%% 1. State
-record(primer_state, {workers, num_to_print}).

%% 2. Start functions
start(NumWorkers) ->
    spawn(?MODULE, init, [NumWorkers]).


%% 3. Initialization functions
init(NumWorkers) ->    
    Workers = lists:map(fun (X) -> {X, worker:start(self())} end, 
              lists:seq(0, NumWorkers-1)),
    State = #primer_state{workers=maps:from_list(Workers), num_to_print=1},
    loop(State).


%% 4. Loop function
loop(State) ->
    receive 
        {check_prime, Number} ->
            handle_check_prime(Number, State);
        {prime_result, Number, IsPrime} ->
            handle_prime_result(Number, IsPrime, State)
    end.


%% 5. Message handlers
handle_check_prime(Number, State = #primer_state{workers = Workers}) ->
    WorkerPID = maps:get(Number rem maps:size(Workers), Workers),
    WorkerPID ! {is_prime, Number},
    loop(State).

handle_prime_result(Number, IsPrime, State = #primer_state{num_to_print = NumToPrint}) ->
    case IsPrime of
        true ->
            io:format("The number ~p is prime [~p]~n", [Number, NumToPrint]);
        false ->
            io:format("The number ~p is not prime [~p]~n", [Number, NumToPrint])
    end,
    loop(State#primer_state{num_to_print = NumToPrint+1}).


%% 6. API

check_prime(PrimerPID, Number) ->
    PrimerPID ! {check_prime, Number}.



%% Functions for the lecture

check_primes(N, NumWorkers, MaxNumber) ->
    _ = rand:seed(default, 20241124),
    PID = primer:start(NumWorkers),
    lists:map(fun (X) -> 
                      RandomNumber = rand:uniform(MaxNumber),
                      primer:check_prime(PID, RandomNumber),
                      {X, RandomNumber}
                  end,
                  lists:seq(1,N)).
