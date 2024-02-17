@echo off
setlocal EnableDelayedExpansion
rem Set debug to true if I run into an issue and need to debug what is being executed
set "debug=false"
if "!debug!"=="true" (
  echo "DEBUG: mode on"
  echo "DEBUG: git-bash arguments: %*"
)
set "arguments=%*"

rem Execute git-bash without arguments
if "!arguments!"=="" ( 
  if "!debug!"=="true" (
    echo "DEBUG: executing git-bash without arguments" 
  )

  "C:\Program Files\Git\bin\bash.exe" 

  if "!debug!"=="true" (
    echo "DEBUG: finished executing git-bash without arguments"
  )
)

rem Execute git-bash with arguments
if defined arguments (
  if "!debug!"=="true" ( 
    echo "DEBUG: Executing git-bash with arguments !arguments!"
  )

  "C:\Program Files\Git\bin\bash.exe" !arguments!

  if "!debug!"=="true" (
    echo "DEBUG: Finished executing git bash with arguments !arguments!"
  )
)

exit
