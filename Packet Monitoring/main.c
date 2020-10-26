#include <sys/time.h>
#include <netinet/in.h>
#include <net/ethernet.h>
#include <pcap/pcap.h>
#include <signal.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <errno.h>
#include <unistd.h>
#include <netinet/ip_icmp.h>
#include <time.h>
#include "function.h"
#include <pthread.h>
#include <netdb.h>
#include <sys/types.h> 
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>



#define PROMISCUOUS 1
#define NONPROMISCUOUS 0
#define BUFSIZE 1024

// IP 헤더 구조체 l
struct ip *iph;

// TCP 헤더 구조체
struct tcphdr *tcph;

 struct ieee80211_hdr {
    uint16_t frame_control;     // 16비트 크기 부호 없는
    uint16_t duration_id;
    uint8_t addr1[6];
    uint8_t addr2[6];
    uint8_t addr3[6];
    uint16_t seq_ctrl;
    uint8_t addr4[6];
};

struct radiotap_hdr {
	uint8_t num[4]; // num[2],num[3] related to radiotap header length
};

time_t timer;
struct tm *t, *tt;

int traffic;
int flag = 0;
int position = 1;
char normal_device[20];

void* thread_list(void *arg);

void* thread_socket(void *arg){		// receive data from index.js

  int parentfd; /* parent socket */
  int childfd; /* child socket */
  int portno; /* port to listen on */
  int clientlen; /* byte size of client's address */
  struct sockaddr_in serveraddr; /* server's addr */
  struct sockaddr_in clientaddr; /* client addr */
  struct hostent *hostp; /* client host info */
//  char buf[BUFSIZE]; /* message buffer */
  char *buf = (char *)malloc(50);
 char *tmp = (char *)malloc(1200);	//  all device size
  char *hostaddrp; /* dotted decimal host addr string */
  int optval; /* flag value for setsockopt */
  int n; /* message byte size */

  portno = 9090;

  /* 
   * socket: create the parent socket 
   */
  parentfd = socket(AF_INET, SOCK_STREAM, 0);
  if (parentfd < 0) 
    error("ERROR opening socket");

  /* setsockopt: Handy debugging trick that lets 
   * us rerun the server immediately after we kill it; 
   * otherwise we have to wait about 20 secs. 
   * Eliminates "ERROR on binding: Address already in use" error. 
   */
  optval = 1;
  setsockopt(parentfd, SOL_SOCKET, SO_REUSEADDR, 
	     (const void *)&optval , sizeof(int));

  /*
   * build the server's Internet address
   */
  bzero((char *) &serveraddr, sizeof(serveraddr));

  /* this is an Internet address */
  serveraddr.sin_family = AF_INET;

  /* let the system figure out our IP address */
  serveraddr.sin_addr.s_addr = htonl(INADDR_ANY);

  /* this is the port we will listen on */
  serveraddr.sin_port = htons((unsigned short)portno);

  /* 
   * bind: associate the parent socket with a port 
   */
  if (bind(parentfd, (struct sockaddr *) &serveraddr, 
	   sizeof(serveraddr)) < 0) 
    error("ERROR on binding");

  /* 
   * listen: make this socket ready to accept connection requests 
   */
  if (listen(parentfd, 5) < 0) /* allow 5 requests to queue up */ 
    error("ERROR on listen");

  /* 
   * main loop: wait for a connection request, echo input line, 
   * then close connection.
   */
  clientlen = sizeof(clientaddr);
  while (1) {
	printf("startttttt\n");
     int flagggg = 0;

    /*
     * accept: wait for a connection request
     */
    childfd = accept(parentfd, (struct sockaddr *) &clientaddr, &clientlen);
    if (childfd < 0)
      error("ERROR on accept");

    /*
     * gethostbyaddr: determine who sent the message
     */
    hostp = gethostbyaddr((const char *)&clientaddr.sin_addr.s_addr,
			  sizeof(clientaddr.sin_addr.s_addr), AF_INET);
    if (hostp == NULL)
      error("ERROR on gethostbyaddr");
    hostaddrp = inet_ntoa(clientaddr.sin_addr);
    if (hostaddrp == NULL)
      error("ERROR on inet_ntoa\n");
    printf("\n\n\n\nserver established connection with %s (%s)\n\n\n\n\n",
	   hostp->h_name, hostaddrp);

    /*
     * read: read input string from the client
     */
    bzero(buf, 50);
	printf("buf addr : %p\n",buf);

//	while(1){
    n = read(childfd, buf, 50);
    if (n < 0) 
      error("ERROR reading from socket");
    printf("\n\n\n\n\nserver received %d bytes: %s\n\n\n\n\n", n, buf);

    if(strlen(buf) != 1){	// when error such as '4search' occurs
	flagggg = 1;
        printf(" more than one error \n");
    }
    /*
     * write: echo the input string back to the client
     */
    n = write(childfd, buf, strlen(buf));
    if (n < 0)
      error("ERROR writing to socket");
    printf("write\n");

    if(buf[0]==49){
//	char *tmpp = (char *)malloc(50);
        printf("1. \n");
//	sleep(1);
        if(flagggg == 1){
//		tmp = buf+1;	
		 strcpy(tmp,buf+1);
	}
	else{	
        	n = read(childfd, tmp, BUFSIZE);
//	printf("yyyyyyyyyyyyyy %s %p\n",tmpp,tmpp);
		if (n < 0)
               		 error("ERROR reading from socket");
  	}
        printf("\nMAC address is %s\n", tmp);

/*	n = write(childfd, buf, strlen(buf));
    	if (n < 0)
      		error("ERROR writing to socket");
*/
	insert(mylist, tmp, position);
	position++;

        printf("INSERT!!!\n");
	tmp+=15;
    }
    else if(atoi(buf) == 3){

	printf("3. \n");
        n = read(childfd, buf, BUFSIZE);	// should we change 'buf' to 'tmp'?  No
        if (n < 0)
                error("ERROR reading from socket");
	strcpy(normal_device, buf);
        printf("\nNoaml_device is %s\n", normal_device);

        n = write(childfd, buf, strlen(buf));
        if (n < 0)
                error("ERROR writing to socket");

	flag = 1;
	normal_start = time(NULL);

    }
    else if(buf[0] == 50){

	printf("2. \n");
	if(flagggg == 1){
                buf = buf+1;    // strcpy(tmp,buf+1)
        }
        else{   
                n = read(childfd, buf, BUFSIZE);
//      printf("yyyyyyyyyyyyyy %s %p\n",tmpp,tmpp);
                if (n < 0)
                         error("ERROR reading from socket");
        }

    //    n = read(childfd, buf, BUFSIZE);
    //    if (n < 0)
    //            error("ERROR reading from socket");
	delete(mylist, buf);
	position--;

        n = write(childfd, buf, strlen(buf));
        if (n < 0)
                error("ERROR writing to socket");
	printf("\nDelete %s\n", buf);

//	delete(mylist, buf);

    }
    else if(buf[0]==52){		// unregisterd list 
      
        printf("4. \n");
//        n = read(childfd, buf, BUFSIZE);	// read 'search'
 //       if (n < 0)
  //              error("ERROR reading from socket");
/*	 if(flagggg == 1){
                buf = buf+1;    // strcpy(tmp,buf+1)
        }
        else{   
                n = read(childfd, buf, BUFSIZE);

                if (n < 0)
                         error("ERROR reading from socket");
        }
*/
	printf("here?????????????????????????\n");
	
	now = time(NULL);

	char timess22[50];
        tt = localtime(&now);
        sprintf(timess22, "%d-%02d-%02d %02d:%02d:%02d", tt->tm_year + 1900, tt->tm_mon + 1, tt->tm_mday, tt->tm_hour, tt->tm_min, tt->tm_sec);

        strcat(listJson,"{\"time\":\"");
        strcat(listJson,timess22);
        strcat(listJson,"\"");
        strcat(listJson,",\"list\":[");

       // char traf22[5];
        nptr tmp22=unregistered_list->head;
        while(tmp22!=NULL){
            strcat(listJson,"\"");  
            strcat(listJson,tmp22->value);
            strcat(listJson,"\"");
            tmp22=tmp22->next;
	    if(tmp22 != NULL)
		 strcat(listJson,",");

        }
        strcat(listJson,"]}");
        printf("count22: %s\n",listJson);

        char cc[500];    // when memory out, string c can be broken in thread_routine

        strcpy(cc,listJson);
        	
	pthread_t thread22;
        pthread_create(&thread22,NULL,thread_list,cc);
        pthread_detach(thread22);
        listJson[0] = '\0';
	init(unregistered_list);
        number = 1;

        n = write(childfd, buf, strlen(buf));
        if (n < 0)
                error("ERROR writing to socket");
	printf("\nUnregiseted_list Sending %s\n", buf);
    }

    close(childfd);
    printf("endddddddd\n");

    //close(childfd);
  }

  free(buf);
}

