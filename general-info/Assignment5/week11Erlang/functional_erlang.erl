-module(functional_erlang).
-export([remove/2, remove_comp/2, count/2, count_occurrences/2, count_occurrences_comp/2,
  filter/2, filter_comp/2, flatten/1, fold/3, what_is_the_temperature/3, is_satisfied/2]).

%% Record definitions - using 'conj' instead of 'and' for conjunction
-record(prop, {var}).
-record(conj, {left, right}).  % Changed from 'and' to 'conj'
-record(not_op, {expr}).       % Changed from 'not' to 'not_op'

%% Remove all occurrences of Elem from List (without list comprehension)
remove(_, []) -> [];
remove(Elem, [Elem|Tail]) -> remove(Elem, Tail);
remove(Elem, [Head|Tail]) -> [Head|remove(Elem, Tail)].

%% Remove with list comprehension
remove_comp(Elem, List) -> [X || X <- List, X /= Elem].

%% Count occurrences of Elem in List
count(_, []) -> 0;
count(Elem, [Elem|Tail]) -> 1 + count(Elem, Tail);
count(Elem, [_|Tail]) -> count(Elem, Tail).

%% Count occurrences satisfying a predicate (without list comprehension)
count_occurrences(_, []) -> 0;
count_occurrences(Pred, [Head|Tail]) ->
  case Pred(Head) of
    true -> 1 + count_occurrences(Pred, Tail);
    false -> count_occurrences(Pred, Tail)
  end.

%% Count occurrences with list comprehension
count_occurrences_comp(Pred, List) ->
  length([X || X <- List, Pred(X)]).

%% Filter elements that satisfy a predicate (without list comprehension)
filter(_, []) -> [];
filter(Pred, [Head|Tail]) ->
  case Pred(Head) of
    true -> [Head|filter(Pred, Tail)];
    false -> filter(Pred, Tail)
  end.

%% Filter with list comprehension
filter_comp(Pred, List) -> [X || X <- List, Pred(X)].

%% Flatten a list of lists
flatten([]) -> [];
flatten([Head|Tail]) when is_list(Head) ->
  flatten(Head) ++ flatten(Tail);
flatten([Head|Tail]) -> [Head|flatten(Tail)].

%% Fold with accumulator (left-to-right)
fold([], _, Acc) -> Acc;
fold([Head|Tail], Fun, Acc) ->
  fold(Tail, Fun, Fun(Acc, Head)).

%% Temperature display function
what_is_the_temperature(Temperature, Scale, City) ->
  io:format("It is ~p degrees ~s in ~s~n", [Temperature, Scale, City]).

%% Propositional logic satisfaction checker
is_satisfied(#prop{var = Var}, Model) ->
  lists:member(Var, Model);
is_satisfied(#conj{left = L, right = R}, Model) ->
  is_satisfied(L, Model) andalso is_satisfied(R, Model);
is_satisfied(#not_op{expr = Expr}, Model) ->
  not is_satisfied(Expr, Model).