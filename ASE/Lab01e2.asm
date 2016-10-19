;Si scriva un programma in linguaggio Assembler 8086 che esegua le seguenti operazioni: 
;a.  Definisca  una  matrice  5*5  chiamata  MATR  i  cui  elementi  siano  memorizzati  su  8  bit, 
;inizializzati a piacimento. 
;b.  Senza perdere precisione, si memorizzi in una locazione di memoria scelta la somma di tutti 
;gli elementi della riga 2 
;c.  Senza perdere precisione, si memorizzi in una locazione di memoria scelta la somma di tutti 
;gli elementi della colonna 3 
;d.  Esegua  la  differenza  dei  due  risultati  ottenuti  al  punto  b.  e  c.  e  la  si  memorizzi  in  una 
;locazione di memoria scelta. 

.model small
.data
    ;a
    rowsN       equ 5
    colsN       equ 5
    matr        db   1,  2,  3,  4,  5,
                db   6,  7,  8,  9, 10,
                db  11, 12 ,13 ,14 ,15,
                db  16, 17 ,18 ,19 ,20,
                db  21, 22 ,23 ,24 ,25
    sum2ndRow   dw  ?
    sum3rdCol   dw  ?
    sumsDiff    dw  ?
.code
    .startup
    ;b
    mov bx, 1*colsN         ;riga 2
    mov si, 0               ;parto dalla colonna 0
    mov cx, colsN           ;usato da loop e testato contro 0
    mov dx, 0               ;accumulatore a 0
    
    a_for:
        mov ah, 0               ;parte alta della word 0
        mov al, matr[bx][si]    ;nella parte passa il byte della matrice
        add dx, ax              ;somma a 16 bit
        add si, 1               ;colonna successiva, ovvero byte successivo
        loop a_for              ;dec cx e confronto con 0
    mov sum2ndRow, dx           ;sposto la somma finale nella variabile
    
    ;c
    mov bx, 0               ;parto dalla riga 0
    mov si, 2               ;colonna 3
    mov cx, rowsN           ;usato da loop e testato contro 0
    mov dx, 0               ;accumulatore a 0
    
    b_for:
        mov ah, 0               ;parte alta della word 0
        mov al, matr[bx][si]    ;nella parte passa il byte
        add dx, ax              ;somma a 16 bit
        add bx, colsN           ;riga successiva
        loop b_for              ;dec cx e confronto con 0
    mov sum3rdCol, dx
    
    ;d
    mov ax, sum3rdCol
    sub ax, sum2ndRow
    mov sumsDiff, ax
    .exit
end