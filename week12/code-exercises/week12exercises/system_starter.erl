-module(system_starter).
-export([test_successful_case/0, test_security_restriction/0, test_concurrent_payments/0, run_all_tests/0]).

%This tests exercise 1-7
test_successful_case() ->
    Bank1 = bank:start(),
    Account1 = account:start(1000, Bank1),
    Account2 = account:start(1000, Bank1),
    MobileApp1 = mobile_app:start(Bank1),
    MobileApp2 = mobile_app:start(Bank1),
    
    io:format("~nTEST 1: Valid transactions (same bank)~n"),
    io:format("Initial balances:~n"),
    Account1 ! print_balance,
    timer:sleep(100),   
    Account2 ! print_balance,
    timer:sleep(100),   
    
    io:format("~nSending payment of 100 from Account1 to Account2...~n"),
    MobileApp1 ! {payment_request, Account1, Account2, 100},
    timer:sleep(200),    
    
    io:format("~nSending payment of 50 from Account2 to Account1...~n"),
    MobileApp2 ! {payment_request, Account2, Account1, 50},
    timer:sleep(200),    
    
    io:format("~nFinal balances:~n"),
    Account1 ! print_balance,
    timer:sleep(100),   
    Account2 ! print_balance,
    timer:sleep(100).   

%This tests exercise. 8
test_security_restriction() ->
    Bank1 = bank:start(),
    Bank2 = bank:start(),
    Account1 = account:start(1000, Bank1),
    Account2 = account:start(1000, Bank1),
    MobileApp1 = mobile_app:start(Bank1),
    MobileApp2 = mobile_app:start(Bank2),
    
    io:format("~nTEST 2: Security restriction (different banks)~n"),
    io:format("Initial balances:~n"),
    Account1 ! print_balance,
    timer:sleep(100),   
    Account2 ! print_balance,
    timer:sleep(100),   
    
    io:format("~nSending payment of 100 from Account1 to Account2 (should work)...~n"),
    MobileApp1 ! {payment_request, Account1, Account2, 100},
    timer:sleep(200),    
    
    io:format("~nTrying payment of 50 from Account2 to Account1 (should fail)...~n"),
    io:format("(This should fail because MobileApp2 uses Bank2 but Account2 belongs to Bank1)~n"),
    MobileApp2 ! {payment_request, Account2, Account1, 50},
    timer:sleep(200),    
    
    io:format("~nFinal balances:~n"),
    Account1 ! print_balance,
    timer:sleep(100),   
    Account2 ! print_balance,
    timer:sleep(100).   

%This tests exercise 5-6
test_concurrent_payments() ->
    Bank1 = bank:start(),
    Account1 = account:start(1000, Bank1),
    Account2 = account:start(1000, Bank1),
    MobileApp1 = mobile_app:start(Bank1),
    
    io:format("~nTEST 3: Concurrent payments~n"),
    io:format("Initial balances:~n"),
    Account1 ! print_balance,
    timer:sleep(100),   
    Account2 ! print_balance,
    timer:sleep(100),   
    
    NumberOfPayments = 100,
    PaymentAmount = 1,
    
    io:format("~nSending ~p concurrent payments of ~p DKK each from Account1 to Account2...~n", 
              [NumberOfPayments, PaymentAmount]),
              
    mobile_app:send_N_requests(NumberOfPayments, Account1, Account2, MobileApp1),
    timer:sleep(2000),
    
    io:format("~nFinal balances:~n"),
    Account1 ! print_balance,
    timer:sleep(100),   
    Account2 ! print_balance,
    timer:sleep(100),   
    
    io:format("~nExpected changes:~n"),
    io:format("Account1 should decrease by: ~p~n", [NumberOfPayments * PaymentAmount]),
    io:format("Account2 should increase by: ~p~n", [NumberOfPayments * PaymentAmount]).

run_all_tests() ->
    io:format("Running all tests...~n"),
    test_successful_case(),
    timer:sleep(500),
    test_security_restriction(),
    timer:sleep(500),
    test_concurrent_payments().