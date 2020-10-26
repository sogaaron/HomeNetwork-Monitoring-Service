
#include <stdio.h>
#include <stdlib.h>
#include <stddef.h>
#include <string.h>
#include "function.h"


void init(list* lptr){
	//initialize the list
	nptr tmp = lptr->head;
	while(tmp != NULL){
//		printf("list : %s %p\n",tmp,tmp);
		tmp = tmp->next;
	}
	tmp = lptr->head;
	nptr tmp2;
	while(tmp != NULL){	// list memory free
//		 printf("list free: %p %p\n",tmp,tmp->value);
		tmp2 = tmp->next;
//		free(tmp->value);       // element's address free
		free(tmp);	// element free
		
		tmp = tmp2;
	
	}	
	
	lptr->count=0;
	lptr->head=NULL;
	
}

int insertToList(list* lptr, char* value,int position){
	int re = 0;
	int flag = 0;

         //insert value to proper position
        if(position<1 || position>(lptr->count)+1){
                printf("ppPosition Out of Bound :%d  %d\n",position,lptr->count);
                return;
        }

nptr tmp = lptr->head;
        int i = 1;

 while(tmp != NULL){
//       printf("%d. %s     %s	%p\n", i, tmp->value, value, tmp);
       tmp = tmp->next;
        i++;
 }

        nptr new_nptr=(node*)malloc(sizeof(node));	// list element memory allocation
//	printf("addr: %p\t%p\t%p\n",new_nptr, new_nptr->value, value);
	new_nptr->value = (char *)malloc(20);
        strcpy(new_nptr->value,value);
//        printf("ppposition : %d %s count: %d\n", position,value,lptr->count);
	

        if(position==1){

                new_nptr->next=lptr->head;
                lptr->head=new_nptr;
		number++;
		//(*position)++;
		lptr->count++;			
		    printf("insert :%p\n",value);
		re = 1;
        }
	else{	
//		printf("here???\n");
		//int flag = 0;
//                nptr 
		tmp=lptr->head;
	        while(tmp != NULL){

                      if(strcmp(tmp->value,value) == 0){  // when unregistered_list has this address 	
//				printf("%s == %s\n", tmp->value, value);
//				printf("unadded element free\n");	
                            	flag = 1;  
				free(new_nptr);	// unadded element's memory free
//				free(new_nptr->value);
			    	break;
                      }
//			printf("why??\n");
		      tmp = tmp->next;
      		}

		tmp = lptr->head;
		if(flag == 0){     // when this address added into unresigstered_list
			printf("222222222222\n");
                	int i;
                	for(i=1;i<position-1;i++){
                        	tmp=tmp->next;
                	}

                	new_nptr->next=tmp->next;
                	tmp->next=new_nptr;
//			number++;
//(*position)++;
			lptr->count++;
			printf("insert :%s %p\n",value,new_nptr->value); 
			re = 1;
      		}

	}
	return re;
}


void insert(list* lptr,char* value,int position){
	
	//insert value to proper position
	if(position<1 || position>(lptr->count)+1){
		printf("Position Out of Bound\n");
		return;
	}
	nptr new_nptr=(node*)malloc(sizeof(node));
	//new_nptr->value=value;
	new_nptr->value = (char *)malloc(20);
	strcpy(new_nptr->value, value);
//	printf("position : %d %s %s\n", position,value,new_nptr->value);

	if(position==1){

		new_nptr->next=lptr->head;
		lptr->head=new_nptr;
	}
	else{

		nptr tmp=lptr->head;
		int i;
		for(i=1;i<position-1;i++){
			tmp=tmp->next;
		}
		new_nptr->next=tmp->next;
		tmp->next=new_nptr;
	}
	lptr->count++;
//	number++;
}
/*
void delete(list* lptr,int position){
	//delete an item on the position
	if(position<1 || position>(lptr->count)){
		printf("Position Out of Bound\n");
		return;
	}
	nptr tmp=lptr->head;

	if(position==1){
		lptr->head=tmp->next;
		free(tmp);
	}
	else{
		int i;
		for(i=1;i<position-1;i++){
			tmp=tmp->next;
		}
		nptr tmp2=tmp->next;
		tmp->next=tmp2->next;
		free(tmp2);
	}
	lptr->count--;
}
*/

void delete(list* lptr,char* value){
	//delete an item on the position
	int i=0;
	nptr tmp=lptr->head;
	nptr tmp3;
	while(tmp!=NULL){
		if(strcmp(value, tmp->value)==0){
			if(i==0){
				lptr->head=tmp->next;
				free(tmp);
//				free(tmp->value);
			}else{
			nptr tmp2=tmp3->next;
			tmp3->next=tmp2->next;
			free(tmp2);
//			free(tmp2->value);
			}
			break;
		}
	i++;
	tmp3 = tmp;
	tmp=tmp->next;	// 다음 등록기기로 넘어감
	}
	lptr->count--;
}






int* search(list* lptr,char* value, int subtype, int length){
	
	int re = 0;
	int re2 = 0;
	int flag = 0;
	nptr tmp=lptr->head;
	int i=1;
	while(tmp!=NULL){
		if(strcmp(value,tmp->value) == 0) {	// when names are same
//			printf("hey %d\n",length);
			if(subtype >=12);
			else if(subtype >=8){ //Qos data
			  // if(strcmp(value,"600194d2701b") == 0){
				printf("qqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqq %d\n",length-34);
				if(tmp->command < length-34)
					tmp->command = length-34;
			 //  }
			  // else{
				tmp->traffic += (length - 34);
			//	printf("Qos : %d\n", tmp->traffic);
			//	printf("hhhhh\n");
			//   }
			}
			else if(subtype >=4) ;
			else {
			   //if(strcmp(value,"600194d2701b") == 0){
				printf("ddddddddddddddddddddddddddddddddddddddddddddddddd %d\n",length-32);
                                if(tmp->command < length-32)
                                        tmp->command = length-32;
                          // }else{
			   	tmp->traffic += (length-32);
			   	printf("Data : %d\n", tmp->traffic);
			  // }
			}  //data
			flag = 1;
			break;
		}
		i++;
		tmp=tmp->next;	// 다음 등록기기로 넘어감
	}
//	number++;
	if(flag==0){	// when mylist doesn't have this address
		re = insertToList(unregistered_list,value,unregistered_list->count+1);	// unregistered device add
		re2 = insertToList(new_list,value,new_list->count+1);
	}
	//int *r = (int *)malloc(sizeof(int)*2);
	//r[0] = re;
	//r[1] = re2;
	return re2;	// add : 1, not : 0
}