void* thread_list(void *arg){                //  send data to index.js
    
    printf("thread! %s\n",arg);

    int sockfd, portno, n;
    struct sockaddr_in serveraddr;
    struct hostent *server;
    char *hostname = "localhost";
    char buf[BUFSIZE];

    portno = 9053;

    /* socket: create the socket */
    sockfd = socket(AF_INET, SOCK_STREAM, 0);
    if (sockfd < 0)
        error("ERROR opening socket22");

    /* gethostbyname: get the server's DNS entry */
    server = gethostbyname(hostname);
    if (server == NULL) {
        fprintf(stderr,"ERROR, no such host as %s\n", hostname);
        exit(0);
    }

    /* build the server's Internet address */
    bzero((char *) &serveraddr, sizeof(serveraddr));
    serveraddr.sin_family = AF_INET;
    bcopy((char *)server->h_addr,
          (char *)&serveraddr.sin_addr.s_addr, server->h_length);
    serveraddr.sin_port = htons(portno);

    /* connect: create a connection with the server */
    if (connect(sockfd, &serveraddr, sizeof(serveraddr)) < 0)
      error("ERROR connecting");

    /* get message line from the user */
//    printf("Please enter msg: ");
    bzero(buf, BUFSIZE);
//    fgets(buf, BUFSIZE, stdin);
    snprintf(buf,sizeof(buf), "%s", arg);
    //sprintf(buf, "%s", arg);
	printf("buf   %s\n\n",buf);

/* send the message line to the server */
    n = write(sockfd, buf, strlen(buf));        // send data to index.js
    if (n < 0)
      error("ERROR writing to socket22");

    /* print the server's reply */
    bzero(buf, BUFSIZE);
    n = read(sockfd, buf, BUFSIZE);
    if (n < 0)
      error("ERROR reading from socket22");
    printf("Echo from server22: %s", buf);
    close(sockfd);
 //   return 0;
}

