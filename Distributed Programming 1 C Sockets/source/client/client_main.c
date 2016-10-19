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

#define READBUFFSIZE 4097

#define TRIESNUMBER 2
#define TIMEOUTSECONDS 3

enum ErrorType {NOPERROR, PERROR};
enum ErrorAction {DIE, NODIE};

void printError(const char* message, enum ErrorType errorType, enum ErrorAction errorAction)
{
  if(errorType==PERROR)
  {
    fprintf(stderr, "Error: %d\n", errno);
    perror(message);
  }
  else fprintf(stderr, "Error: %s\n", message);
  if(errorAction == DIE)
    exit(EXIT_FAILURE);
}

char *lookupHostUDP(const char *host)
{
  struct addrinfo hints, *res;
  char *first_ipv4_address = NULL;
  int errcode;
  char addrstr[100];
  void *ptr;
  int flag = 0;

  memset(&hints, 0, sizeof (hints));
  hints.ai_family = PF_UNSPEC;
  hints.ai_socktype = SOCK_DGRAM;
  hints.ai_flags |= AI_CANONNAME;

  errcode = getaddrinfo (host, NULL, &hints, &res);
  if(errcode != 0)
  {
    printError("getaddrinfo()", PERROR, DIE);
    return NULL;
  }

  //printf ("Host: %s\n", host);
  while (res)
  {
    inet_ntop (res->ai_family, res->ai_addr->sa_data, addrstr, 100);

    switch (res->ai_family)
    {
      case AF_INET:
	ptr = &((struct sockaddr_in *) res->ai_addr)->sin_addr;
	inet_ntop (res->ai_family, ptr, addrstr, 100);
	if(flag == 0)
	{
	  first_ipv4_address = strdup(addrstr); 
	  flag = 1;
	}
	break;
      case AF_INET6:
	ptr = &((struct sockaddr_in6 *) res->ai_addr)->sin6_addr;
	inet_ntop (res->ai_family, ptr, addrstr, 100);
	break;
    }

    //printf ("IPv%d address: %s (%s)\n", res->ai_family == PF_INET6 ? 6 : 4, addrstr, res->ai_canonname);
    res = res->ai_next;
  }

  return first_ipv4_address;
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

void usage(int argc, char* programName)
{
  if(argc!=4)
  {
    printf("usage: %s <address|url> <port> <fileName>\n", programName);
    printError("usage()", NOPERROR, DIE);
  }
}

int main(int argc, char *argv[])
{
  usage(argc, argv[0]);
  
  char* ipAddressString = lookupHostUDP(argv[1]);
  char* portString = argv[2];
  char* fileName = argv[3];
  
  int result;
  
  //if(strlen(fileName)>256 || !validChars(fileName, strlen(fileName))) printError("Invalid fileName", NOPERROR, DIE);
  
  //da qui in poi il nome del file è garantito essere valido: max 256 caratteri.
  
  struct in_addr serverIp;
  result = inet_aton(ipAddressString, &serverIp);  
  if(!result) printError("inet_aton()", NOPERROR, DIE);
  
  uint16_t serverPort = atoi(portString);
  //if(serverPort<1 || serverPort>65535) printError("Invalid port number", NOPERROR, DIE);
  
  struct sockaddr_in serverAddress;
  serverAddress.sin_family = AF_INET;
  serverAddress.sin_port   = htons(serverPort);
  serverAddress.sin_addr   = serverIp;

  int socket0;
  socket0 = socket(AF_INET, SOCK_DGRAM, IPPROTO_UDP);
  if(socket0<0) printError("socket()", PERROR, DIE);
  
  int done = 0;
  int try;
  for(try=1; try<=TRIESNUMBER && !done; try++)
  {
    result = sendto(socket0, fileName, strlen(fileName), 0, (struct sockaddr*)&serverAddress, sizeof(serverAddress));
    if(result==-1) printError("sendto()", PERROR, DIE);
    else if(result!=strlen(fileName)) printError("sendto(): datagram too big", NOPERROR, DIE);
	//else printf("Pacchetto UDP inviato.\n");
    
    while(1)
    {
		fd_set socketReadSet;
		FD_ZERO(&socketReadSet);
		FD_SET(socket0, &socketReadSet);
		  
		struct timeval timeout;
		timeout.tv_sec = TIMEOUTSECONDS;
		timeout.tv_usec = 0;
		
		do
		  result = select(FD_SETSIZE, &socketReadSet, NULL, NULL, &timeout);
		while(result<0 && errno == EINTR);
		
		if(result<0) printError("select()", PERROR, DIE);  
		else if(result==0)
		{
		  break;	//timeout senza risposta
		}
		else
		{
		  //ho una risposta, è buona?
		  char buffer[READBUFFSIZE];
		  struct sockaddr_in fromAddress;
		  socklen_t fromAddressLength = sizeof(fromAddress);
		  
		  int receivedBytesCount = recvfrom(socket0, buffer, sizeof(buffer)-1, 0, (struct sockaddr*)&fromAddress, &fromAddressLength);
		  if(receivedBytesCount==-1) printError("recvfrom()", PERROR, DIE);
		  else if(receivedBytesCount==0) printError("recvfrom(): the peer has performed an orderly shutdown", NOPERROR, DIE);
		  //buffer[receivedBytesCount]='\0';
		  
		  if(receivedBytesCount<4) printError("too short reply 1", NOPERROR, DIE);
		  
		  int firstCode = htonl(*((uint32_t*)&buffer[0]));
		  
		  if(firstCode!=0 && firstCode!=1 && firstCode!=2)
			continue; //printError("invalid firstCode", NOPERROR, DIE);
		  
		  if((firstCode==0 || firstCode==1) && receivedBytesCount!=4+strlen(fileName))
		  	continue; //printError("wrong reply length 2", NOPERROR, DIE);
		  
		  if(firstCode==2 && receivedBytesCount!=8+strlen(fileName))
		  	continue; //printError("wrong reply length 3", NOPERROR, DIE);
		  
		  if(strncmp(&buffer[4], fileName, strlen(fileName)))
		  	continue; //printError("wrong fileName in the reply 4", NOPERROR, DIE);
		  	
		  //ora qui siamo nella happy way
		  if(firstCode==0)
			printf("RESPONSE 0\n");
		  else if(firstCode==1)
		  	printf("RESPONSE 1\n");
		  else if(firstCode==2)
		  {
		  	int fileSize = ntohl(*((uint32_t*)&buffer[4+strlen(fileName)]));
			printf("RESPONSE 2 %d\n", fileSize);
		  }
		  
		  done = 1;
		  fflush(stdout);
		  break;
		}
	}
  }
  
  if(try>2)
  {
  	printf("RESPONSE -1\n");
    fflush(stdout);
  }
  
  exit(EXIT_SUCCESS);
}
