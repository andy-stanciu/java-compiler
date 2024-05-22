#!/bin/bash
# this script regenerates tests for codegen. WARNING: do not use unless you're 100% sure
# that you want to regenerate (and thus overwrite) all the existing codegen tests.
cd ../..
for file in ./test/resources/Codegen/*.java; do
  echo "Compiling ${file%.*}.java..."
  filename=$(basename "$file")
  java -cp "build/classes;lib/*" MiniJava "$file" > "./test/resources/Codegen/out/${filename%.*}.S"
done
exit 0