void* thread_routine(void *arg){		//  send data to index.js

    printf("thread!\n");

    int sockfd, portno, n;
    struct sockaddr_in serveraddr;
    struct hostent *server;
    char *hostname = "localhost";
    char buf[BUFSIZE];

    portno = 9050;

    /* socket: create the socket */
    sockfd = socket(AF_INET, SOCK_STREAM, 0);
    if (sockfd < 0) 
        error("ERROR opening socket");

    /* gethostbyname: get the server's DNS entry */
    server = gethostbyname(hostname);
    if (server == NULL) {
        fprintf(stderr,"ERROR, no such host as %s\n", hostname);
        exit(0);
    }

    /* build the server's Internet address */
    bzero((char *) &serveraddr, sizeof(serveraddr));
    serveraddr.sin_family = AF_INET;
    bcopy((char *)server->h_addr, 
	  (char *)&serveraddr.sin_addr.s_addr, server->h_length);
    serveraddr.sin_port = htons(portno);

    /* connect: create a connection with the server */
    if (connect(sockfd, &serveraddr, sizeof(serveraddr)) < 0) 
      error("ERROR connecting");

    /* get message line from the user */
    bzero(buf, BUFSIZE);
    sprintf(buf, "%s", arg);

    /* send the message line to the server */
    n = write(sockfd, buf, strlen(buf));	// send data to index.js
    if (n < 0) 
      error("ERROR writing to socket");

    /* print the server's reply */
    bzero(buf, BUFSIZE);
    n = read(sockfd, buf, BUFSIZE);
    if (n < 0) 
      error("ERROR reading from socket");
    printf("Echo from server: %s", buf);
    close(sockfd);
    return 0;

}

