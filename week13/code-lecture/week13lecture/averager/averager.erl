% raup@itu.dk * 2024-11-21

% This is just an API module to call the scatter/gather averager
% program.

% The only subtlety is that we wait for the gather message from the
% top most gatherer, as it contains the final result.

-module(averager).
-export[average/1, non_parallel_average/1].

average(List) ->
    PID = scatterer:start(self()),
    PID ! {scatter, List, length(List)},
    receive
        {gather, Result} ->
            Result
    end.

non_parallel_average(List) ->
    lists:foldl(fun (X,Y) -> X+Y end, 0, List)/length(List).
