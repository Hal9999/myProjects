    #start=8259.exe#
;1 s * 40 KHz = 40000
C0TICKS equ 40000
    
    .model small          
    .data     
        sampleLSB   db ?
        lsbInFull   db 0
        buffer      dw 601 dup(0)   ;buffer circolare
        head        dw 0            ;prima posizione libera
        tail        dw 600          ;ultima posizione libera: se t+1=h ho buffer vuoto, se t=h ho buffer pieno, ma il testo garantisce che non accade che il buffer si riempia
        doHomework  db 0            ;se 1, allora la procedura di output viene avviata al prossimo input di msb
        avg         dw ?
        sendStatus  db ?
    .stack     
    .code
INIT_IVT	PROC
		PUSH AX
		PUSH BX
		PUSH AX
		PUSH DS
    		XOR	AX,	AX
    		MOV	DS,	AX      
    		MOV	BX, 39 ;channel 7		
    		SHL	BX, 2
    		MOV AX, offset ISR_PA_IN
    		MOV	DS:[BX], AX
    		MOV	AX, seg ISR_PA_IN
    		MOV	DS:[BX+2], AX       
    		MOV	BX, 36 ;channel 4
    		SHL	BX, 2		
    		MOV AX, offset ISR_PB_OUT
    		MOV	DS:[BX], AX
    		MOV	AX, seg ISR_PB_OUT
    		MOV	DS:[BX+2], AX       		
    		MOV	BX, 35 ;channel 3
    		SHL	BX, 2		
    		MOV AX,	offset ISR_COUNT0
    		MOV	DS:[BX], AX
    		MOV	AX, seg ISR_COUNT0
    		MOV	DS:[BX+2], AX 					
		POP	DS
		POP	DX
		POP	BX
		POP	AX 		
		RET
INIT_IVT	ENDP
                 
INIT_8255   PROC 
                mov al, 10110100b   ;porta A modo 1 input, porta B modo 1 ouput
                out 083h, al
                mov al, 00001001b   ;set PC4 to enable interrupt on PA in
                out 083h, al   
                mov al, 00000101b   ;set PC2 to enable interrupt on PB out
                out 083h, al
            RET            
INIT_8255   ENDP          

INIT_8253   PROC
                mov al, 00110100b   ;counter0 init
                out 063h, al 
                mov ax, C0TICKS     ;counter0 value
                out 060h, al
                xchg al, ah
                out 060h, al
            RET
INIT_8253   ENDP    
   
INIT_8259   PROC
            PUSH AX
                MOV	AL, 00011011b   ;ICW1: level triggered, single 8259, ICW4 yes
    	        OUT	40h, AL
    	        MOV	AL, 00100000b   ;ICW2: start from INTR 32
    	        OUT	41h, AL
    	        MOV AL, 00000011b   ;ICW4: full nested mode, buf mode, master, AEOI
    	        OUT 41h, AL
    	        MOV AL, 01100111b   ;OCW1: channels 3, 4, 7 enabled
        	    OUT 41h, AL
            POP AX
	        RET
INIT_8259   ENDP

ISR_PA_IN   PROC
            IRET    
ISR_PA_IN   ENDP                      
                               
ISR_COUNT0  PROC
                mov al, 00110100b   ;counter0 init
                out 063h, al
                mov ax, 10000     ;counter0 value
                out 060h, al
                xchg al, ah
                out 060h, al
            IRET    
ISR_COUNT0  ENDP

ISR_PB_OUT  PROC

            IRET    
ISR_PB_OUT  ENDP


.startup    
            CLI         
            call INIT_IVT
	        call INIT_8259 
            call INIT_8253 
            call INIT_8255
            STI        
                        
            ;hlt
      block:
            jmp  block
                      
.exit
end