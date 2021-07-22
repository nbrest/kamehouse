# win path
# List all directories in my.scripts
MY_SCRIPTS_PATH=$(find ${HOME}/my.scripts -name '.*' -prune -o -type d)
# Filter aws
MY_SCRIPTS_PATH=$(echo "$MY_SCRIPTS_PATH" | grep -v 'my.scripts/aws')
# Filter bashrc
MY_SCRIPTS_PATH=$(echo "$MY_SCRIPTS_PATH" | grep -v aws/bashrc) 
MY_SCRIPTS_PATH=$(echo "$MY_SCRIPTS_PATH" | grep -v lin/bashrc) 
MY_SCRIPTS_PATH=$(echo "$MY_SCRIPTS_PATH" | grep -v win/bashrc) 
MY_SCRIPTS_PATH=$(echo "$MY_SCRIPTS_PATH" | grep -v common/bashrc) 
# Filter deprecated
MY_SCRIPTS_PATH=$(echo "$MY_SCRIPTS_PATH" | grep -v deprecated)
# Filter lin
MY_SCRIPTS_PATH=$(echo "$MY_SCRIPTS_PATH" | grep -v 'my.scripts/lin') 
# Filter .. directory
MY_SCRIPTS_PATH=$(echo "$MY_SCRIPTS_PATH" | grep -v 'my.scripts/\..*')
# Replace \n with :  
MY_SCRIPTS_PATH=$(echo "$MY_SCRIPTS_PATH" | tr '\n' ':')
# Remove last :
MY_SCRIPTS_PATH=$(echo "$MY_SCRIPTS_PATH" | sed '$s/.$//')

if [[ ! ${PATH} =~ "${MY_SCRIPTS_PATH}" ]]; then
  # "${PATH} doesn't contain ${MY_SCRIPTS_PATH}"
  export PATH=${PATH}:${MY_SCRIPTS_PATH}
fi