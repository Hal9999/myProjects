;Si scriva un programma in linguaggio Assembler 8086 che esegua le seguenti operazioni: 
;a.  Definisca  due  variabili  in  memoria,  VARW  su  16  bit  e  VARD  su  32  bit,  inizializzati 
;rispettivamente a FFFFH e 00112233H 
;b.  Scambi i due byte piu' significativi di VARD con VARW 
;c.  Esegua la somma di VARW e VARD (valori dopo lo scambio) memorizzandola in VARD 
;n.b. si utilizzino il metodo di indirizzamento base relative addressing e l'istruzione LEA

.model small
.data
    ;a
    varW    DW  0FFFFh
    varD    DD  00112233h
.code
    .startup
    ;b
    lea bx, varD
    mov ax, [bx]+2
    mov cx, varW
    mov [bx]+2, cx
    mov varW, ax
	
	;c
	lea bx, varD
	mov ax, [bx]
	add ax, varW
	jo OF
	retOF:
	mov [bx], ax
    .exit
    
	OF:
        mov cx, [bx]+2
        inc cx
        mov [bx]+2, cx
        jmp retOF
end