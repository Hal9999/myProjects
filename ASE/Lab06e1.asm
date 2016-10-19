    #start=8259.exe#
;100 ms * 40 KHz = 4000
C0TICKS         equ 4000    ;100 ms
swT1000TICKS    equ 10      ;1 s
swT500TICKS     equ 5       ;500 ms
    
    .model small          
    .data     
        sampleLSB       db ?
        lsbInFull       db 0
        buffer          dw 601 dup(0)   ;buffer circolare
        head            dw 0            ;prima posizione libera
        tail            dw 600          ;ultima posizione libera: se t+1=h ho buffer vuoto, se t=h ho buffer pieno, ma il testo garantisce che non accade che il buffer si riempia
        doHomework      db 0            ;se 1, allora la procedura di output viene avviata al prossimo input di msb
        avg             dw ?
        sendStatus      db ?
        systemStatus    db 0            ;0: acquisizione dati, 1: 500 ms con no input e fine output, 2: 100 ms con out 0ffh, 3: invia 000h e dopo ack spegne tutto
        swT1000         dw 0
        swT500          dw 0
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

ISR_PA_IN   PROC ;24
            push ax
            push bx
                in al, 80h
                cmp lsbInFull, 1
                je isMSB
                mov sampleLSB, al
                mov lsbInFull, 1
                jmp _ISR_PA_IN
          isMSB:mov ah, sampleLSB
                xchg al, ah                 ;in ax ho il dato da accodare
                cmp ax, 0FFFFh
                jne continueAdd
                mov al, 11100111b           ;OCW1: disabilito in canale 7 di PAin
        	    out 41h, al
                mov systemStatus, 1
                jmp _ISR_PA_IN
    continueAdd:mov lsbInFull, 0
                mov bx, head
                mov buffer[bx], ax
                inc bx
                cmp bx, 601
                jne noHeadReset1
                mov bx, 0
   noHeadReset1:mov head, bx
                cmp doHomework, 1
                jne _ISR_PA_IN
                call sendData
 _ISR_PA_IN:pop bx                    
            pop ax
            IRET    
ISR_PA_IN   ENDP                      
                               
ISR_COUNT0  PROC ;26
            push ax
                cmp systemStatus, 0             ;in base al valore di systemStatus valuto l'opportuno counter
                jne maySS1                      ;se scade il timer tra lsb e msb allora setto una variabile e quando arriva msb chiamo la procedura di calcolo della media da ISR_PA_IN
                inc swT1000
                cmp swT1000, swT1000TICKS
                jne _ISR_COUNT0
                mov swT1000, 0
                cmp lsbInFull, 0
                je goNow
                mov doHomework, 1
                jmp _ISR_COUNT0
          goNow:call sendData
                jmp _ISR_COUNT0
         maySS1:cmp systemStatus, 1             ;fine del conteggio principale e degli input (da inibire con 8259), ma si completa l'output
                jne maySS2
                inc swT500
                cmp swT500, swT500TICKS
                jne _ISR_COUNT0
                mov systemStatus, 2
                mov al, 0FFh
                out 081h, al
                jmp _ISR_COUNT0
         maySS2:cmp systemStatus, 2
                jne _ISR_COUNT0
                mov systemStatus, 3
                mov al, 00h
                mov 081h, al
_ISR_COUNT0:pop ax
            IRET    
ISR_COUNT0  ENDP

ISR_PB_OUT  PROC ;24
            push ax
                cmp systemStatus, 2
                je outS2
                cmp systemStatus, 3
                je outS3
                cmp sendStatus, 0
                jne may1
                mov sendStatus, 1
                mov al, byte ptr avg
                out 81h, al
                jmp _ISR_PB_OUT
           may1:cmp sendStatus, 1
                jne may2
                mov sendStatus, 2
                mov al, byte ptr avg+1
                out 81h, al
                jmp _ISR_PB_OUT
           may2:mov al, 0
                out 81h, al
                jmp _ISR_PB_OUT
          outS2:mov al, 0FFh
                out 81h, al
                jmp _ISR_PB_OUT
          outS3:mov al, 11111111b           ;OCW1: disabilito tutti gli irq
        	    out 41h, al
_ISR_PB_OUT:pop ax
            IRET    
ISR_PB_OUT  ENDP

sendData    proc ;40
            push ax
            push cx
            push dx
            push bp
            push si
                ;calcolo la media: scorro da tail+1(compreso) a head(non compreso), conservo il valore di head in bp e alla fine setto t=h
                mov bp, head
                ;QUI: con normal mode posso indicare che sono uscito dalla parte critica della ISR e quindi posso ricevere altri IRQ
                mov si, tail
                mov ax, 0           ;somma lsb
                mov dx, 0           ;somma msb
                mov cx, 0           ;numero di elementi presenti nel buffer tra h e t
        avgLoop:inc si
                cmp si, 601
                jne noTailReset1
                mov si, 0
   noTailReset1:cmp si, bp          ;controllo se ho raggiunto la testa (t+1=h)
                je endSum           ;ho finito di sommare
                clc
                add ax, buffer[si]
                adc dx, 0           ;sommo su doubleword
                inc cx
                jmp avgLoop
         endSum:cmp cx, 0           ;se non ho ricevuto alcun dato ho finito, non mando proprio nulla su pbout (non ha senso calcolare la media di 0 valori)
                je _sendData
                idiv cx             ;calcolo la media, Q in ax
                mov avg, ax         ;la media viene inviata da pbout
                mov si, tail
                mov cx, 0           ;numero elementi nel buffer maggiori della media
      countLoop:inc si
                cmp si, 601
                jne noTailReset2
                mov si, 0
   noTailReset2:cmp si, bp
                je endCount
                cmp ax, buffer[si]  
                jge countLoop
                inc cx              ;conto i valori superiori alla media
                jmp countLoop
       endCount:dec bp              ;aggiorno tail a head-1
                cmp bp, 0
                jge noHeadReset2
                mov bp, 0
   noHeadReset2:mov tail, bp        
                mov sendStatus, 0   ;prossima cosa da fare: 0 msb avg, 1 lsb avg, 2 00h
                mov al, cl
                out 81h, al         ;mando in out il conteggio dei maggiori di avg
  _sendData:pop si
            pop bp
            pop dx
            pop cx
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
end ;114