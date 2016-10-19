//16:40
/*#include <stdio.h>
#include <stdlib.h>

char *prog_name;

int main (int argc, char *argv[])
{
	return 0;
}*/

//server udp echo

#include <stdlib.h> // getenv()
#include <sys/types.h>
#include <sys/socket.h>
#include <sys/time.h> // timeval
#include <sys/select.h>
#include <sys/wait.h>
#include <netinet/in.h>
#include <arpa/inet.h> // inet_aton()
#include <sys/un.h> // unix sockets
#include <netdb.h>
#include <errno.h>
#include <unistd.h>
#include <stdio.h>
#include <ctype.h>
#include <rpc/xdr.h>
#include <string.h>
#include <time.h>
#include <sys/stat.h>
#include <fcntl.h>

#define READBUFFSIZE 4096

enum ErrorType {NOPERROR, PERROR};
enum ErrorAction {DIE, NODIE};

void printError(const char* message, enum ErrorType errorType, enum ErrorAction errorAction)
{
  /*if(errorType==PERROR)
  {
    fprintf(stderr, "Error: %d\n", errno);
    perror(message);
  }
  else fprintf(stderr, "Error: %s\n", message);*/
  if(errorAction == DIE)
    exit(EXIT_FAILURE);
}

void usage(int argc, char* programName)
{
  if(argc!=3)
  {
    /*printf("usage: %s <port> <number of parallel processes>\n", programName);*/
    printError("usage()", NOPERROR, DIE);
  }
}

int validChars(char* string, int length)
{
	int i;
	for(i=0; i<length; i++)
	{
		char c = string[i];
		if((c>='a' && c<='z') || (c>='A' && c<='Z') || c=='_' || c=='.')
			continue;
		else
			return 0;
	}
	return 1;
}

int main(int argc, char *argv[])
{
  usage(argc, argv[0]);
  
  char* portString = argv[1];
  const char* processLimitString = argv[2];
  
  int result;
  
  uint16_t serverPort = atoi(portString);
  
  struct sockaddr_in address;
  address.sin_family      = AF_INET;
  address.sin_port        = htons(serverPort);
  address.sin_addr.s_addr = htonl(INADDR_ANY);
  
  int socket0 = socket(AF_INET, SOCK_DGRAM, IPPROTO_UDP);
  if(socket0<0) printError("socket()", PERROR, DIE);
  
  result = bind(socket0, (struct sockaddr*)&address, sizeof(address));
  if(result!=0) printError("bind()", PERROR, DIE);
  
  int childNumber = atoi(processLimitString);
  
  int i;
  for(i=0; i<childNumber; i++)
  {
    //printf("Generazione del figlio #%d\n", i);
    int childPID = fork(); if(childPID<0) { printError("fork()", PERROR, DIE); }
    if(childPID>0)
    {
      //printf("Padre %d ha appena creato il figlio %d\n", getpid(), childPID);
    }
    else
    {
      //printf("Figlio %d, il cui padre è %d\n", getpid(), getppid());
      //processo figlio (serve il client); nel loop fa accept() e serve il client
      while(1)
      {
		  char requestBuffer[4097];
		  char replyBuffer[512];
		  while(1)
		  {
			int receivedBytesCount;
			struct sockaddr_in fromAddress;
			socklen_t fromAddressLength = sizeof(fromAddress);
		
			receivedBytesCount = recvfrom(socket0, requestBuffer, sizeof(requestBuffer)-1, 0, (struct sockaddr*)&fromAddress, &fromAddressLength);
			if(receivedBytesCount==-1) { printError("recvfrom()", PERROR, NODIE); continue; }
			if(receivedBytesCount==0) { printError("recvfrom(): the peer has performed an orderly shutdown", NOPERROR, NODIE); continue; }
		
			int firstCode;
			struct stat fileStats;
			//requestBuffer[receivedBytesCount] = 0;	//0 terminatore
			requestBuffer[256] = 0;					//0 terminatore
			char* fileName = requestBuffer;
			if(receivedBytesCount>256 || !validChars(requestBuffer, receivedBytesCount))
			{
				//richiesta non valida: troppo lunga o caratteri non ammessi
				firstCode = 0;
			}
			else
			{
				//richiesta lecita
				result = stat(fileName, &fileStats);
				//if(result==-1) { printError("stat()", PERROR, NODIE); continue; }
		
				int fileExists = !result;
				if(!fileExists)
				{
					//il file non esiste
					firstCode = 1;
				}
				else
				{
					//il file esiste
					firstCode = 2;
				}
			}
	
			//qui ho firstCode
			*((uint32_t*)&replyBuffer[0]) = htonl(firstCode);
	
			memcpy(&replyBuffer[4], requestBuffer, strlen(fileName));
		
			int replyLength = 4 + strlen(fileName);
		
			if(firstCode==2)
			{
				*((uint32_t*)&replyBuffer[strlen(fileName)+4]) = htonl(fileStats.st_size);
				replyLength += 4;
			}
		
			if(firstCode==0)
				printf("REQUEST %s %i %d\n", inet_ntoa(fromAddress.sin_addr), ntohs(fromAddress.sin_port), firstCode);
			else
				printf("REQUEST %s %i %d %s\n", inet_ntoa(fromAddress.sin_addr), ntohs(fromAddress.sin_port), firstCode, fileName);

			fflush(stdout);

			result = sendto(socket0, replyBuffer, replyLength, 0, (struct sockaddr*)&fromAddress, fromAddressLength);
			if(result==-1) { printError("sendto()", PERROR, NODIE); continue; }
			else if(result!=replyLength) { printError("sendto(): datagram too big", NOPERROR, NODIE); continue; }
			//else printf("Pacchetto UDP inviato.\n");
		  	}
      }

      exit(EXIT_SUCCESS);
    }
  }
}
