.model small
.data
    var dw  0FEFFh
    ris db  ?
.code
.startup
    mov cx, 16
    mov ax, [var]
    mov bx, 0
    for:
        ror ax, 1
        adc bl, 0
        loop for
    mov [ris], bl
.exit
end