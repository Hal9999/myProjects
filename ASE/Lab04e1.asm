N EQU 5
    .model small          
    .data
        BUFFER DB 5 DUP(?)
        TESTA  DW 0             ;prima posizione libera
        CODA   DW N-1           ;ultima posizione libera

    .stack     
    .code   
    
    
    ; procedura di inizializzazione della interrupt vector table
INIT_IVT	PROC
		PUSH 	AX
		PUSH	BX
		PUSH	DX
		PUSH 	DS
		XOR	AX, 	AX
		MOV	DS, 	AX      
		
		; channel 7
		MOV	BX, 	39		
		SHL	BX, 	2		
		MOV AX, 	offset ISR_PA_IN
		MOV	DS:[BX], 	AX
		MOV	AX,     seg ISR_PA_IN
		MOV	DS:[BX+2], 	AX       
  				
		; channel 4
		MOV	BX, 	36		
		SHL	BX, 	2		
		MOV AX, 	offset ISR_PB_OUT
		MOV	DS:[BX], 	AX
		MOV	AX,     seg ISR_PB_OUT
		MOV	DS:[BX+2], 	AX       		
		; channel 3
		MOV	BX, 	35		
		SHL	BX, 	2		
		MOV AX, 	offset ISR_COUNT0
		MOV	DS:[BX], 	AX
		MOV	AX,     seg ISR_COUNT0
		MOV	DS:[BX+2], 	AX 										
										
		POP	DS
		POP	DX
		POP	BX
		POP	AX 		
		RET
INIT_IVT	ENDP

; ISR for reading the value received on PA            
ISR_PA_IN   PROC
            PUSH AX                
            PUSH DI
            PUSH SI
                MOV SI, TESTA
                MOV DI, CODA
                CMP SI, DI
                IN AL, 080H
                JE scarta
                MOV BUFFER[SI], AL
                INC SI
                CMP SI, N
                JNE noZeroT
                MOV SI, 0
                noZeroT:
                MOV TESTA, SI
   scarta:  POP SI
            POP DI
            POP AX            
            IRET    
ISR_PA_IN   ENDP             
                                     
; ISR for waiting a confirmation that the value written on PB is externally read                                           
ISR_PB_OUT  PROC
            PUSH AX                
            PUSH DI
            PUSH SI
                MOV SI, TESTA
                MOV DI, CODA
                INC DI
                CMP DI, N
                JNE noZeroC2
                MOV DI, 0
                noZeroC2:
                CMP DI, SI
                JE finished
                MOV AL, BUFFER[DI]
                OUT 082H, AL
                MOV CODA, DI
finished:   POP SI
            POP DI
            POP AX    
            IRET    
ISR_PB_OUT  ENDP         
                
ISR_COUNT0  PROC
            PUSH AX                
            PUSH DI
            PUSH SI
                MOV SI, TESTA
                MOV DI, CODA
                INC DI
                CMP DI, N
                JNE noZeroC
                MOV DI, 0
                noZeroC:
                CMP DI, SI
                JE bufferVuoto
                MOV AL, BUFFER[DI]
                OUT 082H, AL
                MOV CODA, DI
bufferVuoto:POP SI
            POP DI
            POP AX   
            IRET    
ISR_COUNT0  ENDP   

                 
INIT_8255   PROC
                MOV AL, 10110100b;
                OUT 083h, AL
                ; set PC4 to enable interrupt on PA in
                MOV AL, 00001001b    
                OUT 083h, AL
                ; set PC2 to enable interrupt on PB in or PB out
                MOV AL, 00000101b   
                OUT 083h, AL
                ; set PC6 to enable interrupt on PA out
                ;mov al, 00001101b 
                ;out 083h, al  
            RET            
INIT_8255   ENDP          

INIT_8253   PROC
                MOV AL, 00110100b ;counter0 init
                OUT 063h, AL      ;counter0 value
                MOV ax, 10000                       
                OUT 060h, AL
                XCHG AH, AL
                OUT 060h, AL                             
            RET
INIT_8253   ENDP    

.startup    
            CLI         
            CALL INIT_IVT
            CALL INIT_8253 
            CALL INIT_8255
            STI        
                        
            
block:      ;hlt
            JMP  block
                      
.exit
end

