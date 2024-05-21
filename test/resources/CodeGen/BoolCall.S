    .data
Test$$:
    .quad   0
    .quad   Test$check
    .quad   Test$init
    .quad   Test$run

    .text
    .globl  asm_main
asm_main:
    pushq   %rbp
    movq    %rsp,%rbp
    movq    $16,%rdi
    call    mjcalloc
    leaq    Test$$(%rip),%rdx
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
Test$run:
    pushq   %rbp
    movq    %rsp,%rbp
    subq    $16,%rsp
    movq    %rdi,-8(%rbp)
    leaq    -16(%rbp),%rax
    pushq   %rax
    movq    -8(%rbp),%rax
    pushq   %rax
    popq    %rdi
    movq    0(%rdi),%rax
    pushq   %rcx
    call    *16(%rax)
    popq    %rcx
    popq    %rdx
    movq    %rax,0(%rdx)
    movq    -8(%rbp),%rax
    pushq   %rax
    popq    %rdi
    movq    0(%rdi),%rax
    call    *8(%rax)
    cmpq    $0,%rax
    je      else0
    movq    $0,%rax
    movq    %rax,%rdi
    call    put
    jmp     end_if0
else0:
    movq    $1,%rax
    movq    %rax,%rdi
    call    put
end_if0:
    movq    $1000000,%rax
    movq    %rbp,%rsp
    popq    %rbp
    ret
Test$init:
    pushq   %rbp
    movq    %rsp,%rbp
    subq    $16,%rsp
    movq    %rdi,-8(%rbp)
    movq    -8(%rbp),%rdx
    leaq    8(%rdx),%rax
    pushq   %rax
    movq    $0,%rax
    popq    %rdx
    movq    %rax,0(%rdx)
    movq    $1,%rax
    movq    %rbp,%rsp
    popq    %rbp
    ret
Test$check:
    pushq   %rbp
    movq    %rsp,%rbp
    subq    $16,%rsp
    movq    %rdi,-8(%rbp)
    movq    -8(%rbp),%rdx
    movq    8(%rdx),%rax
    movq    %rbp,%rsp
    popq    %rbp
    ret
