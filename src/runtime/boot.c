#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <inttypes.h>

extern void asm_main();   /* main function in compiled code */
                          /* change function name if your   */
                          /* compiled main has a different label */

/* Write x to standard output followed by a newline */
void put(int64_t x) {
  printf("%" PRId64 "\n", x);
}

/*
 *  mjcalloc returns a pointer to a chunk of memory with at least
 *  num_bytes available.  Returned storage has been zeroed out.
 *  Return NULL if attempt to allocate memory fails or if num_bytes
 *  is 0.
 */

void * mjcalloc(size_t num_bytes) {
  return (calloc(1, num_bytes));
}

/* Write array exception to standard error and exit with failure */
void exception_array(int64_t x, int64_t s, int64_t l) {
  fprintf(stderr,
          "Error on line %" PRId64 ": Array index %" PRId64
          " out of bounds for array with length %" PRId64 "\n",
          l, x, s);
  exit(1);
}

/* Write negative array size exception to standard error and exit with failure */
void exception_array_size(int64_t s, int64_t l) {
  fprintf(stderr,
          "Error on line %" PRId64 ": Array size %" PRId64 " cannot be negative\n", l, s);
  exit(1);
}

/* Write division exception to standard error and exit with failure */
void exception_division(int64_t l) {
  fprintf(stderr, "Error on line %" PRId64 ": Division by zero\n", l);
  exit(1);
}

/* Execute compiled program asm_main */
int main() {
  asm_main();
  return 0;
}
