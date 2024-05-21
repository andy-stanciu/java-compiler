    .data
A$$:
    .quad   0
    .quad   A$run
Point$$:
    .quad   0
    .quad   Point$getX
    .quad   Point$getY
    .quad   Point$init

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
    movq    %rbp,%rsp
    popq    %rbp
    ret
A$run:
    pushq   %rbp
    movq    %rsp,%rbp
    subq    $32,%rsp
    movq    %rdi,-8(%rbp)
    leaq    -16(%rbp),%rax
    pushq   %rax
    movq    $24,%rdi
    pushq   %rcx
    call    mjcalloc
    popq    %rcx
    leaq    Point$$(%rip),%rdx
    movq    %rdx,0(%rax)
    popq    %rdx
    movq    %rax,0(%rdx)
    leaq    -16(%rbp),%rax
    pushq   %rax
    movq    -16(%rbp),%rax
    pushq   %rax
    movq    $3,%rax
    pushq   %rax
    movq    $5,%rax
    pushq   %rax
    popq    %rdx
    popq    %rsi
    popq    %rdi
    movq    0(%rdi),%rax
    pushq   %rcx
    call    *24(%rax)
    popq    %rcx
    popq    %rdx
    movq    %rax,0(%rdx)
    movq    -16(%rbp),%rax
    pushq   %rax
    popq    %rdi
    movq    0(%rdi),%rax
    call    *8(%rax)
    pushq   %rax
    movq    -16(%rbp),%rax
    pushq   %rax
    popq    %rdi
    movq    0(%rdi),%rax
    pushq   %rcx
    call    *16(%rax)
    popq    %rcx
    popq    %rdx
    cmpq    %rax,%rdx
    jl      true0
    movq    $0,%rax
    jmp     else0
true0:
    movq    $1,%rax
join0:
    leaq    -24(%rbp),%rax
    pushq   %rax
    movq    -16(%rbp),%rax
    pushq   %rax
    popq    %rdi
    movq    0(%rdi),%rax
    pushq   %rcx
    call    *8(%rax)
    popq    %rcx
    popq    %rdx
    movq    %rax,0(%rdx)
    jmp     end_if0
else0:
    leaq    -24(%rbp),%rax
    pushq   %rax
    movq    -16(%rbp),%rax
    pushq   %rax
    popq    %rdi
    movq    0(%rdi),%rax
    pushq   %rcx
    call    *16(%rax)
    popq    %rcx
    popq    %rdx
    movq    %rax,0(%rdx)
end_if0:
    movq    -24(%rbp),%rax
    movq    %rbp,%rsp
    popq    %rbp
    ret
Point$init:
    pushq   %rbp
    movq    %rsp,%rbp
    subq    $32,%rsp
    movq    %rdi,-8(%rbp)
    movq    %rsi,-16(%rbp)
    movq    %rdx,-24(%rbp)
    movq    -8(%rbp),%rdx
    leaq    8(%rdx),%rax
    pushq   %rax
    movq    -16(%rbp),%rax
    popq    %rdx
    movq    %rax,0(%rdx)
    movq    -8(%rbp),%rdx
    leaq    16(%rdx),%rax
    pushq   %rax
    movq    -24(%rbp),%rax
    popq    %rdx
    movq    %rax,0(%rdx)
    movq    -8(%rbp),%rax
    movq    %rbp,%rsp
    popq    %rbp
    ret
Point$getX:
    pushq   %rbp
    movq    %rsp,%rbp
    subq    $16,%rsp
    movq    %rdi,-8(%rbp)
    movq    -8(%rbp),%rdx
    movq    8(%rdx),%rax
    movq    %rbp,%rsp
    popq    %rbp
    ret
Point$getY:
    pushq   %rbp
    movq    %rsp,%rbp
    subq    $16,%rsp
    movq    %rdi,-8(%rbp)
    movq    -8(%rbp),%rdx
    movq    16(%rdx),%rax
    movq    %rbp,%rsp
    popq    %rbp
    ret
