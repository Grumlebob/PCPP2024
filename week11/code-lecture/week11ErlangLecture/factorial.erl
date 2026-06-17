% raup@itu.dk * 2024-10-22

% This file is simply a very verbose explanation of each program statement of the very simple factorial program; implemented with regular recursion.


% This command defines the name of the module "factorial".
% The name of the module must match the name of the file. That is, the file for this module must be named "factorial.erl"
% Note the "." at the end of the program statement. Recall, in Erlang, program statements and expressions are always terminated with a ".".
-module(factorial). 

% This command defines the functions of the module that can be called from outside the module, e.g., from the interpreter or other modules.
% The -export command takes as input a list of elements <functiona_name>/<number_of_parameters>.
% 
-export([factorial/1]).

% This module defines a recursive implementation of the factorial function.
% Recall that the factorial of an integer N equals 1*..*N-2*N-1 (Eq. 1).

% Note that the definition of the function is composed by two cases: factorial(0) and factorial(N)---remember that 
% functional programming languages do without loops and so does Erlang.
% The Erlang interpret will match the input to the function from top to bottom.
% First it will check whether the input is 0, and, if so, execution the case factorial(0). If not, it will proceed with the next case factorial(N).

% As expected, factorial of 0 equals 1. The "->" symbol indicates the beginning of the definition of the function case.
% Note that the function definition finishes with ";", this is because the function is not completely defined yet. There are more cases to be defined below.
% In Erlang, functions return the result of evaluating the last expression in the function. In this case, the functional only contains one expression, 
% which corresponds is the constant "1". Consequently, the result of executing this case returns 1.
factorial(0) -> 
    1;

% The factorial of a natural number N>0 is defined (not surprisingly) as N*factorial(N-1).
% This recursive definition generates the series of multiplications we mentioned in (Eq. 1) above.
% Since this is the last case of the factorial function, note that the function definition finishes with a "." (as usual).
% As before, this function will return the result of evaluating the expression N*factorial(N-1).
% To evaluate the multiplication "*", it is needed to evaluate both operands (N) and (facotiral(N-1)) and multiply the result.
% Evaluating N is trivial as it is (or should be) a natural number greater than 0. 
% Evaluating factorial(N-1) produces a recursive call to the function.
% This will be repeated until recursive calls reach the case factorial(0), which returns 1.
% For instance, factorial(3) = 3*factorial(2), factorial(2)=2*factorial(1), factorial(1) = 1*factorial(0).
% Putting it all together, we have factorial(3) = 3*2*1*1 as required.
factorial(N) -> 
    N*factorial(N-1).
