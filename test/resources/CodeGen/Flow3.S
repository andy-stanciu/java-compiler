    .data
Flow$$:
    .quad   0
    .quad   Flow$sideEffect1
    .quad   Flow$sideEffect2
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
    call    *24(%rax)
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
    movq    -8(%rbp),%rax
    pushq   %rax
    popq    %rdi
    movq    0(%rdi),%rax
    call    *8(%rax)
    cmpq    $0,%rax
    je      else0
    movq    -8(%rbp),%rax
    pushq   %rax
    popq    %rdi
    movq    0(%rdi),%rax
    call    *16(%rax)
    cmpq    $0,%rax
    je      else0
join0:
    movq    $7,%rax
    movq    %rax,%rdi
    call    put
    jmp     end_if0
else0:
    movq    $6,%rax
    movq    %rax,%rdi
    call    put
end_if0:
    movq    -8(%rbp),%rax
    pushq   %rax
    popq    %rdi
    movq    0(%rdi),%rax
    call    *16(%rax)
    cmpq    $0,%rax
    je      else1
    movq    -8(%rbp),%rax
    pushq   %rax
    popq    %rdi
    movq    0(%rdi),%rax
    call    *8(%rax)
    cmpq    $0,%rax
    je      else1
join1:
    movq    $4,%rax
    movq    %rax,%rdi
    call    put
    jmp     end_if1
else1:
    movq    $5,%rax
    movq    %rax,%rdi
    call    put
end_if1:
    movq    $3,%rax
    pushq   %rax
    movq    $2,%rax
    popq    %rdx
    cmpq    %rax,%rdx
    jl      true0
    movq    $0,%rax
    jmp     join3
true0:
    movq    $1,%rax
join3:
    cmpq    $0,%rax
    je      else2
    movq    -8(%rbp),%rax
    pushq   %rax
    popq    %rdi
    movq    0(%rdi),%rax
    call    *8(%rax)
    cmpq    $0,%rax
    je      else2
join2:
    movq    $9,%rax
    movq    %rax,%rdi
    call    put
    jmp     end_if2
else2:
    movq    $4,%rax
    pushq   %rax
    movq    $2,%rax
    popq    %rdx
    cmpq    %rax,%rdx
    jl      true1
    movq    $0,%rax
    jmp     join4
true1:
    movq    $1,%rax
join4:
    xorq    $1,%rax
    cmpq    $0,%rax
    je      else3
    movq    $3,%rax
    movq    %rax,%rdi
    call    put
    jmp     end_if3
else3:
    movq    $1,%rax
    movq    %rax,%rdi
    call    put
end_if3:
end_if2:
    jmp     while_test0
while0:
    movq    $333,%rax
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
    movq    $5,%rax
    popq    %rdx
    cmpq    %rax,%rdx
    jl      true2
    movq    $0,%rax
    jmp     join6
true2:
    movq    $1,%rax
join6:
    cmpq    $0,%rax
    je      join5
    movq    -8(%rbp),%rax
    pushq   %rax
    popq    %rdi
    movq    0(%rdi),%rax
    call    *8(%rax)
    xorq    $1,%rax
    cmpq    $0,%rax
    jne     while0
join5:
    movq    $0,%rax
    movq    %rbp,%rsp
    popq    %rbp
    ret
Flow$sideEffect1:
    pushq   %rbp
    movq    %rsp,%rbp
    subq    $16,%rsp
    movq    %rdi,-8(%rbp)
    movq    $111,%rax
    movq    %rax,%rdi
    call    put
    movq    $0,%rax
    movq    %rbp,%rsp
    popq    %rbp
    ret
Flow$sideEffect2:
    pushq   %rbp
    movq    %rsp,%rbp
    subq    $16,%rsp
    movq    %rdi,-8(%rbp)
    movq    $222,%rax
    movq    %rax,%rdi
    call    put
    movq    $1,%rax
    movq    %rbp,%rsp
    popq    %rbp
    ret
