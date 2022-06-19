# Eclipse:

- Follow [dev-environment-setup-eclipse.md](dev-environment-setup-eclipse.md) 

# IntelliJ:

- Follow [dev-environment-setup-intellij.md](dev-environment-setup-intellij.md) 

# Tomcat Dev:

* Download tomcat from apache's website and extract it to *$HOME/programs/apache-tomcat-dev*
* Use the sample configuration in the folder `local-setup/tomcat-dev` to update the tomcat port and manager users

# Apache Httpd:

- Follow [installation-apache.md](installation-apache.md) guide to install apache 
- Follow [dev-environment-setup-apache.md](dev-environment-setup-apache.md) to configure apache for intellij or eclipse dev

# VS Code:

* Create a vs code workspace and add either intellij or eclipse kamehouse folder
* To debug the frontend in vscode, use the chrome debugger launch configurations in .vscode/lauch.json
* There's 2 debugger launch configurations there, one for /kame-house-groot app and the other for /kame-house to debug the frontend in vscode and the backend in intellij: Run > Start Debugging or open the debugger tab to select which debugger to launch
* Create a symlink in kamehouse-ui/src/main: `mklink /D "kame-house" "webapp"` so that the vscode debugger picks up the files for /kame-house
* When setting the breakpoints to debug /kame-house, open the js files by browsing through kamehouse/kamehouse-ui/src/main/kame-house (through the symlink). Not by browsing through kamehouse/kamehouse-ui/src/main/webapp or they won't be bound
* When setting the breakpoints to debug /kame-house-groot, open the js files by browsing through kamehouse/kamehouse-groot/public/kame-house-groot
