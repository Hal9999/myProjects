.model small

.data
    vett1   dw  6677h, 3344h
    vett2   dw  5566h, 1122h
    res     dd  ?
.code
.startup
    ;membro 1
    mov ax, vett1[0]    ;VETT1(0)
    mov bx, vett2[2]    ;VETT2(1)
    dec bx              ;VETT2(1)-1  divisore
    mov dx, 0           ;00:VETT1(0) dividendo
    div bx              ;(dx:ax)/bx
    mov dx, 0           ;kill the remainder
    mul ax              ;^2
    mov word ptr res,   ax
    mov word ptr res+2, dx ;conservo in res (32b)
    ;membro 2
    mov ax, vett1[2]    ;VETT1(1)
    neg ax              ;-VETT1(1)
    mov dx, 0           ;00:-VETT1(1)     dividendo
    mov bx, vett2[0]    ;VETT2(0)
    inc bx              ;VETT2(0)+1 divisore
    div bx              ;(dx:ax)/bx
    mov dx, 0           ;kill the remainder
    mul ax              ;^2
    sub word ptr res,   ax
    sbb word ptr res+2, dx
    jno noOF
        mov word ptr res,   0
        mov word ptr res+2, 0
    noOF:
.exit
end