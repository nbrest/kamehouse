#!/bin/bash

# Pass the file to decrypt as a parameter

main() {
  ${HOME}/programs/kamehouse-shell/bin/kamehouse/kamehouse-cmd.sh -o decrypt -if "$1" -of stdout | grep -v "\[main\]" | grep -v "\[DEBUG\]" | grep -v "\[TRACE\]"
  exit 0
}

main "$@"