void* thread_menu(void *arg){

     int num;
     while(1){
        printf("< Menu >\n");
	printf("1.Registration\n");
	printf("2.Normal traffic measurement\n");
	printf("3.Alarm control\n");
	scanf("%d", &num);

	if(num == 1){
	}
	else if (num == 2){
		printf("device addr:");
		scanf("%s",normal_device);
		flag = 1;
		normal_start = time(NULL);
	}
	else if (num == 3){
	}
		
     }

}

// 패킷을 받아들일경우 이 함수를 호출한다.  
// packet 가 받아들인 패킷이다.
void callback(u_char *useless, const struct pcap_pkthdr *pkthdr, 
                const u_char *packet)
{
    struct ether_header *ep;
    int chcnt =0;
    int length=pkthdr->len;
    struct ieee80211_hdr *ieeeh;
    struct radiotap_hdr *radioh;

    radioh = (struct radiotap_hdr *)packet;
   // printf("radiottttttttttpppp %d\n",radioh->num[2]);
    if(radioh->num[2] == 18){
   	ieeeh = (struct ieee80211_hdr *)(packet+18);
        length -= 18;
    }
    else{ 
    	ieeeh = (struct ieee80211_hdr *)(packet+21);
  	length -= 21;
    }

    int rr = (ieeeh->frame_control/256)%4;
    int type = ((ieeeh->frame_control%256)%16)/4;
    //type = 0 management frame
    //type = 1 control frame
    //type = 2 data frame

    int subtype = (ieeeh->frame_control%256)/16;
    int retry = ((ieeeh->frame_control/256)%16)/8;	

    char *strAddr1 = (char *)malloc(sizeof(char)*20);	// src, dst, transmitter, receiver
    char *strAddr2 = (char *)malloc(sizeof(char)*20);
    char *strAddr3 = (char *)malloc(sizeof(char)*20);
    char *strAddr4 = (char *)malloc(sizeof(char)*20);

    int flag1=0,flag2=0,flag3=0,flag4=0;	// standard for whether the address is added into list, so never do 'free'
//	int *flag1 ;
// int *flag2 ;
// int *flag3 ;
// int *flag4 ;
 	

    //packet+60   QoS
    //packet+58    Data

 if(type == 2 && retry == 0){

    if((ieeeh->addr1[0]== 255 && ieeeh->addr1[1]== 255) || (ieeeh->addr3[0]==255 && ieeeh->addr3[1]==255)
	|| (ieeeh->addr1[0]==1 && ieeeh->addr1[1]==0) || (ieeeh->addr3[0]==1 && ieeeh->addr3[1]==0)
 	|| (ieeeh->addr1[0]==51 && ieeeh->addr1[1]==51) || (ieeeh->addr3[0]==51 && ieeeh->addr3[1]==51)
 	|| (ieeeh->addr4[2]==170) );  // IPv4,6mcast and broadcast and EAPol exception
    
    else{ 

 /*   char strAddr1[20];	// src, dest, transmit, receive 
    char strAddr2[20];
    char strAddr3[20];
    char strAddr4[20];
*/

   	 sprintf(strAddr1, "%.2x%.2x%.2x%.2x%.2x%.2x",
        	ieeeh->addr1[0],
        	ieeeh->addr1[1],
       		ieeeh->addr1[2],
        	ieeeh->addr1[3],
        	ieeeh->addr1[4],
        	ieeeh->addr1[5]);

    	sprintf(strAddr2, "%.2x%.2x%.2x%.2x%.2x%.2x",
        	ieeeh->addr2[0],
        	ieeeh->addr2[1],
        	ieeeh->addr2[2],
       		ieeeh->addr2[3],
        	ieeeh->addr2[4],
        	ieeeh->addr2[5]);

    	sprintf(strAddr3, "%.2x%.2x%.2x%.2x%.2x%.2x",
        	ieeeh->addr3[0],
        	ieeeh->addr3[1],
        	ieeeh->addr3[2],
        	ieeeh->addr3[3],
        	ieeeh->addr3[4],
        	ieeeh->addr3[5]);

    	sprintf(strAddr4, "%.2x%.2x%.2x%.2x%.2x%.2x",
        	ieeeh->addr4[0],
        	ieeeh->addr4[1],
        	ieeeh->addr4[2],
        	ieeeh->addr4[3],
        	ieeeh->addr4[4],
        	ieeeh->addr4[5]);

//	printf("%s %s %s %s %d\n",strAddr1,strAddr2,strAddr3,strAddr4,rr);
//        printf("%p %p %p %p \n",strAddr1,strAddr2,strAddr3,strAddr4);
//	printf("num %d ",length);    
    // src,dst,trasmit,receive addr 중에 등록 기기 있는지 체크  
	
	flag1 = search(mylist, strAddr1, subtype, length);

    	if(strcmp(strAddr1,strAddr2)){
       		flag2 = search(mylist, strAddr2, subtype, length);
    	}
    	if(strcmp(strAddr3,strAddr1) && strcmp(strAddr3,strAddr2)){
       		flag3 = search(mylist, strAddr3, subtype, length);
    	}
    	if (rr==3){ // address 4 가 존재할 경우				주석 풀기
        	if(strcmp(strAddr4,strAddr1) && strcmp(strAddr4,strAddr2) && strcmp(strAddr4, strAddr3)){
	     		flag4 =  search(mylist, strAddr4, subtype, length);
        	}
    	}
 	
//	printf("flag: %d %d %d %d\n",flag1,flag2,flag3,flag4);

    }
 }

 end = time(NULL);
 nptr tmp;

/*
 if(flag == 1){	// 2.normal value measure 

	tmp=mylist->head;
        while(tmp!=NULL){
            if(strcmp(normal_device,tmp->value) == 0){
		if(tmp->traffic > tmp->normal_max)
			tmp->normal_max = tmp->traffic;
	    }
            tmp=tmp->next;
        }
	if((end-normal_start) >= 60) 
                flag = 0;

 }
*/


 if((end-start) >= 5){	// 1분 경과후  tmp->trafb                                            각 디바이스 패킷수 구글 클라우드로 전송 
        char timess[50];
	t = localtime(&end);
        sprintf(timess, "%d-%02d-%02d %02d:%02d:%02d", t->tm_year + 1900, t->tm_mon + 1, t->tm_mday, t->tm_hour, t->tm_min, t->tm_sec);

	strcat(countJson,"{\"time\":\"");
        strcat(countJson,timess);
	strcat(countJson,"\"");
	char traf[10], com[5];
        tmp=mylist->head;
	while(tmp!=NULL){

	    strcat(countJson,",\"");
            strcat(countJson,tmp->value);
            strcat(countJson,"\":[");
	    sprintf(traf, "%d,", tmp->traffic);
	    strcat(countJson, traf);
	    sprintf(com, "%d]", tmp->command);
	    strcat(countJson, com);
            tmp->count = 0;
	//	printf("normalmax: %d, flag: %d\n",tmp->normal_max,flag);
	
//	if(atoi(traf) == tmp->traffic)
//		printf("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~%s %d~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n\n\n\n\n",traf,tmp->traffic);
	    tmp->traffic = 0;
	    tmp->command = 0;
            tmp=tmp->next;

        }
	strcat(countJson,"}");
	printf("count: %s\n",countJson);

	char c[500];	// when memory out, string c can be broken in thread_routine

	strcpy(c,countJson);
	printf("c   %s\n\n\n", c);
	start = time(NULL);

	pthread_t thread;
	pthread_create(&thread,NULL,thread_routine,c);
	pthread_detach(thread);
	countJson[0] = '\0';
	
	tmp = new_list->head;
	int i = 1;
 while(tmp != NULL){
       printf("new %d. %s \n", i, tmp->value);
       tmp = tmp->next;
        i++;
 }
   tmp = unregistered_list->head;
         i = 1;
 while(tmp != NULL){
       printf("un %d. %s \n", i, tmp->value);
       tmp = tmp->next;
        i++;
 }


	init(unregistered_list);
	number = 1;
 }

if(flag1 == 1){
	char fresh1[20]={0,};
	strcpy(fresh1, strAddr1);
	pthread_t threadNew;
	pthread_create(&threadNew, NULL, thread_list,fresh1);
	pthread_detach(threadNew);
	printf("\n\n 1.   new mac %s\n\n",fresh1);
}
if(flag2 == 1){
        char fresh2[20] = {0,};
        strcpy(fresh2, strAddr2);
        pthread_t threadNew;
        pthread_create(&threadNew, NULL, thread_list,fresh2);
	pthread_detach(threadNew);
	printf("\n\n 2.    new mac %s\n\n",fresh2);
}
if(flag3 == 1){
        char fresh3[20] = {0,};
        strcpy(fresh3, strAddr3);
        pthread_t threadNew;
        pthread_create(&threadNew, NULL, thread_list,fresh3);
	pthread_detach(threadNew);
	printf("\n\n 3.     new mac %s\n\n",fresh3);
}
if(flag4 == 1){
        char fresh4[20] = {0,};
        strcpy(fresh4, strAddr4);
        pthread_t threadNew;
        pthread_create(&threadNew, NULL, thread_list,fresh4);
	pthread_detach(threadNew);
	printf("\n\n 4.     new mac %s\n\n",fresh4);
}



free(strAddr1);
free(strAddr2);
free(strAddr3);
free(strAddr4);
/* if(flag1 == 0)free(strAddr1); else printf("add: %p\n",strAddr1); 
 if(flag2 == 0)free(strAddr2); else printf("add: %p\n",strAddr2);
 if(flag3 == 0)free(strAddr3); else printf("add: %p\n",strAddr3);
 if(flag4 == 0)free(strAddr4); else printf("add: %p\n",strAddr4);
*/


}


