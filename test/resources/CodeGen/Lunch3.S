    .data

    .text
    .globl  asm_main
asm_main:
    pushq   %rbp
    movq    %rsp,%rbp
    movq    $5,%rax
    pushq   %rax
    movq    $4,%rax
    popq    %rdx
    imulq   %rdx,%rax
    pushq   %rax
    movq    $3,%rax
    popq    %rdx
    imulq   %rdx,%rax
    pushq   %rax
    movq    $2,%rax
    popq    %rdx
    imulq   %rdx,%rax
    pushq   %rax
    movq    $1,%rax
    popq    %rdx
    imulq   %rdx,%rax
    movq    %rax,%rdi
    call    put
    movq    %rbp,%rsp
    popq    %rbp
    ret
