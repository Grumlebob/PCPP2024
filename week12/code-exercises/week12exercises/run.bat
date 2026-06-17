@echo off

echo "Running..."

call erl -make

call erl -noshell -s system_starter run_all_tests -s init stop

pause
