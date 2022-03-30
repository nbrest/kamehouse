###########################################################################
# This needs to be updated like lin/bashrc/path.sh if I ever use aws again
###########################################################################
# aws path
# List all directories
MY_SCRIPTS_PATH=$(find ${HOME}/programs/kamehouse-shell/bin -name '.*' -prune -o -type d)
# Filter lin/backup
MY_SCRIPTS_PATH=$(echo "$MY_SCRIPTS_PATH" | grep -v '/lin/backup')
# Filter bashrc
MY_SCRIPTS_PATH=$(echo "$MY_SCRIPTS_PATH" | grep -v aws/bashrc) 
MY_SCRIPTS_PATH=$(echo "$MY_SCRIPTS_PATH" | grep -v lin/bashrc) 
MY_SCRIPTS_PATH=$(echo "$MY_SCRIPTS_PATH" | grep -v win/bashrc) 
MY_SCRIPTS_PATH=$(echo "$MY_SCRIPTS_PATH" | grep -v common/bashrc) 
# Filter deprecated
MY_SCRIPTS_PATH=$(echo "$MY_SCRIPTS_PATH" | grep -v deprecated)
# Filter sudoers
MY_SCRIPTS_PATH=$(echo "$MY_SCRIPTS_PATH" | grep -v lin/sudoers)
# Filter win
MY_SCRIPTS_PATH=$(echo "$MY_SCRIPTS_PATH" | grep -v '/win') 
# Filter .. directory
MY_SCRIPTS_PATH=$(echo "$MY_SCRIPTS_PATH" | grep -v '/\..*')
# Replace \n with :  
MY_SCRIPTS_PATH=$(echo "$MY_SCRIPTS_PATH" | tr '\n' ':')
# Remove last :
MY_SCRIPTS_PATH=$(echo "$MY_SCRIPTS_PATH" | sed '$s/.$//')

if [[ ! ${PATH} =~ "${MY_SCRIPTS_PATH}" ]]; then
  # "${PATH} doesn't contain ${MY_SCRIPTS_PATH}"
  export PATH=${PATH}:${MY_SCRIPTS_PATH}
fi
