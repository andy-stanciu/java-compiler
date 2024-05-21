    .data
victor$$:
    .quad   0
    .quad   victor$addTwo

    .text
    .globl  asm_main
asm_main:
    pushq   %rbp
    movq    %rsp,%rbp
    movq    $8,%rdi
    call    mjcalloc
    leaq    victor$$(%rip),%rdx
    movq    %rdx,0(%rax)
    pushq   %rax
    movq    $1,%rax
    pushq   %rax
    movq    $1,%rax
    pushq   %rax
    popq    %rdx
    popq    %rsi
    popq    %rdi
    movq    0(%rdi),%rax
    call    *8(%rax)
    movq    %rax,%rdi
    call    put
    movq    %rbp,%rsp
    popq    %rbp
    ret
victor$addTwo:
    pushq   %rbp
    movq    %rsp,%rbp
    subq    $32,%rsp
    movq    %rdi,-8(%rbp)
    movq    %rsi,-16(%rbp)
    movq    %rdx,-24(%rbp)
    movq    -16(%rbp),%rax
    pushq   %rax
    movq    -24(%rbp),%rax
    popq    %rdx
    addq    %rdx,%rax
    movq    %rbp,%rsp
    popq    %rbp
    ret
