#!/bin/bash

# Place this script in your compiler's base directory. Remember to modify the constants below.
# To run this script:
# 1) Open Git Bash (this should be installed if you have Git)
# 2) cd into your compiler's base directory
# 3) run "./test_compiler"

##############################################################################################################
# Remember to modify these constants!
PATH_TO_JAVA_FILES="test/resources/Codegen/src" # relative path to the directory containing java test files
ATTU="andys22@attu.cs.washington.edu"           # attu connection string
ATTU_PATH="~/java-compiler"                     # path to directory on attu containing boot.c and test_attu.sh
##############################################################################################################

echo "Compiling Java programs..."
echo ""
mkdir -p "../${PATH_TO_JAVA_FILES}/out"
for file in ${PATH_TO_JAVA_FILES}/*.java; do
  filename=$(basename "$file")
  echo "${filename} -> ${filename%.*}.S"
  java -cp "build/classes;lib/*" Java "$file" > "../${PATH_TO_JAVA_FILES}/out/${filename%.*}.S"
done

echo ""
echo "Sending Java programs to attu..."
echo ""
scp -r "../${PATH_TO_JAVA_FILES}/out" "${ATTU}:${ATTU_PATH}"

echo ""
echo "Sending Java source files to attu..."
echo ""
scp -r "${PATH_TO_JAVA_FILES}" "${ATTU}:${ATTU_PATH}"

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
rm -r "../${PATH_TO_JAVA_FILES}/out"

exit 0