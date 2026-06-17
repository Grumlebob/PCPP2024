-module(functional_erlang_tests).
-export([run_all_tests/0]).

%% Include the same record definitions as in functional_erlang.erl
-record(prop, {var}).
-record(conj, {left, right}).
-record(not_op, {expr}).

run_all_tests() ->
  io:format("~n=== Running all tests ===~n~n"),
  test_remove(),
  test_count(),
  test_count_occurrences(),
  test_filter(),
  test_flatten(),
  test_fold(),
  test_temperature(),
  test_propositional_logic(),
  io:format("~n=== All tests completed ===~n").

%% Test remove function
test_remove() ->
  io:format("Testing remove functions...~n"),
  List1 = [1,2,3,1,4,1,5],
  Result1 = functional_erlang:remove(1, List1),
  io:format("Remove 1 from ~p: ~p~n", [List1, Result1]),

  List2 = ["apple", "banana", "apple", "cherry", "apple"],
  Result2 = functional_erlang:remove("apple", List2),
  io:format("Remove 'apple' from ~p: ~p~n", [List2, Result2]),

  % Test with list comprehension version
  Result3 = functional_erlang:remove_comp(1, List1),
  io:format("Remove 1 (comprehension) from ~p: ~p~n~n", [List1, Result3]).

%% Test count function
test_count() ->
  io:format("Testing count function...~n"),
  List = [1,2,3,1,4,1,5],
  Count = functional_erlang:count(1, List),
  io:format("Count of 1 in ~p: ~p~n~n", [List, Count]).

%% Test count_occurrences function
test_count_occurrences() ->
  io:format("Testing count_occurrences functions...~n"),
  List = [1,2,3,4,5,6,7,8,9,10],
  IsEven = fun(X) -> X rem 2 =:= 0 end,
  Count1 = functional_erlang:count_occurrences(IsEven, List),
  io:format("Count of even numbers in ~p: ~p~n", [List, Count1]),

  % Test with list comprehension version
  Count2 = functional_erlang:count_occurrences_comp(IsEven, List),
  io:format("Count of even numbers (comprehension) in ~p: ~p~n~n", [List, Count2]).

%% Test filter function
test_filter() ->
  io:format("Testing filter functions...~n"),
  List = [1,2,3,4,5,6,7,8,9,10],
  IsEven = fun(X) -> X rem 2 =:= 0 end,
  Filtered1 = functional_erlang:filter(IsEven, List),
  io:format("Filter even numbers from ~p: ~p~n", [List, Filtered1]),

  % Test with list comprehension version
  Filtered2 = functional_erlang:filter_comp(IsEven, List),
  io:format("Filter even numbers (comprehension) from ~p: ~p~n~n", [List, Filtered2]).

%% Test flatten function
test_flatten() ->
  io:format("Testing flatten function...~n"),
  NestedList = [[1,2,3], [4,[5,6]], [7,8,[9,10]]],
  Flattened = functional_erlang:flatten(NestedList),
  io:format("Flatten ~p: ~p~n~n", [NestedList, Flattened]).

%% Test fold function
test_fold() ->
  io:format("Testing fold function...~n"),
  List = [1,2,3,4,5],
  Sum = functional_erlang:fold(List, fun(Acc, X) -> Acc + X end, 0),
  io:format("Sum of ~p using fold: ~p~n", [List, Sum]),

  Product = functional_erlang:fold(List, fun(Acc, X) -> Acc * X end, 1),
  io:format("Product of ~p using fold: ~p~n~n", [List, Product]).

%% Test temperature function
test_temperature() ->
  io:format("Testing temperature function...~n"),
  functional_erlang:what_is_the_temperature(25, "Celsius", "Copenhagen"),
  functional_erlang:what_is_the_temperature(77, "Fahrenheit", "New York"),
  io:format("~n").

%% Test propositional logic
test_propositional_logic() ->
  io:format("Testing propositional logic...~n"),
  P = #prop{var=p},
  Q = #prop{var=q},
  NotP = #not_op{expr=P},
  PandQ = #conj{left=P, right=Q},
  NotPandQ = #conj{left=NotP, right=Q},

  Model1 = [p,q],
  Model2 = [p],
  Model3 = [q],
  Model4 = [],

  io:format("Model [p,q]:~n"),
  io:format("P: ~p~n", [functional_erlang:is_satisfied(P, Model1)]),
  io:format("Q: ~p~n", [functional_erlang:is_satisfied(Q, Model1)]),
  io:format("not P: ~p~n", [functional_erlang:is_satisfied(NotP, Model1)]),
  io:format("P and Q: ~p~n", [functional_erlang:is_satisfied(PandQ, Model1)]),
  io:format("(not P) and Q: ~p~n~n", [functional_erlang:is_satisfied(NotPandQ, Model1)]),

  io:format("Model [p]:~n"),
  io:format("P and Q: ~p~n", [functional_erlang:is_satisfied(PandQ, Model2)]),
  io:format("(not P) and Q: ~p~n~n", [functional_erlang:is_satisfied(NotPandQ, Model2)]),

  io:format("Model [q]:~n"),
  io:format("P and Q: ~p~n", [functional_erlang:is_satisfied(PandQ, Model3)]),
  io:format("(not P) and Q: ~p~n~n", [functional_erlang:is_satisfied(NotPandQ, Model3)]),

  io:format("Model []:~n"),
  io:format("P and Q: ~p~n", [functional_erlang:is_satisfied(PandQ, Model4)]),
  io:format("(not P) and Q: ~p~n~n", [functional_erlang:is_satisfied(NotPandQ, Model4)]).