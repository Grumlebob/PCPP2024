% raup@itu.dk * 2024-11-19

-module(worker).
-export([start/1, init/1, is_prime_naive/1]).

% State
-record(worker_state, {primer_server}).

% Start
start(PrimerPID) ->
    spawn(?MODULE, init, [PrimerPID]).

% Initialization 
init(PrimerPID) ->
    State = #worker_state{primer_server=PrimerPID},
    loop(State).
    

% Behavior upon receiving messages
loop(State) ->
    receive
        {is_prime, Number} ->
            handle_is_prime(Number, State);

        %%% Only for benchmarking
        {is_prime_benchmarking, Number, PID} ->
            handle_is_prime_benchmarking(Number, PID, State)
    end.

% 5. Message handlers

handle_is_prime(Number, State = #worker_state{primer_server=PrimerPID}) ->
    PrimerPID ! {prime_result, Number, is_prime_naive(Number)},
    loop(State).






% Private functions (not very relevant for understanding the lecture example)

%% Naive method to compute if a number is prime (primality test)
%%% This implementation is clearly suboptimal, there exist much better primality test methods (see, e.g., https://en.wikipedia.org/wiki/Primality_test). However, a slow implementation, such as this one, will illustrates some performance benefits of using concurrency.

%%% 0 and 1 are not prime by definition
is_prime_naive(N) when N >= 0, N < 2 ->
    false;
%%% All even numbers are trivially not prime, as they are divisible by 2
is_prime_naive(N) when N > 2, N rem 2 == 0 ->
    false;
%%% We check whether N is divisible by any odd number from 3 up to N-1
is_prime_naive(N) when N > 1 ->
    is_prime_naive(N, 3);
%%% We only consider positive numbers and 0
is_prime_naive(N) when N < 0 ->
    error({badarg, "Please input a natural number or zero"}).

%% Helper function to check primality of odd numbers from 3 to N-2
is_prime_naive(N, PossibleDivisor) when PossibleDivisor < N, N rem PossibleDivisor /= 0 ->
    is_prime_naive(N, PossibleDivisor + 2);
is_prime_naive(N, PossibleDivisor) when PossibleDivisor < N, N rem PossibleDivisor == 0 ->
    false;
is_prime_naive(_, _) ->
    true.





%%% Only for bencmarking (not relevant for the lecture example)
handle_is_prime_benchmarking(Number,PID,
                             State = #worker_state{primer_server=PrimerPID}) ->
    PrimerPID ! {prime_result_benchmarking, Number, PID, is_prime_naive(Number)},
    loop(State).
