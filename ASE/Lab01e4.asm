.model small
.data
    ;a
    vettLen     dw  4
    vett        dd  FF332211h, 01001000h, 0000FF1Fh, 00000001h
    sum         dd  00000000h
.code
    .startup
    mov cx, vettLen
    mov dx, 0
    lea si, vett
    for:
        mov ax, [si]+2
        test ax, ax
        js isNeg
            ;32bit sum
            mov ax, word ptr [si]
            mov bx, word ptr [si]+2
            add word ptr sum, ax
            adc word ptr sum+2, bx
            jno noOF
                ;OF
                mov word ptr sum, 0 
                mov word ptr sum+2, 0
                jmp done
            noOF:
        isNeg:
        add si, 4
    loop for
    done:
    .exit
end