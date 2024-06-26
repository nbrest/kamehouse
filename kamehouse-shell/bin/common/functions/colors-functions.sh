setColors() {
  # Colors
  COL_BLUE="\033[1;34m"
  COL_BOLD="\033[1m"
  COL_CYAN="\033[1;36m"
  COL_GREEN="\033[1;32m"
  COL_NORMAL="\033[0;39m"
  COL_PURPLE="\033[1;35m"
  COL_RED="\033[1;31m"
  COL_YELLOW="\033[1;33m"

  # Set to true to remove coloring from all script output
  UNCOLORED_SCRIPTS=false
  if ${UNCOLORED_SCRIPTS}; then
    COL_BLUE=${COL_NORMAL}
    COL_BOLD=${COL_NORMAL}
    COL_CYAN=${COL_NORMAL}
    COL_GREEN=${COL_NORMAL}
    COL_PURPLE=${COL_NORMAL}
    COL_RED=${COL_NORMAL}
    COL_YELLOW=${COL_NORMAL}
  fi
}

setColors
