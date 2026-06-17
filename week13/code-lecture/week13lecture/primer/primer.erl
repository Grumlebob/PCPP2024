% raup@itu.dk * 2024-11-19

-module(primer).
-export([start/1, init/1, check_prime/2, check_prime_benchmarking/3,
         check_primes/3, check_primes_benchmarking/3]).

%% 1. State
%%% num_printed` is added for debugging purposes only
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
            handle_prime_result(Number, IsPrime, State);

        %%% Only for benchmarking
        {check_prime_benchmarking, Number, PID} ->
            handle_check_prime_benchmarking(Number, PID, State);
        {prime_result_benchmarking, Number, PID, IsPrime} ->
            handle_prime_result_benchmarking(Number, IsPrime, PID, State)
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









%%% Only for benchmarking (not relevant for the lecture example)

check_prime_benchmarking(PrimerPID, Number, PID) ->
    PrimerPID ! {check_prime_benchmarking, Number, PID}.


handle_prime_result_benchmarking(Number, IsPrime, PID, State) ->
    PID ! {Number, IsPrime},
    loop(State).


handle_check_prime_benchmarking(Number, PID,
                                State = #primer_state{workers = Workers}) ->
    WorkerPID = maps:get(Number rem maps:size(Workers), Workers),
    WorkerPID ! {is_prime_benchmarking, Number, PID},
    loop(State).


check_primes_benchmarking(N, NumWorkers, MaxNumber) ->
    _ = rand:seed(default, 20241124),
    PID = primer:start(NumWorkers),
    Result = lists:map(fun (X) -> 
                               RandomNumber = rand:uniform(MaxNumber),
                               primer:check_prime_benchmarking(PID, RandomNumber, self()),
                               {X, RandomNumber}
                       end,
                       lists:seq(1,N)),
    await_all_replies(N),
    Result.

await_all_replies(0) ->
    ok;
await_all_replies(N) ->
    receive 
        _ ->
            ok
    end,
    await_all_replies(N-1).
