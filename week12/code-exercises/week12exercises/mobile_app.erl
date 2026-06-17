-module(mobile_app).
-export([start/1, init/1, send_N_requests/4]).

% State holds reference to associated bank
-record(state, {bank_pid}).

start(BankPid) ->
  spawn(?MODULE, init, [BankPid]).

init(BankPid) ->
  loop(#state{bank_pid = BankPid}).

% Main loop
loop(State) ->
  receive % Blocks if there are no messages
    %Args explained:
    %payment_request: Atom. The message type
    %PayerAccount: Pid. The account to withdraw from
    %PayeeAccount: Pid. The account to deposit to
    %Amount: Integer. The amount to transfer
    {payment_request, PayerAccount, PayeeAccount, Amount} ->
      State#state.bank_pid ! {transaction, self(), PayerAccount, PayeeAccount, Amount},
      %Block, waiting for banks response. Which is either "ok" or "failed"
      receive
        {transaction_ok, Amount} ->
          io:format("Payment of ~p completed successfully~n", [Amount]);
        {transaction_failed, Amount} ->
          io:format("Payment of ~p failed~n", [Amount])
      end;
    %Some unexpected error
    Other ->
      io:format("Mobile app received unexpected message: ~p~n", [Other])
  end,
  loop(State).

%Exercise 5: Function to send multiple payment requests

% Entry point for multiple requests
send_N_requests(N, PayerAccount, PayeeAccount, MobileApp) -> 
  %Uses helper function to send N requests
  send_N_requests_helper(N, PayerAccount, PayeeAccount, MobileApp, 1).  

%Base case, is that N is 0, then we are done
send_N_requests_helper(0, _, _, _, _) ->
  ok;
%In case N is not 0, we send a request and recurse with N-1
send_N_requests_helper(N, PayerAccount, PayeeAccount, MobileApp, Amount) ->
  % Send one request
  MobileApp ! {payment_request, PayerAccount, PayeeAccount, Amount},
  % Recurse with N-1
  send_N_requests_helper(N-1, PayerAccount, PayeeAccount, MobileApp, Amount).