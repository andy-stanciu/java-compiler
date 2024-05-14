#!/bin/bash
# this script regenerates tests for the scanner. WARNING: do not use unless you're 100% sure
# that you want to regenerate (and thus overwrite) all the existing scanner tests.
cd ../..
for file in ./test/resources/Scanner/*.java; do
  echo "Generating tests for ${file%.*}.java..."
  if [[ $file != *"Broken"* ]];
  then
    java -cp "build/classes;lib/*" MiniJava -S "$file" > "${file%.*}.expected"
  else
    java -cp "build/classes;lib/*" MiniJava -S "$file" > "${file%.*}.expected" 2> "${file%.*}.err"
  fi
done
exit 0