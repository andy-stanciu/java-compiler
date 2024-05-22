#!/bin/bash
# this script regenerates tests for the parser. WARNING: do not use unless you're 100% sure
# that you want to regenerate (and thus overwrite) all the existing parser tests.
cd ../..
for file in ./test/resources/Parser/*.java; do
  if [[ $file != *"Broken"* ]];
  then
    echo "Generating ${file%.*}.ast..."
    java -cp "build/classes;lib/*" MiniJava -A "$file" > "${file%.*}.ast"
    echo "Generating ${file%.*}.pretty..."
    java -cp "build/classes;lib/*" MiniJava -P "$file" > "${file%.*}.pretty"
  fi
done
exit 0