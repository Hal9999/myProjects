    #start=8259.exe#
    
    .model small          
    .data
        count       DB 0
        prevSample  DW 0
        ABSCount    DB 0
        swTimer1    DW 0
        swTimer2    DW 0

    .stack     
    .code   
INIT_IVT	PROC
        		PUSH 	AX
        		PUSH	BX
        		PUSH	DX
        		PUSH 	DS
            		XOR	AX, 	AX
            		MOV	DS, 	AX      
            		
            		; channel 3 of 8259 master
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
                 
INIT_8255   PROC
                mov al, 10010000b;
                out 083h, al
            RET            
INIT_8255   ENDP          

INIT_8253   PROC
                mov al, 00110100b
                out 063h, al 
                                 
                mov ax, 1200    ;irq35 ogni 10us
                out 060h, al
                xchg al, ah
                out 060h, al          
            RET
INIT_8253   ENDP    

INIT_8259   PROC
                MOV	AL, 00011011b  ; ICW1
                ; level triggered
                ; single 8259
                ; IC4 = si
    	        OUT	40h, AL
    	        MOV	AL, 00100000b  ; ICW2
    	        ; a partire da INTR 32
    	        OUT	41h, AL
    	    
    	        MOV AL, 00000011b  ; ICW4
    	        ; fully nested mode
    	        ; buf mode
    	        ; master
    	        ; Automatic End Of Interrupt
    	        OUT 41h, AL
    	        MOV AL, 11110111b  ; OCW1   
    	        ;channel 3 enabled
        	    OUT 41h, AL
    	    RET
INIT_8259   ENDP

ISR_COUNT0   PROC
                inc swTimer1
                cmp swTimer1, 3
                jne noTick1
                mov swTimer1, 0
                call sampleSpeed
        noTick1:
                cmp ABSCount, 0
                jz fineC0
                cmp swTimer2, 100 ;10.000
                inc swTimer2
                jne fineC0
                mov swTimer2, 0
                call mayABSStop ;12
         fineC0:
             iret    
ISR_COUNT0   ENDP

sampleSpeed proc
            push ax    
                in al, 080h
                mov ah, 0
                cmp ax, 0
                jnz doDelta
                cmp ABSCount, 0
                jz doDelta
                mov ABSCount, 1
                call mayABSStop
                
        doDelta:xchg prevSample, ax
                sub ax, prevSample
                cmp ax, 5
                jg mayABS
                mov count, 0
                jmp exit_sampleSpeed
         mayABS:inc count
                cmp count, 4
                jne exit_sampleSpeed
                mov count, 3
                call goABS ;19
exit_sampleSpeed:
            pop ax
            ret    
sampleSpeed endp

goABS       proc
            push ax
                cmp ABSCount, 2
                je exit_goABS
                cmp ABSCount, 0
                jnz plusABS
                    mov al, 0FFh
                    out 81h, al
                inc ABSCount
                mov swTimer2, o
                ret
        plusABS:inc ABSCount
     exit_goABS:                ;10
            pop ax
            ret
goABS       endp

mayABSStop  proc
            push ax
                dec ABSCount
                cmp ABSCount, 0
                jnz anotherABS
                mov al, 000h
                out 81h, al
     anotherABS:mov swTimer2, 0 ;6
   fineStop:pop ax
            ret
mayABSStop  endp

            .startup    
            CLI         
            call INIT_IVT
	        call INIT_8259 
            call INIT_8253 
            call INIT_8255
            STI        
                        
block:      ;hlt
            jmp  block
                      
            .exit
end