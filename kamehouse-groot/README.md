| [Home](/README.md) | [Docs](/docs/README.md) |
---------------------------------------------------------------

*********************

# GRoot Module:

This module is an app built with js/php to:

* Manage tomcat, replacing tomcat's `/manager` app. `start/stop/deploy/undeploy` all modules in local and all servers
* Start and stop tomcat
* Git pull from all my repos, both in local and all servers
* Re generate media playlists in media server when adding new media files
* Tail logs in the local server
* Reboot the server

This module is accessed through /kame-house-groot and relies on some of the js/css frameworks being available at /kame-house. So even though the /kame-house tomcat modules don't need to be deployed on tomcat for this app to run. /kame-house frontend code still needs to be available in the apache httpd web server for /kame-house-groot

The few APIs of GRoot are built with PHP so the apache httpd server needs to have php installed as well.

To install copy [public/kame-house-groot](public/kame-house-groot) to the apache httpd serving directory. 
Also copy [public/index.html](public/index.html) to the root of the web server directory to redirect to `/kame-house` from `/`

# Login:

- KameHouse GRoot runs in apache httpd. The users are authenticated using php mariadb extension to query the kamehouse users in the databse directly from php, so even if tomcat is down you can still login to groot to restart tomcat.

# Install:

## Linux:

- Run `install-kamehouse-groot.sh -u kamehouseUsername` as root to allow `www-data` user to execute `kamehouse-shell` scripts from kamehouse-groot. This script is executed in `install-kamehouse.sh`

# Troubleshoot

## require_once() calls don't find the files

- GRoot needs to run on a server that populates `$_SERVER["DOCUMENT_ROOT"]` such as apache httpd
