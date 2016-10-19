;Si scriva un programma in linguaggio Assembler 8086 che esegua le seguenti operazioni: 
;a.  Definisca  due  variabili  in  memoria,  VARW  su  16  bit  e  VARD  su  32  bit,  inizializzati 
;rispettivamente a FFFFH e 00112233H 
;b.  Scambi i due byte più significativi di VARD con VARW 
;c.  Esegua la somma di VARW e VARD (valori dopo lo scambio) memorizzandola in VARD 
;n.b. si utilizzino il metodo di indirizzamento base relative addressing e l’istruzione LEA

.model small
.data
    ;a
    varW    DW  0FFFFh
    varD    DD  000112233h
.code
    .startup
    ;b
	mov ax, word ptr varD+2
	mov bx, varW
	mov word ptr varD+2, bx
	mov varW, ax
	
	;c
	mov ax, word ptr varD
	add ax, varW
	mov word ptr varD, ax
	mov ax, word ptr varD+2
	adc ax, 0000h
	mov word ptr varD+2, ax
	.exit
end       



;era da fare con LEA e base relative addressing