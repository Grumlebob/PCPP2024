-module(module_a).
-export([start/0]).
-import(module_b, [sum/2]).

start() ->
	A = 5,
	B = 2,
	sum(A, B).
