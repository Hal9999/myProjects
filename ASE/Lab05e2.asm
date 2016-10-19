    #start=8259.exe#
                 
;GCD(2 ms * 120 MHz, 45 us * 120 MHz)
;GCD(240000, 5400) = 600 = 5 us
;45 us = 9 * 5 us
;2 ms = 400 * 5 us
C0TICKS equ 600
T1TICKS equ 9
T2TICKS equ 400

    .model small          
    .data
        swTimer1        dw 0
        swTimer2        dw 0     
        samplesStats0   db 256 dup(0)           ;buffer 0
        samplesStats1   db 256 dup(0)           ;buffer 1
        statsOffset     dw offset samplesStats0 ;offset del buffer per l'input dei 2 ms correnti
        statsIndex      dw 0                    ;id del buffer per l'input
        statsOutOff     dw 0                    ;id del buffer per l'output
        searchIndex     dw 0                    ;posizione corrente nel buffer di output
    .stack     
    .code   
INIT_IVT	PROC
    		PUSH 	AX
    		PUSH	BX
    		PUSH	DX
    		PUSH 	DS
        		XOR	AX, AX
        		MOV	DS, AX                 		     				
        		MOV	BX, 36 ;channel 4		
        		SHL	BX, 2		
        		MOV AX, offset ISR_PB_OUT
        		MOV	DS:[BX], AX
        		MOV	AX, seg ISR_PB_OUT
        		MOV	DS:[BX+2], AX       		
        		MOV	BX, 35 ;channel 3		
        		SHL	BX, 2		
        		MOV AX, offset ISR_COUNT0
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
                mov al, 10010100b ;porta A modo 0 input, porta B modo 1 output 
                out 083h, al 
                mov al, 00000101b ;set PC2 to enable interrupt on PB in or PB out
                out 083h, al   
            RET            
INIT_8255   ENDP          

INIT_8253   PROC
                mov al, 00110100b ;counter0 init
                out 063h, al 
                mov ax, C0TICKS   ;counter0 value
                out 060h, al
                xchg al, ah
                out 060h, al
            RET
INIT_8253   ENDP    
     
INIT_8259   PROC
            PUSH AX
                MOV	AL, 00011011b ;ICW1: level triggered, single 8259, ICW4 yes
    	        OUT	40h, AL
    	        MOV	AL, 00100000b ;ICW2: start from INTR 32
    	        OUT	41h, AL
    	        MOV AL, 00000011b ;ICW4: full nested mode, buf mode, master, AEOI
    	        OUT 41h, AL
    	        MOV AL, 11100111b ;OCW1: channels 3, 4 enabled
        	    OUT 41h, AL
            POP AX
	        RET
INIT_8259   ENDP        
                              
ISR_COUNT0  PROC
                inc swTimer1
                cmp swTimer1, T1TICKS
                jne noT1Timeout
                    mov swTimer1, 0
                    call sampleInput
    noT1Timeout:
                inc swTimer2
                cmp swTimer2, T2TICKS
                jne noT2Timeout
                    mov swTimer2, 0
                    call sendData
    noT2Timeout:
            IRET    
ISR_COUNT0  ENDP

ISR_PB_OUT  PROC
            push ax
            push bx
            push bp
            push si
                mov bp, statsOutOff     ;buffer di output
                mov si, searchIndex     ;prima posizione da controllare 
     searchNext:    cmp si, 256
                    je _ISR_PB_OUT      ;non ho trovato altro, quindi esco e ci rivediamo tra 2 ms
                    mov bx, [bp][si]
                    cmp bx, 2
                    jae nextFound
                    inc si              ;passo al prossimo elemento
                    jmp searchNext       
      nextFound:mov ax, si
                out 081h, al
                inc si                  ;la ricerca ripartira' dal prossimo elemento
                mov searchIndex, si 
_ISR_PB_OUT:pop si
            pop bp
            pop bx
            pop ax
            IRET    
ISR_PB_OUT  ENDP

sampleInput proc
            push ax
            push bx
                in al, 080h
                mov ah, 0
                mov bx, statsOffset
                add bx, ax
                inc [bx]
            pop bx
            pop ax
            ret
sampleInput endp

sendData    proc
            push ax
            push bx
            push cx
            push bp
            push si
                ;mi proteggo contro irq da timer 0 mentre swappo tra i buffer
                cli
				cmp statsIndex, 1
                je switchTo0th
                mov statsIndex, 1
                lea bx, samplesStats1
                lea bp, samplesStats0
                jmp refreshOffset  
    switchTo0th:mov statsIndex, 0
                lea bx, samplesStats0
                lea bp, samplesStats1 
  refreshOffset:mov statsOffset, bx
                mov statsOutOff, bp 
                ;ora azzero il buffer e dovrei essere ancora protetto :-\
      resetBuff:mov cx, 256
                    mov [bx], 0
                    inc bx
                    loop resetBuff
				sti
                ;swappato e resettato, adesso posso iniziare l'output su 8255 e posso farmi interrompere da altri irq di timer0
                mov si, 0               ;in si ho l'offset rispetto a bp
    searchFirst:    cmp si, 256
                    je _sendData        ;non ho trovato nulla, quindi esco e ci rivediamo tra 2 ms
                    mov bx, [bp][si]
                    cmp bx, 2
                    jae foundFirst
                    inc si              ;passo al prossimo elemento
                    jmp searchFirst
     foundFirst:mov ax, si
                out 081h, al
                inc si                  ;la ricerca ripartira' dal prossimo elemento
                mov searchIndex, si
  _sendData:pop si
            pop bp
            pop cx
            pop bx
            pop ax
            ret
sendData    endp

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