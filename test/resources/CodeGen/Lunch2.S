    .data

    .text
    .globl  asm_main
asm_main:
    pushq   %rbp
    movq    %rsp,%rbp
    movq    $14,%rax
    pushq   %rax
    movq    $11,%rax
    popq    %rdx
    movq    %rax,%rcx
    subq    %rcx,%rdx
    movq    %rdx,%rax
    movq    %rax,%rdi
    call    put
    movq    %rbp,%rsp
    popq    %rbp
    ret
