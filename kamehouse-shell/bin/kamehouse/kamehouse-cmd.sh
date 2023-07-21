#!/bin/bash

main() {
  # Execute the latest deployed version of kamehouse-cmd
  ${HOME}/programs/kamehouse-cmd/bin/kamehouse-cmd.sh "$@"
}

main "$@"
