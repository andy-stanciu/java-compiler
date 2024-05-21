    .data
Driver$$:
    .quad   0
    .quad   Driver$populate

    .text
    .globl  asm_main
asm_main:
    pushq   %rbp
    movq    %rsp,%rbp
    movq    $8,%rdi
    call    mjcalloc
    leaq    Driver$$(%rip),%rdx
    movq    %rdx,0(%rax)
    pushq   %rax
    movq    $5,%rax
    pushq   %rax
    addq    $1,%rax
    shlq    $3,%rax
    movq    %rax,%rdi
    call    mjcalloc
    popq    %rdx
    movq    %rdx,0(%rax)
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
Driver$populate:
    pushq   %rbp
    movq    %rsp,%rbp
    subq    $16,%rsp
    movq    %rdi,-8(%rbp)
    movq    %rsi,-16(%rbp)
    leaq    -16(%rbp),%rax
    pushq   0(%rax)
    movq    $2,%rax
    popq    %rdx
    movq    %rax,%rdi
    movq    0(%rdx),%rsi
    cmpq    $0,%rdi
    jl      exception_array
    cmpq    %rsi,%rdi
    jge     exception_array
    addq    $1,%rdi
    pushq   %rdx
    pushq   %rdi
    movq    $3,%rax
    popq    %rdi
    popq    %rdx
    movq    %rax,(%rdx,%rdi,8)
    movq    -16(%rbp),%rax
    pushq   %rax
    movq    $2,%rax
    popq    %rdx
    movq    %rax,%rdi
    movq    0(%rdx),%rsi
    cmpq    $0,%rdi
    jl      exception_array
    cmpq    %rsi,%rdi
    jge     exception_array
    addq    $1,%rdi
    movq    (%rdx,%rdi,8),%rax
    movq    %rbp,%rsp
    popq    %rbp
    ret
