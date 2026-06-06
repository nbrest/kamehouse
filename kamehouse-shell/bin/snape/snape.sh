#!/bin/bash

main() {
  # Execute the latest deployed version of kamehouse-snape
  ${HOME}/programs/kamehouse-snape/bin/kamehouse-snape.sh "$@"
}

main "$@"
