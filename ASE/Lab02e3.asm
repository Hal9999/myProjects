.model small
.data
    VETT db 4, 2, 3
.code
.startup
    mov al, VETT[0]
    mov bl, VETT[1]
    cmp al, bl
    jng noG
        mov VETT[1], al
        mov VETT[0], bl
    noG:
    
    mov al, VETT[1]
    mov bl, VETT[2]
    cmp al, bl
    jng noG2
        mov VETT[2], al
        mov VETT[1], bl
    noG2:
    
    mov al, VETT[0]
    mov bl, VETT[1]
    cmp al, bl
    jng noG3
        mov VETT[1], al
        mov VETT[0], bl
    noG3: 
.exit
end