# Replace GIT_PROJECT_DIR in each git pull script
GIT_PROJECT_DIR="DEFAULT_INVALID_DIR"
GIT_BRANCH="dev"
GIT_REMOTE="all"

mainProcess() {
  log.info "Git pull ${COL_PURPLE}${GIT_BRANCH} ${GIT_PROJECT_DIR}"

  cd ${GIT_PROJECT_DIR}
  checkCommandStatus "$?"

  if [ ! -d ".git" ]; then
    log.error "This directory ${GIT_PROJECT_DIR} doesn't contain a git repository. Skipping git pull..."
    exit 1
  fi

  git checkout ${GIT_BRANCH}
  checkCommandStatus "$?"
  git pull ${GIT_REMOTE} ${GIT_BRANCH}
  checkCommandStatus "$?"
}
