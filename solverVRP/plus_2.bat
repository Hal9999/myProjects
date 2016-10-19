@echo off
del /Q output\solutions.csv

java -Dnumber=12 -jar solverVRP.jar -i C101.txt ;
java -Dnumber=12 -jar solverVRP.jar -i C102.txt ;
java -Dnumber=12 -jar solverVRP.jar -i C103.txt ;
java -Dnumber=12 -jar solverVRP.jar -i C104.txt ;
java -Dnumber=12 -jar solverVRP.jar -i C105.txt ;
java -Dnumber=12 -jar solverVRP.jar -i C106.txt ;
java -Dnumber=12 -jar solverVRP.jar -i C107.txt ;
java -Dnumber=12 -jar solverVRP.jar -i C108.txt ;
java -Dnumber=12 -jar solverVRP.jar -i C109.txt ;
java -Dnumber=5 -jar solverVRP.jar -i C201.txt ;
java -Dnumber=5 -jar solverVRP.jar -i C202.txt ;
java -Dnumber=5 -jar solverVRP.jar -i C203.txt ;
java -Dnumber=5 -jar solverVRP.jar -i C204.txt ;
java -Dnumber=5 -jar solverVRP.jar -i C205.txt ;
java -Dnumber=5 -jar solverVRP.jar -i C206.txt ;
java -Dnumber=5 -jar solverVRP.jar -i C207.txt ;
java -Dnumber=5 -jar solverVRP.jar -i C208.txt ;
java -Dnumber=9 -jar solverVRP.jar -i RC201.txt ;
java -Dnumber=9 -jar solverVRP.jar -i RC202.txt ;
java -Dnumber=6 -jar solverVRP.jar -i RC203.txt ;
java -Dnumber=6 -jar solverVRP.jar -i RC204.txt ;
java -Dnumber=9 -jar solverVRP.jar -i RC205.txt ;
java -Dnumber=7 -jar solverVRP.jar -i RC206.txt ;
java -Dnumber=7 -jar solverVRP.jar -i RC207.txt ;
java -Dnumber=6 -jar solverVRP.jar -i RC208.txt ;

