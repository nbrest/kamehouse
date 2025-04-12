| [Home](/README.md) | [Docs](/docs/README.md) |
---------------------------------------------------------------

*********************

# GRoot Module:

This module is consists of apis built with php to:

* Manage tomcat. Wrapper over tomcat's `/manager` app. `start/stop/deploy/undeploy` all kamehouse modules in the local server and all servers at once
* Start and stop tomcat
* Git pull from all my repos, both in local and all servers
* Re generate video and audio playlists in the media server when adding new media files
* Tail logs in the local server
* Trigger kamehouse-shell scripts on the server over https
* Reboot the server

This module's apis are accessed through the ui module at the path `/kame-house/groot/`. So even though the /kame-house tomcat modules don't need to be deployed on tomcat for this app to run. /kame-house frontend code still needs to be available in the apache httpd web server to access the php apis on `/kame-house-groot`

The few APIs of GRoot are built with PHP so the apache httpd server needs to have php installed as well.

Install with the standard kamehouse deploy scripts

GRoot relies on tomcat manager's app to be deployed and running

# Login:

- KameHouse GRoot runs in apache httpd. The users are authenticated using php mariadb extension to query the kamehouse users in the databse directly from php, so even if tomcat is down you can still login to groot to restart tomcat.

# Install:

- [optional] Update `GROOT_API_BASIC_AUTH` in `${HOME}/.kamehouse/config/.shell/shell.pwd` with base64 encoded user:pass to execute deploy/git pull on remote servers from groot

## Linux:

- Run `install-kamehouse-groot.sh -u kamehouseUsername` and `set-kamehouse-sudoers-permissions.sh -u kamehouseUsername` to allow `www-data` user to execute `kamehouse-shell` scripts from kamehouse-groot. These scripts is executed from `install-kamehouse.sh`

# PHP Code structure:

- The PHP code is divided in two big groups. API Endpoints and class definitions. 
- All the class definitions are under the `/kamehouse` folder in the apis tree. All the business logic is defined there.
- API Endpoints are defined in all other folders separate from `/kamehouse`. The enpoints don't contain any business logic. They are very short and contain only a `main` method that simply load the objects from the classes defined in `/kamehouse` and call methods on those objects to execute the endpoint's specific functionality.

# Troubleshoot

## require_once() calls don't find the files

- GRoot needs to run on a server that populates `$_SERVER["DOCUMENT_ROOT"]` such as apache httpd
