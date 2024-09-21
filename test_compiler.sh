#!/bin/bash

# Place this script in your compiler's base directory. Remember to modify the constants below.
# To run this script:
# 1) Open Git Bash (this should be installed if you have Git)
# 2) cd into your compiler's base directory
# 3) run "./test_compiler"

##############################################################################################################
# Remember to modify these constants!
PATH_TO_JAVA_FILES="test/resources/CodeGen" # relative path to the directory containing java test files
ATTU="andys22@attu.cs.washington.edu"       # attu connection string
ATTU_PATH="~/java-compiler"                 # path to directory on attu containing boot.c and test_attu.sh
##############################################################################################################

# overwrite
if [ -d "${PATH_TO_JAVA_FILES}/out" ]; then
  rm -rf "${PATH_TO_JAVA_FILES}/out"
fi

mkdir -p "${PATH_TO_JAVA_FILES}/out"

if [ $# -eq 0 ]; then
  echo "Compiling Java programs..."
  echo ""
  for file in ${PATH_TO_JAVA_FILES}/src/*.java; do
    filename=$(basename "$file")
    echo "${filename} -> ${filename%.*}.S"
    java -cp 'build/classes:lib/*' Java "$file" > "${PATH_TO_JAVA_FILES}/out/${filename%.*}.S"
  done

  echo ""
  echo "Sending Java programs to attu..."
  echo ""
  scp -r "${PATH_TO_JAVA_FILES}/out" "${ATTU}:${ATTU_PATH}"

  echo ""
  echo "Sending Java source files to attu..."
  echo ""
  scp -r "${PATH_TO_JAVA_FILES}/src" "${ATTU}:${ATTU_PATH}"
elif [ $# -eq 1 ]; then
  echo "Compiling $1.java..."
  echo ""
  echo "$1.java -> $1.S"
  java -cp 'build/classes:lib/*' Java "${PATH_TO_JAVA_FILES}/src/$1.java" > "${PATH_TO_JAVA_FILES}/out/$1.S"

  echo ""
  echo "Sending Java programs to attu..."
  echo ""

  scp "${PATH_TO_JAVA_FILES}/out/$1.S" "${ATTU}:${ATTU_PATH}/out/"

  echo ""
  echo "Sending Java source files to attu..."
  echo ""
  scp "${PATH_TO_JAVA_FILES}/src/$1.java" "${ATTU}:${ATTU_PATH}/src/"
else
  echo "Usage: ./test_compiler [source]"
  exit 1
fi

echo ""
echo "Comparing diff on attu..."
ssh -t "${ATTU}" "cd ${ATTU_PATH}; ./test_attu.sh"
echo "Done!"

echo ""
echo "Fetching results..."
scp "${ATTU}:${ATTU_PATH}/report.txt" "."
echo ""
cat report.txt

# cleanup
rm report.txt

exit 0