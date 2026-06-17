-module(account).
-export([start/2, init/2]).

% State holds balance and owning bank's pid
-record(state, {balance = 0, bank_pid}).

start(InitialBalance, BankPid) -> 
  spawn(?MODULE, init, [InitialBalance, BankPid]).

init(InitialBalance, BankPid) ->
    % Start loop with initial state
  loop(#state{balance = InitialBalance, bank_pid = BankPid}).

% The main loop
loop(State) ->
  %Block until we receive a message, which is either:
  % Deposit/withdrawal if minus
  % Print balance
  % Get balance
  receive 
    {deposit, Amount, SenderPid} ->
      NewBalance = handle_deposit(Amount, State, SenderPid),
      % Continue loop with updated balance
      loop(State#state{balance = NewBalance});

    print_balance ->
      io:format("Account balance: ~p~n", [State#state.balance]),
      loop(State);

    {get_balance, From} -> 
      % Send balance to requester
      From ! {balance_response, State#state.balance},
      loop(State)
  end.

% Handle deposit/withdrawal requests
% This handles withdrawls. That means negative amounts
handle_deposit(Amount, State, SenderPid) when Amount < 0 ->
  % Check if sender is owner bank (challenging requirement)
  case SenderPid =:= State#state.bank_pid of
    % If matching bank, proceed, if we don't overdraw
    true -> 
      NewBalance = State#state.balance + Amount,
      case NewBalance >= 0 of
        true ->
          SenderPid ! {deposit_ok, Amount},
          NewBalance;
        %If we do overdraw, then reject.
        false -> 
          SenderPid ! {deposit_rejected, Amount},
          %Keep old balance.
          State#state.balance
      end;
    %If mismatching bank, reject.
    false ->
      SenderPid ! {deposit_rejected, Amount},
      State#state.balance
  end;
%This handles deposits. That means positive amounts
handle_deposit(Amount, State, SenderPid) -> 
  NewBalance = State#state.balance + Amount,
  SenderPid ! {deposit_ok, Amount},
  NewBalance.