-module(demo).
-export([start/0,hello/1]).

hello(Count) ->
	receive
		{name, Name} -> 
			io:fwrite(Name ++ "\n"),
			hello(Count + 1)
	end.

start() ->
	Hello = spawn(demo, hello, [0]),
	Hello ! {name, "Jane Doe"},
	Hello ! {name, "John Doe"}.
