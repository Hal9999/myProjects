    N equ 5
    .model small          
    
    .data
        vett    db 15, 18, 2, 23, 78;N dup(?)
        count   dw N
        min     db 0
        max     db 0
        avg     db 0
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
					
		POP	DS
		POP	DX
		POP	BX
		POP	AX
		RET
INIT_IVT	ENDP

; ISR for reading the value received on PA            
ISR_PA_IN   PROC   
    in   al, 080h
    mov  cx, count
    jcxz break
    dec cx
    mov count, cx
    mov si, cx
    mov vett[si], al
    break:
    IRET    
ISR_PA_IN   ENDP             
        
            
    
    .startup    
    CLI         
    ;call INIT_IVT
    STI            

    mov al, 10110000b ; GA(m=1,pa=in,pcu=hndsk/intrpt) ; GB(m=1,pb=out,pcl=hndsk/intrpt)  
    ;out 083h, al
    ; set PC4 to enable interrupt on PA in
    mov al, 00001001b    
    ;out 083h, al   
    ; set PC2 to enable interrupt on PB in or PB out
    ;mov al, 00000101b 
    ;out 083h, al   
    ; set PC6 to enable interrupt on PA out
    ;mov al, 00001101b 
    ;out 083h, al
    
              
    next:
        ;mov cx, count
        ;jcxz compute
        ;jmp next
		
    compute:
        mov si, N-1
        mov cl, vett[si]    ;min
        mov ch, cl          ;max
        mov ah, 0           ;
        mov al, cl          ;sum (16b)
        mov bh, 0
        for:
            dec si
            mov bl, vett[si]
            add ax, bx      ;sum
            cmp bl, cl
            jae noMin       ;j if bl(curr)>=cl(min)
                mov cl, bl
            noMin:
            cmp bl, ch    
            jbe noMax       ;j if bl(curr)<=ch(max)
                mov ch, bl
            noMax:
            cmp si, 0
            jg for
        mov min, cl
        mov max, ch
        mov cl, N   
        div cl
        mov avg, al

		out 081h, al
          
    ;hlt
    
    .exit
end  ; set entry point and stop the assembler.
