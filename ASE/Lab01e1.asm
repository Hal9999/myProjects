;Si  scriva  un  programma  in  linguaggio
;Assembler  8086  che  inizializzi  a  FFh  tutti  i  registri 
;general purpose a disposizione.

.MODEL small

.DATA
.CODE
.STARTUP
    mov ah, 0FFh
    mov bh, 0FFh
    mov ch, 0FFh
    mov dh, 0FFh
    mov al, 0FFh
    mov bl, 0FFh
    mov cl, 0FFh
    mov dl, 0FFh
.EXIT

END