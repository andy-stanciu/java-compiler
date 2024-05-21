    .data
Flow$$:
    .quad   0
    .quad   Flow$test

    .text
    .globl  asm_main
asm_main:
    pushq   %rbp
    movq    %rsp,%rbp
    movq    $8,%rdi
    call    mjcalloc
    leaq    Flow$$(%rip),%rdx
    movq    %rdx,0(%rax)
    pushq   %rax
    popq    %rdi
    movq    0(%rdi),%rax
    call    *8(%rax)
    movq    %rax,%rdi
    call    put
    movq    %rbp,%rsp
    popq    %rbp
    ret
Flow$test:
    pushq   %rbp
    movq    %rsp,%rbp
    subq    $16,%rsp
    movq    %rdi,-8(%rbp)
    leaq    -16(%rbp),%rax
    pushq   %rax
    movq    $0,%rax
    popq    %rdx
    movq    %rax,0(%rdx)
    jmp     while_test0
while0:
    movq    -16(%rbp),%rax
    movq    %rax,%rdi
    call    put
    leaq    -16(%rbp),%rax
    pushq   %rax
    movq    -16(%rbp),%rax
    pushq   %rax
    movq    $1,%rax
    popq    %rdx
    addq    %rdx,%rax
    popq    %rdx
    movq    %rax,0(%rdx)
while_test0:
    movq    -16(%rbp),%rax
    pushq   %rax
    movq    $10,%rax
    popq    %rdx
    cmpq    %rax,%rdx
    jl      true0
    movq    $0,%rax
    jmp     join0
true0:
    movq    $1,%rax
    jmp     while0
join0:
    movq    $0,%rax
    movq    %rbp,%rsp
    popq    %rbp
    ret
