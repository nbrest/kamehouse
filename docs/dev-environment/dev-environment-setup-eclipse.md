| [Home](/README.md) | [Docs](/docs/README.md) |
---------------------------------------------------------------

*********************

# Eclipse setup:

- Clone git repo to *${HOME}/workspace/kamehouse*
- Import kamehouse root project into eclipse from *${HOME}/workspace/kamehouse*
- Build all modules

- Don't configure a server in eclipse any more. It's too problematic. It's much easier and better to setup remote debugging the way I also do it in intellij

## Browser:
- Use chrome instead of internal browser:
  - Window > Web Browser > Chrome

## Maven:
- Currently using embedded version

## Organize imports: 
- Window -> Preferences -> Java -> Code Style -> Organize imports:
  - com
  - edu
  - net
  - org
  - java
  - javax

## Replace tabs with spaces:
  - Window -> Preferences -> Java -> Code Style -> Formatter
  - Import Active profile:
    - From [no-tabs.xml](/local-setup/eclipse/no-tabs.xml)

## Setup remote debugging in eclipse:
  - Run > Debug Configurations > Remote Java Application > New Configuration:
  - name: debug-tomcat
  - Connect tab:
    - project: kame-house-admin (or any kamehouse module really)
    - connection type: Standard (socket attach)
    - connection properties:
      - host: localhost
      - port: 8000
    - allow termination of remote VM: check
  - Sources tab:
    - Add > Add all kamehouse modules
  - Common tab:
    - display in favorites menu > debug: check

## Setup remote debug docker:

  - Run > Debug Configurations > Remote Java Application > New Configuration:
  - name: debug-tomcat-docker
  - Connect tab:
    - project: kame-house-admin (or any kamehouse module really)
    - connection type: Standard (socket attach)
    - connection properties:
      - host: localhost
      - port: 6000
    - allow termination of remote VM: check
  - Sources tab:
    - Add > Add all kamehouse modules
  - Common tab:
    - display in favorites menu > debug: check

## Configure commands to Start/Stop/Status/Deploy tomcat for eclipse:

  ### `deploy-kamehouse-dev.sh`
    - Run > External Tools > External Tools Configurations > Program > New Configuration:
    - name: deploy-kamehouse-dev.sh
    - Main tab:
      - Windows:
        - location: C:\Windows\System32\cmd.exe
        - working directory: 
        - arguments: "/c %USERPROFILE%/programs/kamehouse-shell/bin/win/bat/git-bash.bat -c '~/programs/kamehouse-shell/bin/kamehouse/deploy/deploy-kamehouse-dev.sh'"
      - Linux:
        - location: /bin/bash
        - working directory:
        - arguments: -c '~/programs/kamehouse-shell/bin/kamehouse/deploy/deploy-kamehouse-dev.sh'
    - Common tab:
      - display in favorites menu > External tools: check       

  ### `tomcat-startup-dev.sh`
    - Run > External Tools > External Tools Configurations > Program > New Configuration:
    - name: tomcat-startup-dev.sh
    - Main tab:
      - Windows:
        - location: C:\Windows\System32\cmd.exe
        - working directory: 
        - arguments: "/c %USERPROFILE%/programs/kamehouse-shell/bin/win/bat/git-bash.bat -c '~/programs/kamehouse-shell/bin/kamehouse/tomcat-startup-dev.sh'"
      - Linux:
        - location: /bin/bash
        - working directory: 
        - arguments: -c '~/programs/kamehouse-shell/bin/kamehouse/tomcat-startup-dev.sh'
    - Common tab:
      - display in favorites menu > External tools: check

  ### `tomcat-status.sh`
    - Run > External Tools > External Tools Configurations > Program > New Configuration:
    - name: tomcat-status-dev.sh
    - Main tab:
      - Windows:
        - location: C:\Windows\System32\cmd.exe
        - working directory: 
        - arguments: "/c %USERPROFILE%/programs/kamehouse-shell/bin/win/bat/git-bash.bat -c '~/programs/kamehouse-shell/bin/win/kamehouse/tomcat-status-dev.sh'"
      - Linux:
        - location: /bin/bash
        - working directory: 
        - arguments: -c '~/programs/kamehouse-shell/bin/lin/kamehouse/tomcat-status-dev.sh'
    - Common tab:
      - display in favorites menu > External tools: check

  ### `tomcat-stop.sh`
    - Run > External Tools > External Tools Configurations > Program > New Configuration:
    - name: tomcat-stop-dev.sh
    - Main tab:
      - Windows:
        - location: C:\Windows\System32\cmd.exe
        - working directory: 
        - arguments: "/c %USERPROFILE%/programs/kamehouse-shell/bin/win/bat/git-bash.bat -c '~/programs/kamehouse-shell/bin/kamehouse/tomcat-stop-dev.sh'"
      - Linux
        - location: /bin/bash
        - working directory:
        - arguments: -c '~/programs/kamehouse-shell/bin/kamehouse/tomcat-stop-dev.sh'
    - Common tab:
      - display in favorites menu > External tools: check

  ### For convenience I can add these commands also to the run green button dropdown:
    - Run Configurations > Launch Group > New Configuration
      - Lauch tab > Add > Program > Select the script that I added above
      - Common Tab > Display in favorites menu > run :check

## Start tomcat eclipse:
- external tools > `tomcat-startup-dev.sh`

## Deploy kamehouse in eclipse:
- external tools > `deploy-kamehouse-dev.sh`

## Stop tomcat eclipse:
- external tools > `tomcat-stop-dev.sh`

## Check the status of tomcat eclipse:
- external tools > `tomcat-status-dev.sh`

## Debug tomcat from eclipse:
- debug > `debug-tomcat-kamehouse`

## Tail kamehouse dev logs 
- using tail-log.sh script to see it in colors and be able to filter through log level.
```sh
tail-log.sh -f tomcat-dev
```