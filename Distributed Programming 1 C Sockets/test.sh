#!/bin/bash
SOURCE_DIRECTORY="source"
GCC_OUTPUT="gcc_output.txt"
TOOLS_DIR="tools"
PORTFINDER_DIR="portfinder"
PORTFINDER_EXEC="port_finder"
SERVER1="socket_server1"
SERVER2="socket_server2"
CLIENT="socket_client"
TESTCLIENT="test_client"
TESTCLIENT_DIR="testclient"
TESTSERVER="test_server"
TESTSERVER_DIR="testserver"
SMALL_TEST_FILE="small_file.txt"
BIG_TEST_FILE="big_file.txt"

# Command to avoid infinite loops
TIMEOUT="timeout"

# Maximum allowed running time of a standard client (to avoid infinite loops)
MAX_EXEC_TIME=10

#*******************************KILL PROCESSES**********************************
function killProcesses
{
	# Kill running servers
	for f in `ps -ef | grep $SERVER1 | awk '{print $2}'`; do kill -9 $f &> /dev/null; done
	for f in `ps -ef | grep $SERVER2 | awk '{print $2}'`; do kill -9 $f &> /dev/null; done
	for f in `ps -ef | grep $TESTSERVER | awk '{print $2}'`; do kill -9 $f &> /dev/null; done

	# Kill running clients
	for f in `ps -ef | grep $CLIENT | awk '{print $2}'`; do kill -9 $f 2>&1 &> /dev/null; done
	for f in `ps -ef | grep $TESTCLIENT | awk '{print $2}'`; do kill -9 $f 2>&1 &> /dev/null; done
}

#*******************************COUNT PROCESSES**********************************
# First argument: the pattern of the process to be counted
# Return value: the number of processes that include the pattern
function countProcesses
{
	# decrement by one not to count grep process itself
	echo $((`ps -ef | grep $1 | wc -l` - 1))
}

#**********************************CLEANUP***************************************
function cleanup
{
	echo -n "Cleaning up..."
	killProcesses
	# Delete previously generated folders and files (if they exist)
	rm -r -f temp*  2>&1 &> /dev/null
	rm -r -f client_temp_dir* 2>&1 &> /dev/null
	rm -f $TOOLS_DIR/$PORTFINDER_EXEC 2>&1 &> /dev/null
	rm -f $SOURCE_DIRECTORY/$CLIENT 2>&1 &> /dev/null
	rm -f $SOURCE_DIRECTORY/$SERVER1 2>&1 &> /dev/null
	rm -f $SOURCE_DIRECTORY/$SERVER2 2>&1 &> /dev/null
	echo -e "\t\t\tOk!"
}

