-module(bank).
-export([start/0, init/0]).

start() -> 
  spawn(?MODULE, init, []).

init() ->
  loop().

% Our main loop
loop() -> 
  %Block till we get a message
  receive
    %Args explained:
    %transaction: Atom. The message type
    %From: Pid. The pid of the mobile app
    %PayerAccount: Pid. The account to withdraw from
    %PayeeAccount: Pid. The account to deposit to
    %Amount: Integer. The amount to transfer

    {transaction, From, PayerAccount, PayeeAccount, Amount} ->
      WithdrawAmount = -Amount,
      % Send withdrawal request to payer account
      PayerAccount ! {deposit, WithdrawAmount, self()},
      %And now we wait for response from payer account
      receive
        %If withdrawal was successful
        {deposit_ok, ReceivedAmount} when ReceivedAmount == WithdrawAmount ->
          % Then we deposit to payee account
          PayeeAccount ! {deposit, Amount, self()},
          %And wait for response from payee account. Either Ok or Rejected
          receive
            {deposit_ok, Amount} -> 
              % notifiy the mobile app that the transaction was successful
              From ! {transaction_ok, Amount};

            {deposit_rejected, Amount} -> 
              %If the deposit was rejected, we return the money to the payer
              PayerAccount ! {deposit, Amount, self()},
              %And notify the mobile app that the transaction failed
              From ! {transaction_failed, Amount}
          end;
        % If withdrawal is rejected we notify the mobile app of the failure
        {deposit_rejected, ReceivedAmount} when ReceivedAmount == WithdrawAmount -> 
          From ! {transaction_failed, Amount}
      end;
    %Unexpected message
    Other ->
      io:format("Bank received unexpected message: ~p~n", [Other])
  end,
  loop().