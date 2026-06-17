-module(time_formatter).

-export([time/1]).

time(Seconds) ->
    Hours = Seconds div 3600,
    RemainingSecs = Seconds rem 3600,
    Minutes = RemainingSecs div 60,
    FinalSeconds = RemainingSecs rem 60,
    io:format('Time in format HH:MM:SS\n'),
    io:format('~p:~p:~p~n', [Hours, Minutes, FinalSeconds]).
