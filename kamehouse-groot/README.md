# GRoot Module:

This module is an app built with js/php to:

* Manage tomcat, replacing tomcat's /manager app. start/stop/deploy/undeploy all modules in local and all servers
* Start and stop tomcat
* Git pull from all my repos, both in local and all servers
* Re generate media playlists in media server when adding new media files
* Tail logs in the local server
* Reboot the server

This module is accessed through /kame-house-groot and relies on some of the js/css frameworks being available at /kame-house. So even though the /kame-house tomcat modules don't need to be deployed on tomcat for this app to run. /kame-house frontend code still needs to be available in the apache httpd web server for /kame-house-groot

To install also copy public/index.html to the root of the web server directory to redirect to /kame-house-groot from /
