COMMON_FUNCTIONS_PATH=`dirname ${BASH_SOURCE[0]}`
sourceFiles=("colors-functions.sh" "minimal-functions.sh" "default-functions.sh" "log-functions.sh" "extended-functions.sh" "git/git-functions.sh")
for INDEX in ${!sourceFiles[@]}; do
  source ${COMMON_FUNCTIONS_PATH}/${sourceFiles[$INDEX]}
  if [ "$?" != "0" ]; then
    echo "`date +%Y-%m-%d' '%H:%M:%S` - [ERROR] - Error importing ${sourceFiles[$INDEX]}"
    exit 99
  fi
done
