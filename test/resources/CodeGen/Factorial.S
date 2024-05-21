    .data
Fac$$:
    .quad   0
    .quad   Fac$ComputeFac

    .text
    .globl  asm_main
asm_main:
    pushq   %rbp
    movq    %rsp,%rbp
    movq    $8,%rdi
    call    mjcalloc
    leaq    Fac$$(%rip),%rdx
    movq    %rdx,0(%rax)
    pushq   %rax
    movq    $10,%rax
    pushq   %rax
    popq    %rsi
    popq    %rdi
    movq    0(%rdi),%rax
    call    *8(%rax)
    movq    %rax,%rdi
    call    put
    movq    %rbp,%rsp
    popq    %rbp
    ret
Fac$ComputeFac:
    pushq   %rbp
    movq    %rsp,%rbp
    subq    $32,%rsp
    movq    %rdi,-8(%rbp)
    movq    %rsi,-16(%rbp)
    movq    -16(%rbp),%rax
    pushq   %rax
    movq    $1,%rax
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
    movq    $1,%rax
    popq    %rdx
    movq    %rax,0(%rdx)
    jmp     end_if0
else0:
    leaq    -24(%rbp),%rax
    pushq   %rax
    movq    -16(%rbp),%rax
    pushq   %rax
    movq    -8(%rbp),%rax
    pushq   %rax
    movq    -16(%rbp),%rax
    pushq   %rax
    movq    $1,%rax
    popq    %rdx
    movq    %rax,%rcx
    subq    %rcx,%rdx
    movq    %rdx,%rax
    pushq   %rax
    popq    %rsi
    popq    %rdi
    movq    0(%rdi),%rax
    call    *8(%rax)
    popq    %rdx
    imulq   %rdx,%rax
    popq    %rdx
    movq    %rax,0(%rdx)
end_if0:
    movq    -24(%rbp),%rax
    movq    %rbp,%rsp
    popq    %rbp
    ret
