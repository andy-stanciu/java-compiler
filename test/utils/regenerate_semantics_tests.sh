#!/bin/bash
# this script regenerates tests for semantics. WARNING: do not use unless you're 100% sure
# that you want to regenerate (and thus overwrite) all the existing semantics tests.
cd ../..
for file in ./test/resources/Semantics/*.java; do
  echo "Generating tests for ${file%.*}.java..."
  if [[ $file != *"Fail"* ]];
  then
    java -cp "build/classes;lib/*" MiniJava -T "$file" > "${file%.*}.tbl"
  else
    java -cp "build/classes;lib/*" MiniJava -T "$file" > "${file%.*}.tbl" 2> "${file%.*}.err"
  fi
done
exit 0