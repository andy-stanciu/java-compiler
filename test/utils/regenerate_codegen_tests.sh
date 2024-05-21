#!/bin/bash
# this script regenerates tests for codegen. WARNING: do not use unless you're 100% sure
# that you want to regenerate (and thus overwrite) all the existing codegen tests.
cd ../..
for file in ./test/resources/CodeGen/*.java; do
  echo "Compiling ${file%.*}.java..."
  java -cp "build/classes;lib/*" MiniJava "$file" > "${file%.*}.S"
done
exit 0