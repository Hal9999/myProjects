 .model small
 
 .data
    fib    dw  20 dup(?)
 
 .code
 .startup
    mov [fib+38], 0
    mov [fib+36], 1
    
    lea bx, fib+38
    lea si, fib+34
    mov cx, 18
    for:
        mov ax, [bx]
        sub bx, 2
        add ax, [bx]
        mov [si], ax
        sub si, 2
        
        loop for
 .exit
 end