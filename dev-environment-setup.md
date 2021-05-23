# Eclipse:

* Checkout code to *${HOME}/workspace-eclipse/java.web.kame.house*
* Follow instructions in *eclipse/eclipse-configurations.md* (in programming private repo)
* Follow instructions in *eclipse/workspace-tomcat-setup.md* (in programming private repo)

# IntelliJ:

* Checkout code to *${HOME}/workspace-intellij/java.web.kame.house*
* Follow instructions in *intellij/workspace-tomcat-setup.md* (in programming private repo)

# VS Code:

* Import workspace from *${HOME}/home-synced/workspace-vs-code* (backed up in private repo)
* To debug the frontend in vscode, use the chrome debugger launch configurations in /.vscode in (in kh.webserver private repo)
* There's 2 debugger launch configurations there, one for the base / root app and the other for /kame-house to debug the frontend in vscode and the backend in intellij

# Apache Httpd:

## Windows:

* Download a precompiled version of apache httpd (Currently using https://www.apachehaus.com/)
* Replace all the configuration with the settings I have for httpd in my private server backup repo
* Follow *apache-httpd/httpd-setup.md* (in programming private repo)

## Linux:

* Install apache httpd from the package manager
* Replace all the configuration with the settings I have for httpd in my private server backup repo
* Follow *apache-httpd/httpd-setup.md* (in programming private repo)