#***************************COMPILING TESTING TOOLS******************************
function compileTools
{
	echo -n "Compiling testing tools..."

	gcc -g -Wno-format-security -o $TOOLS_DIR/$PORTFINDER_EXEC $TOOLS_DIR/$PORTFINDER_DIR/*.c $TOOLS_DIR/*.c 2> /dev/null
	if [ ! -e $TOOLS_DIR/$PORTFINDER_EXEC ] || [ ! -e $TOOLS_DIR/$TESTCLIENT ] || [ ! -e $TOOLS_DIR/$SMALL_TEST_FILE ] || [ ! -e $TOOLS_DIR/$TESTSERVER ] ; then
		echo -e "\tFail!. \n[ERROR] Unable to compile testing tools or missing test files. Test aborted!"
		exit -1
	else
		echo -e "\tOk!"
	fi

}

#********************************COMPILING SOURCES******************************
function compileSource
{
	cd $SOURCE_DIRECTORY
	rm -f $GCC_OUTPUT

	echo -n "Test 0.1 (compiling server1)...";
	gcc -g -o $SERVER1 server1/*.c *.c -Iserver1 -lpthread -lm >> $GCC_OUTPUT 2>&1 
	if [ ! -e $SERVER1 ] ; then
		TEST_01_PASSED=false
		echo -e "\tFail! \n\t[ERROR] Unable to compile source code for server1."
		echo -e "\tGCC log is available in $SOURCE_DIRECTORY/$GCC_OUTPUT"
	else
		TEST_01_PASSED=true
		echo -e "\tOk!"
	fi

	echo -n "Test 0.2 (compiling client)...";
	gcc -g -o $CLIENT client/*.c *.c -Iclient -lpthread -lm >> $GCC_OUTPUT 2>&1 
	if [ ! -e $CLIENT ] ; then
		TEST_02_PASSED=false
		echo -e "\tFail! \n\t[ERROR] Unable to compile source code for the client."
		echo -e "\tGCC log is available in $SOURCE_DIRECTORY/$GCC_OUTPUT"
	else
		TEST_02_PASSED=true
		echo -e "\tOk!"
	fi

	echo -n "Test 0.3 (compiling server2)...";
	gcc -g -DTRACE -o $SERVER2 server2/*.c *.c -Iserver2 -lpthread -lm >> $GCC_OUTPUT 2>&1 
	if [ ! -e $SERVER2 ] ; then
		TEST_03_PASSED=false
		echo -e "\tFail! \n\t[ERROR] Unable to compile source code for server2."
		echo -e "\tGCC log is available in $SOURCE_DIRECTORY/$GCC_OUTPUT"
	else
		TEST_03_PASSED=true
		echo -e "\tOk!"
	fi

	cd ..
}

#************************************RUN SERVER*************************************
# First argument: the server to be run (full pathname)
# Second argument: the file for recording the server standard output and error
# Third argument: extra param server (i.e. num of children to fork)
# Return value: the listening port
function runServer
{
	local FREE_PORT=`./$PORTFINDER_EXEC`				# This will find a free port
	$1 $FREE_PORT $3 &> $2 &								# Launch the server
	ensureServerStarted $FREE_PORT
	echo $FREE_PORT	# Returning to the caller the port on which the server is listening
}

#************************************RUN CLIENT*************************************
# First argument: the client to be run
# Second argument: the address or name of the server
# Third argument: the port number of the server
# Fourth argument: the file name argument
# Fifth argument: the part id
# Sixth argument: the run id
# Eighth argument: -bad code
function runClient
{
	if [[ -n $7 ]] ; then 
		echo -n "Running client $1 (server $2, port $3, file $4, with -bad $7) ..."
		output=$($TIMEOUT $MAX_EXEC_TIME ./$1 "$2" "$3" "$4" -bad "$7")
	else
		echo -n "Running client $1 (server $2, port $3, file $4) ..."
		output=$($TIMEOUT $MAX_EXEC_TIME ./$1 "$2" "$3" "$4")
	fi
	rc=$?

	outputname="testclientoutput$5$6"
	eval ${outputname}="'$output'"
	returnname="testclientreturn$5$6"
	eval ${returnname}="'$rc'"
	if [[ $testStudentClient == false ]] && (( $rc == 0 )) ; then
		echo -e "\t$1 did not receive any response from server $2 running on port $3"
	elif [[ $testStudentClient == false ]] && (( $rc == 1 )) ; then
		echo -e "\tResponse received from server $2 not correctly encoded"
	elif [[ $testStudentClient == false ]] && (( $rc == 2 )) ; then
		echo -e "\tTest client run terminated successfully"
	else
		echo -e "\tClient run terminated"
	fi
}


#************************************ENSURE SERVER STARTED*************************************
# Check a server is listening on the specified port. Wait for at most WAITSEC seconds
# first parameter -> the port to be checked
#
function ensureServerStarted
{
	local WAITSEC=5				# Maximum waiting time in seconds
	# Ensure the server has started
	for (( i=1; i<=$WAITSEC; i++ ))	# Maximum WAITSEC tries
	do
		#fuser $1/tcp &> /dev/null
		rm -f temp
		netstat -ln | grep "tcp" | grep ":$1 " > temp
		if [[ -s temp ]] ; then
			# Server started
			break
		else
			# Server non started
			sleep 1
		fi
	done
	rm -f temp
}

#************************************TEST CLIENT_SERVER INTERACTION*************************************
# first parameter -> server to be used 
# second parameter -> client to be used
# third parameter -> start test number
# fourth parameter -> extra server parameter (i.e. num of children to fork in server2)
#\
function testClientServerInteraction
{
	# Creating temp directories
	rm -r -f temp_dir
	rm -r -f client_temp_dir
	mkdir temp_dir 2>&1 &> /dev/null
	mkdir client_temp_dir 2>&1 &> /dev/null
	
	# Preparing the server directory
	# Copying server file to temporary directory
	cp -f $SOURCE_DIRECTORY/$1 temp_dir 2>&1 &> /dev/null
		
	# Copying test-related files to temporary directory
	cp -f $TOOLS_DIR/$PORTFINDER_EXEC temp_dir 2>&1 &> /dev/null
	cp -f $TOOLS_DIR/$SMALL_TEST_FILE temp_dir 2>&1 &> /dev/null
	cp -f $TOOLS_DIR/$BIG_TEST_FILE temp_dir 2>&1 &> /dev/null
	
	# Preparing the client directory
	testStudentClient=false		# true if testing with the student client	
	if [[ $2 == $CLIENT ]] ; then
		cp -f $SOURCE_DIRECTORY/$2 client_temp_dir 2>&1 &> /dev/null
		testStudentClient=true
	else
		cp -f $TOOLS_DIR/$2 client_temp_dir 2>&1 &> /dev/null
	fi

	testStudentServer=false
	if [[ $1 == $SERVER1 ]] || [[ $1 == $SERVER2 ]] ; then 
		cp -f $SOURCE_DIRECTORY/$1 temp_dir 2>&1 &> /dev/null
		testStudentServer=true
	else
		cp -f $TOOLS_DIR/$1 temp_dir 2>&1 &> /dev/null
	fi

	# Setting start test number
	test_number=$3

	echo -e "\n\n//Checking interaction between server $1 and client $2//"
	
	# Run the server
	echo -n "Running server $1..."
	pushd temp_dir >> /dev/null
		local suffix="output1.txt"
		server_port=$(runServer "./$1" "$1$suffix" "$4")
		echo -e "\t[PORT $server_port] Ok!"
	popd >> /dev/null
	
	# Run client (1 - with small file)
	local fname=$SMALL_TEST_FILE
	pushd client_temp_dir >> /dev/null
		runClient $2 "127.0.0.1" "$server_port" "$fname" "$test_suite" "$client_run"
	popd >> /dev/null
	
	# TEST 1.1 return value of test client
	if [[ $testStudentClient == false ]] ; then 
		retvalname="testclientreturn$test_suite$client_run"
		rc="${!retvalname}"
		echo -e "\n\n[TEST $test_suite.$test_number] Checking result of interaction of test client with server $1..."
		local tname="TEST_$test_suite$test_number"
		local tname+="_PASSED"	

		# If the return code is less than 2 -> skip following tests
		if [[ $rc -lt 2 ]] ; then
			echo -e "\t[--TEST $test_suite.$test_number FAILED--] No correct response from $1. Skipping next tests"
			eval ${tname}=false
			return
		elif (( $rc == 2 )) ; then
			echo -e "\t[++TEST $test_suite.$test_number PASSED++] "
			eval ${tname}=true
		fi
		test_number=$(($test_number + 1))
	fi

	# TEST 1.2 2.1 2.3 response code and file size value prompted by the testclient
	echo -e "\n\n[TEST $test_suite.$test_number] Checking the response code and the file size received by the client..."
	local tname="TEST_$test_suite$test_number"
	local tname+="_PASSED"
	pushd temp_dir >> /dev/null
	SMALL_FILE_SIZE=$(stat -c%s "$fname")
	outputvar="testclientoutput$test_suite$client_run"
	outp_arr=( ${!outputvar} )
    client_run=$(($client_run + 1 ))
	if [[ ${outp_arr[1]} == 2 ]] ; then 
		if [[ ${outp_arr[2]} == $SMALL_FILE_SIZE ]] ; then 
			echo -e "\t[++TEST $test_suite.$test_number PASSED++] Ok!"
			eval ${tname}=true
		else
			echo -e "\t[--TEST $test_suite.$test_number FAILED--] File sizes are not equal!"
			eval ${tname}=false
		fi
	else 
		echo -e "\t[--TEST $test_suite.$test_number FAILED--] Wrong response code!"
		eval ${tname}=false
	fi
	popd >> /dev/null
	test_number=$(($test_number + 1))
	echo
 
	# Run client (2 - with file not present in server side)
	pushd client_temp_dir >> /dev/null
		runClient $2 "127.0.0.1" "$server_port" "other_file.txt" "$test_suite" "$client_run"
	popd >> /dev/null

	if [[ $testStudentClient == false ]] ; then
		# TEST 1.3 return value of test client 
		retvalname="testclientreturn$test_suite$client_run"
		rc="${!retvalname}"
		echo -e "\n\n[TEST $test_suite.$test_number] Checking result of interaction of test client with server $1 with unknown file..."
		local tname="TEST_$test_suite$test_number"
		local tname+="_PASSED"	
		if (( $rc == 2 )); then
			echo -e "\t[++TEST $test_suite.$test_number PASSED++] "
			eval ${tname}=true	
		else 
			echo -e "\t[--TEST $test_suite.$test_number FAILED--] No correctly encoded response received from server"
			eval ${tname}=false
		fi
		test_number=$(($test_number + 1))
	fi

	# TEST 1.4 2.2 2.4 response code of the client
    echo -e "\n\n[TEST $test_suite.$test_number] Checking the response code received by the client with unknown file..."
	local tname="TEST_$test_suite$test_number"
	local tname+="_PASSED"	
	outputvar="testclientoutput$test_suite$client_run"
	outp_arr=( ${!outputvar} )
    client_run=$(($client_run + 1 ))
	if [[ ${outp_arr[1]} == 1 ]] ; then
		echo -e "\t[++TEST $test_suite.$test_number PASSED++] "
		eval ${tname}=true
	else
		echo -e "\t[--TEST $test_suite.$test_number FAILED--] Bad response code (${outp_arr[1]}) received from server"
		eval ${tname}=false
	fi
	test_number=$(($test_number + 1))

	#TEST 2.5 - student client timeout
	if [[ $testStudentServer == false ]] ; then
        # Kill the server so that no response will be received by the client
		killProcesses
		cp -f $SOURCE_DIRECTORY/$CLIENT client_temp_dir 2>&1 &> /dev/null
		echo -e "\n\n[TEST $test_suite.$test_number] Checking client behaviour in case of timeout..."
		pushd client_temp_dir >> /dev/null
			runClient $CLIENT "127.0.0.1" "$server_port" "$fname" "$test_suite" "$client_run"
		popd >> /dev/null

		outputvar="testclientoutput$test_suite$client_run"
		outp_arr=( ${!outputvar} )
		client_run=$(($client_run + 1 ))
		if (( ${outp_arr[1]} == -1 )) ; then
			echo -e "\t[++TEST $test_suite.$test_number PASSED++] "
			eval ${tname}=true	
		else 
			echo -e "\t[--TEST $test_suite.$test_number FAILED--] Wrong output from client"
			eval ${tname}=false
		fi
		#CLEANUP
		rm -r -f temp_dir 2>&1 &> /dev/null
		rm -r -f client_temp_dir 2>&1 &> /dev/null
		killProcesses
		return
	fi

    # Stop test procedure here and cleanup if we are testing the client
	if [[ $testStudentClient == true ]] ; then
		#CLEANUP
		rm -r -f temp_dir 2>&1 &> /dev/null
		rm -r -f client_temp_dir 2>&1 &> /dev/null
		if (( $test_suite != 3 )) ; then 
			killProcesses
		fi
		return
	fi

    # Extra tests for testing server robustness
    if [[ $testStudentClient == false ]] ; then
	echo
        # Run client (3 - with bad character in file name)
        pushd client_temp_dir >> /dev/null
            runClient $2 "127.0.0.1" "$server_port" "$fname" "$test_suite" "$client_run" "1"
        popd >> /dev/null

        #TEST 1.5 check interaction of test client with server when client sends a not allowed character (expected exit code 2)
        retvalname="testclientreturn$test_suite$client_run"
        rc="${!retvalname}"
		echo -e "\n\n[TEST $test_suite.$test_number] Checking result of interaction of test client with server $1 with bad request n.1 (filename including not allowed character)"
		local tname="TEST_$test_suite$test_number"
		local tname+="_PASSED"
		if (( $rc == 2 )) ; then
			echo -e "\t[++TEST $test_suite.$test_number PASSED++] "
			eval ${tname}=true	
		else 
			echo -e "\t[--TEST $test_suite.$test_number FAILED--] No correctly encoded response received from server $1"
			eval ${tname}=false
		fi
		test_number=$(($test_number + 1))

		# TEST 1.6 verify response code of server equals 0 when client sends a not allowed character
		outputvar="testclientoutput$test_suite$client_run"
		outp_arr=( ${!outputvar} )
		client_run=$(($client_run + 1 ))
        echo -e "\n\n[TEST $test_suite.$test_number] Checking output of test client with server $1 with bad request n.1 (filename including not allowed character)"
        local tname="TEST_$test_suite$test_number"
        local tname+="_PASSED"

       	if (( ${outp_arr[1]} == 0 )) ; then
			echo -e "\t[++TEST $test_suite.$test_number PASSED++] "
			eval ${tname}=true
		else
			echo -e "\t[--TEST $test_suite.$test_number FAILED--] Bad response code (${outp_arr[1]}) received from server"
			eval ${tname}=false
		fi
		test_number=$(($test_number + 1))

		echo
		pushd client_temp_dir >> /dev/null
		runClient $2 "127.0.0.1" "$server_port" "$fname" "$test_suite" "$client_run" "2"
		popd >> /dev/null

		 #TEST 1.7 check interaction of test client with server when client sends a request of length > 256
		retvalname="testclientreturn$test_suite$client_run"
		rc="${!retvalname}"
		echo -e "\n\n[TEST $test_suite.$test_number] Checking result of interaction of test client with server $1 with bad request n.2 (request of length > 256)"
        local tname="TEST_$test_suite$test_number"
        local tname+="_PASSED"
        if (( $rc == 2 )) ; then
			echo -e "\t[++TEST $test_suite.$test_number PASSED++] "
			eval ${tname}=true	
		else 
			echo -e "\t[--TEST $test_suite.$test_number FAILED--] No correctly encoded response received from server $1"
			eval ${tname}=false
		fi
		test_number=$(($test_number + 1))

		# TEST 1.8 verify response code of server equals  0  when sending a request of length > 256
		outputvar="testclientoutput$test_suite$client_run"
		outp_arr=( ${!outputvar} )
		client_run=$(($client_run + 1 ))
		echo -e "\n\n[TEST $test_suite.$test_number] Checking output of test client with server $1 with bad request n.2 (request of length > 256)"
        local tname="TEST_$test_suite$test_number"
        local tname+="_PASSED"
        if (( ${outp_arr[1]} == 0 )) ; then
			echo -e "\t[++TEST $test_suite.$test_number PASSED++] "
			eval ${tname}=true
		else
			echo -e "\t[--TEST $test_suite.$test_number FAILED--] Bad response code (${outp_arr[1]}) received from server"
			eval ${tname}=false
		fi
		test_number=$(($test_number + 1))

		echo
		pushd client_temp_dir >> /dev/null
			runClient $2 "127.0.0.1" "$server_port" "$fname" "$test_suite" "$client_run" "3"
		popd >> /dev/null

		#TEST 1.9 check interaction of test client with server when client sends a string including a NULL char
		retvalname="testclientreturn$test_suite$client_run"
		rc="${!retvalname}"

		echo -e "\n\n[TEST $test_suite.$test_number] Checking result of interaction of test client with server $1 with bad request n.3 (NULL character in file name)"
        local tname="TEST_$test_suite$test_number"
        local tname+="_PASSED"

        if (( $rc == 2 )) ; then
			echo -e "\t[++TEST $test_suite.$test_number PASSED++] "
			eval ${tname}=true	
		else 
			echo -e "\t[--TEST $test_suite.$test_number FAILED--] No correctly encoded response received from server $1"
			eval ${tname}=false
		fi
		test_number=$(($test_number + 1))

		# TEST 1.10 verify response code of server equals 0  when sending a string including a NULL char
		outputvar="testclientoutput$test_suite$client_run"
		outp_arr=( ${!outputvar} )
		client_run=$(($client_run + 1 ))
		echo -e "\n\n[TEST $test_suite.$test_number] Checking output of test client with server $1 with bad request n.3 (NULL character in file name)"
        local tname="TEST_$test_suite$test_number"
        local tname+="_PASSED"
        if (( ${outp_arr[1]} == 0 )) ; then
			echo -e "\t[++TEST $test_suite.$test_number PASSED++] "
			eval ${tname}=true
		else
			echo -e "\t[--TEST $test_suite.$test_number FAILED--] Bad response code (${outp_arr[1]}) received from server"
			eval ${tname}=false
		fi
		test_number=$(($test_number + 1))
	fi	

	#CLEANUP
	rm -r -f temp_dir 2>&1 &> /dev/null
	rm -r -f client_temp_dir 2>&1 &> /dev/null
	if (( $test_suite != 3 )) ; then
		killProcesses
	fi
}


#********************************** TEST INITIALIZATION ****************************************

cleanup
compileTools
compileSource

client_run=1

#********************************** TEST SUITE 1 ****************************************
echo -e "\n************* TESTING PART 1 *************"
test_suite=1

if [[ $TEST_01_PASSED == true ]] ; then	
	testClientServerInteraction "$SERVER1" "$TESTCLIENT" "1"

else
	echo -e "---Skipping test of Part 1 because your server1 didn't compile---"
fi
echo -e "************* END OF TESTING PART 1 *************"

#********************************** TEST SUITE 2 ****************************************
echo -e "\n\n************* TESTING PART 2 *************"
test_suite=2

if [[ $TEST_02_PASSED == true ]] && [[ $TEST_01_PASSED == true ]] ; then
	echo    	
	echo "//Testing server1 with client//"
	testClientServerInteraction "$SERVER1" "$CLIENT" "1" 
	
	echo
	echo "//now testing client with test server//"
	testClientServerInteraction "$TESTSERVER" "$CLIENT" "3"	

else
	echo -e "---Skipping test of Part 2 because your client OR your server1 didn't compile---"
fi
echo -e "************* END OF TESTING PART 2 *************"

#********************************** TEST SUITE 3 ****************************************
echo -e "\n\n************* TESTING PART 3 *************"
test_suite=3

if [[ $TEST_03_PASSED == true ]] ; then

	testClientServerInteraction "$SERVER2" "$TESTCLIENT" "1" "3"
else
	echo -e "---Skipping test of Part 3 because your server2 didn't compile---"
fi
echo "************* END OF TESTING PART 3 *************"


#**************************************** FINAL STEPS ************************************

echo -e "\n\n************* FINAL STEPS *************"
cleanup

# Checking minimum requirements 
#((testclientreturn11 == 2)) || ((testclientreturn11 >= 1) && (test 2.1 and and test 2.2 passed))
messages=0
if [[ "$testclientreturn11" -eq 2 ]] ; then
	echo -e "\n+++++ OK: You may have met the minimum requirements to pass the exam!!! +++++\n"
	exit
else
	((messages++))
	echo -e "\n \t ${messages})  Your server1 didn't respond with the correct message in test 1.1\n "
fi

if [[ "$testclientreturn11" -gt 1 ]] && [[ "$TEST_21_PASSED" == true ]] && [[ "$TEST_22_PASSED" == true ]] 
			&& [[ "$TEST_23_PASSED" == true ]] && [[ "$TEST_24_PASSED" == true ]] ; then
	echo -e "\n+++++ OK: You may have met the minimum requirements to pass the exam!!! +++++\n"
	exit
else
	((messages++))
	echo -e "\t ${messages})  Your client and  your server1 were not able to complete an interaction \n "
fi

echo -e "\n----- FAIL: You MAY NOT have met the minimum requirements to pass the exam!!! -----\n"
if [[ "$messages" == 2 ]] ; then
	echo -e "\n### Fix at least one of the two items above to meet the minimum requirements ###\n "	
fi
echo -e "\n************* END OF test.sh *************\n"
