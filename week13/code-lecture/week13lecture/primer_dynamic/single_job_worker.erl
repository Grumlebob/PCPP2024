% raup@itu.dk * 2024-11-20

-module(single_job_worker).
-export([start/0,no_loop/0]).

% 1. State (empty)

% 2. Start
start() ->
    spawn(?MODULE, no_loop, []).

% 3. No need for init function in this actor

% 4. Behavior upon receiving messages
% Note that we do not loop, the process finishes execution
% after computing the primality test
no_loop() ->
    receive
        {check_prime, Number, PrimerPID} ->
            PrimerPID ! {prime_result, Number, is_prime_naive(Number)}
    end.











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
