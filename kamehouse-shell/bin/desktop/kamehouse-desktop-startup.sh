#!/bin/bash

main() {
  # Execute the latest deployed version of kamehouse-desktop
  ${HOME}/programs/kamehouse-desktop/bin/kamehouse-desktop-startup.sh "$@"
}

main "$@"
