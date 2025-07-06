| [Home](/README.md) | [Docs](/docs/README.md) |
---------------------------------------------------------------

*********************

# Intellij setup:

- Clone git repo to *${HOME}/workspace/kamehouse*
- Import kamehouse root project into intellij from *${HOME}/workspace/kamehouse*
- Build all modules

## Enable toolbar view:
> view > appearence > toolbar: check

## Add external tools button to toolbar:
Right click on toolbar > Customize 
> Add a separator after git/vcs commands
> Add an action and search for external tools between git/vcs commands and the new separator

- From https://stefancosma.xyz/2018/10/01/how-to-use-tomcat-intellij-idea-community/

## Setup checkstyle:
- Download CheckStyle-IDEA plugin 
- Configure plugin:
  - Settings -> Tools -> Checkstyle
    - Select google checks in the configuration file
- Download latest intellij-java-google-style.xml (Use the one in the /checkstyle folder in the repo)
- Settings -> Editor -> Code Style -> Scheme -> Import Scheme -> Intellij IDEA Code Scheme XML
- After that I can press the following to format java files and they should pass checkstyle in maven:
  - ctrl + alt + o
  - ctrl + alt + l

## Add start/stop/status tomcat and deploy kamehouse as external tools:

 File -> Settings and expand Tools and select External Tools.
  ### `deploy-kamehouse-dev`
    - Windows:
      - Name: deploy-kamehouse-dev
      - Command: cmd.exe
      - Arguments: /c "%USERPROFILE%/programs/kamehouse-shell/bin/win/bat/git-bash.bat -c '${HOME}/programs/kamehouse-shell/bin/kamehouse/deploy/deploy-kamehouse-dev.sh'"
      - Working directory: 
    - Linux:
      - Name: deploy-kamehouse-dev
      - Program: env
      - Arguments: ${HOME}/programs/kamehouse-shell/bin/kamehouse/deploy/deploy-kamehouse-dev.sh
      - Working directory: 

  ### `start-tomcat`
    - Windows:
      - Name: start-tomcat
      - Command: cmd.exe
      - Arguments: /c "%USERPROFILE%/programs/kamehouse-shell/bin/win/bat/git-bash.bat -c '${HOME}/programs/kamehouse-shell/bin/kamehouse/tomcat-startup-dev.sh'"
      - Working directory: 
    - Linux:
      - Name: start-tomcat
      - Program: env
      - Arguments: ${HOME}/programs/kamehouse-shell/bin/kamehouse/tomcat-startup-dev.sh
      - Working directory: 
      
  ### `stop-tomcat`
    - Windows:
      - Name: stop-tomcat
      - Command: cmd.exe
      - Arguments: /c "%USERPROFILE%/programs/kamehouse-shell/bin/win/bat/git-bash.bat -c '${HOME}/programs/kamehouse-shell/bin/kamehouse/tomcat-stop-dev.sh'"
      - Working directory: 
    - Linux:
      - Name: stop-tomcat
      - Program: env
      - Arguments: ${HOME}/programs/kamehouse-shell/bin/kamehouse/tomcat-stop-dev.sh
      - Working directory: 

  ### `status-tomcat`
    - Windows:
      - Name: status-tomcat
      - Program: cmd.exe
      - Arguments: /c "%USERPROFILE%/programs/kamehouse-shell/bin/win/bat/git-bash.bat -c '${HOME}/programs/kamehouse-shell/bin/win/kamehouse/tomcat-status-dev.sh'"
      - Working directory: 
    - Linux:
      - Name: status-tomcat
      - Program: env
      - Arguments: ${HOME}/programs/kamehouse-shell/bin/lin/kamehouse/tomcat-status-dev.sh
      - Working directory: 

## Start tomcat: 

- Tools > External Tools > start-tomcat
  - This will start tomcat and show the console output under Run (bottom right)
    
## Tail logs: 

```sh
tail-log.sh -f tomcat-dev
```

## Setup remote debug:

- Run -> Edit Configurations -> click "+" Remote.
  - name: debug-tomcat
  - Debugger mode to Attach to remote JVM
  - Transport to Socket 
  - Host to localhost
  - Port to 8000. (You can change the port for Tomcat, to something else, in the Tomcat server.xml file)
  - Use module classpath: no module

## Setup remote debug docker:

- Run -> Edit Configurations -> click "+" Remote.
  - name: debug-tomcat-docker
  - Debugger mode to Attach to remote JVM
  - Transport to Socket 
  - Host to localhost
  - Port to 6000
  - Use module classpath: no module

## Setup integration tests:

- Run -> Edit Configurations -> click "+" Maven.
  - name: integration-tests
  - commannd: test-compile failsafe:integration-test failsafe:verify
- I can also run these with my build script from the command line