int main(int argc, char **argv)
{
    char *dev = "wlan1mon";
    char *net;
    char *mask;

    bpf_u_int32 netp;
    bpf_u_int32 maskp;
    char errbuf[PCAP_ERRBUF_SIZE];
    int ret;
    struct pcap_pkthdr hdr;
    struct in_addr net_addr, mask_addr;
    struct ether_header *eptr;
    const u_char *packet;
    struct bpf_program fp;

    pcap_t *pcd;  // packet capture descriptor

    mylist=(list*)malloc(sizeof(list));
    init(mylist);
   
    unregistered_list = (list*)malloc(sizeof(list));
    init(unregistered_list);

    new_list = (list*)malloc(sizeof(list));
    init(new_list);

    number = 1;
    number2 = 1;
    int num = 0;
    char* device1 = "b827eb15a45f";
    char* device2 = "7c5cf8d7d345";

    pcd = pcap_open_live(dev, 100,  NONPROMISCUOUS, -1, errbuf);
    if (pcd == NULL)
    {
        printf("%s\n", errbuf);
        exit(1);
    }

    // 컴파일 옵션을 준다.
    if (pcap_compile(pcd, &fp, argv[2], 0, netp) == -1)
    {
        printf("compile error\n");
        exit(1);
    }
    // 컴파일 옵션대로 패킷필터 룰을 세팅한다.
    if (pcap_setfilter(pcd, &fp) == -1)
    {
        printf("setfilter error\n");
        exit(0);
    }

//     pthread_t thread_m;
//        pthread_create(&thread_m,NULL,thread_menu,NULL);
//        pthread_detach(thread_m);

    pthread_t thread_s;
    pthread_create(&thread_s,NULL,thread_socket,NULL);
    pthread_detach(thread_s);

    sleep(20);
    start = time(NULL);

    // 지정된 횟수만큼 패킷캡쳐를 한다. 
    // pcap_setfilter 을 통과한 패킷이 들어올경우 
    // callback 함수를 호출하도록 한다. 
    pcap_loop(pcd, atoi(argv[1]), callback, NULL);
}
