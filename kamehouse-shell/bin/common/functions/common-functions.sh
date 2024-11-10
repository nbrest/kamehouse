COMMON_FUNCTIONS_PATH=`dirname ${BASH_SOURCE[0]}`
sourceFiles=("colors-functions.sh" "minimal-functions.sh" "default-functions.sh" "log-functions.sh" "extended-functions.sh" "git/git-functions.sh")
for INDEX in ${!sourceFiles[@]}; do
  source ${COMMON_FUNCTIONS_PATH}/${sourceFiles[$INDEX]}
  if [ "$?" != "0" ]; then
    echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing ${sourceFiles[$INDEX]}\033[0;39m"
    exit 99
  fi
done
