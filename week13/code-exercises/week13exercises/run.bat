@echo off

echo "Running..."

call erl -make

call erl -noshell -s tester test_all -s init stop

pause
