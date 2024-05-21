    .data

    .text
    .globl  asm_main
asm_main:
    pushq   %rbp
    movq    %rsp,%rbp
    movq    $5,%rax
    pushq   %rax
    addq    $1,%rax
    shlq    $3,%rax
    movq    %rax,%rdi
    pushq   %rcx
    call    mjcalloc
    popq    %rcx
    popq    %rdx
    movq    %rdx,0(%rax)
    movq    0(%rax),%rax
    movq    %rax,%rdi
    call    put
    movq    %rbp,%rsp
    popq    %rbp
    ret
