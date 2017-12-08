# ADB-project
This repo is for RepCRec project in the class of Advanced Database 

## For test files:
Test1 to Test19 are the tests provided by teacher.
We add 12 test cases more from Test20 to Test26.

## Output:
During the run, the project is going to pay close attention to cycle and print out the abortion of
transaction once it encounters a cycle.
Also, it prints out the output of the end, and dump instructions.

Finally, it prints out the instructions and result of R instruction sorted per transaction.
For read instructions, we print the value as long as it could get the read lock.

## How to run our project:
The command looks like the follows:
java -jar ADB-project.jar /path/to/input/file

The output is sent to standard out.
