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
    movq    $4,%rax
    pushq   %rax
    movq    $4,%rax
    popq    %rdx
    cmpq    %rax,%rdx
    jl      true0
    movq    $0,%rax
    jmp     else0
true0:
    movq    $1,%rax
join0:
    movq    $0,%rax
    movq    %rax,%rdi
    call    put
    jmp     end_if0
else0:
    movq    $6,%rax
    pushq   %rax
    movq    $5,%rax
    popq    %rdx
    cmpq    %rax,%rdx
    jl      true1
    movq    $0,%rax
    jmp     else1
true1:
    movq    $1,%rax
join1:
    movq    $1,%rax
    movq    %rax,%rdi
    call    put
    jmp     end_if1
else1:
    movq    $0,%rax
    pushq   %rax
    movq    $1,%rax
    popq    %rdx
    cmpq    %rax,%rdx
    jl      true2
    movq    $0,%rax
    jmp     else2
true2:
    movq    $1,%rax
join2:
    movq    $2,%rax
    movq    %rax,%rdi
    call    put
    jmp     end_if2
else2:
    movq    $3,%rax
    movq    %rax,%rdi
    call    put
end_if2:
end_if1:
end_if0:
    movq    $0,%rax
    movq    %rbp,%rsp
    popq    %rbp
    ret
