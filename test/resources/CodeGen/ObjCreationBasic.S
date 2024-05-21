    .data
A$$:
    .quad   0
    .quad   A$print1
    .quad   A$print2
    .quad   A$print3

    .text
    .globl  asm_main
asm_main:
    pushq   %rbp
    movq    %rsp,%rbp
    movq    $8,%rdi
    call    mjcalloc
    leaq    A$$(%rip),%rdx
    movq    %rdx,0(%rax)
    pushq   %rax
    popq    %rdi
    movq    0(%rdi),%rax
    call    *8(%rax)
    movq    %rax,%rdi
    call    put
    movq    $8,%rdi
    call    mjcalloc
    leaq    A$$(%rip),%rdx
    movq    %rdx,0(%rax)
    pushq   %rax
    popq    %rdi
    movq    0(%rdi),%rax
    call    *16(%rax)
    movq    %rax,%rdi
    call    put
    movq    $8,%rdi
    call    mjcalloc
    leaq    A$$(%rip),%rdx
    movq    %rdx,0(%rax)
    pushq   %rax
    popq    %rdi
    movq    0(%rdi),%rax
    call    *24(%rax)
    movq    %rax,%rdi
    call    put
    movq    %rbp,%rsp
    popq    %rbp
    ret
A$print1:
    pushq   %rbp
    movq    %rsp,%rbp
    subq    $16,%rsp
    movq    %rdi,-8(%rbp)
    movq    $1,%rax
    movq    %rax,%rdi
    call    put
    movq    $0,%rax
    movq    %rbp,%rsp
    popq    %rbp
    ret
A$print2:
    pushq   %rbp
    movq    %rsp,%rbp
    subq    $16,%rsp
    movq    %rdi,-8(%rbp)
    movq    $2,%rax
    movq    %rax,%rdi
    call    put
    movq    $0,%rax
    movq    %rbp,%rsp
    popq    %rbp
    ret
A$print3:
    pushq   %rbp
    movq    %rsp,%rbp
    subq    $16,%rsp
    movq    %rdi,-8(%rbp)
    movq    $3,%rax
    movq    %rax,%rdi
    call    put
    movq    $0,%rax
    movq    %rbp,%rsp
    popq    %rbp
    ret
