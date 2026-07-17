getKameHouseShellPathContent() {
  local BASE_PATH=$1
  local TYPE=$2 # d: directory, f: file
  
  if [ ! -d "${BASE_PATH}" ]; then
    return
  fi

  # List all directories
  local PATH_CONTENT=$(find ${BASE_PATH} -name '.*' -prune -o -type ${TYPE})

  # Filter bashrc
  PATH_CONTENT=$(echo "$PATH_CONTENT" | grep -v /bashrc)
  
  # Filter functions
  PATH_CONTENT=$(echo "$PATH_CONTENT" | grep -v /functions)

  # Filter docker container scripts
  PATH_CONTENT=$(echo "$PATH_CONTENT" | grep -v /docker/docker-container)
  PATH_CONTENT=$(echo "$PATH_CONTENT" | grep -v /docker/release/java8-release)
  PATH_CONTENT=$(echo "$PATH_CONTENT" | grep -v /docker/release/java11-release)

  # Filter only .sh scripts if TYPE is f
  if [ "${TYPE}" == "f" ]; then
    PATH_CONTENT=$(echo "$PATH_CONTENT" | grep -e "\.sh$")
  fi

  # Filter /win on linux hosts
  if [ ! -d "/c/Users" ]; then
    PATH_CONTENT=$(echo "$PATH_CONTENT" | grep -v /win)  
  fi

  # Filter .. directory
  PATH_CONTENT=$(echo "$PATH_CONTENT" | grep -v '/\..*')

  # Replace \n with :  
  PATH_CONTENT=$(echo "$PATH_CONTENT" | tr '\n' ':')

  # Remove last :
  PATH_CONTENT=$(echo "$PATH_CONTENT" | sed '$s/.$//')

  echo "${PATH_CONTENT}"
} 