# GRoot Module:

This module is an app built with js/php to:

* Manage tomcat, replacing tomcat's /manager app. start/stop/deploy/undeploy all modules in local and all servers
* Start and stop tomcat
* Git pull from all my repos, both in local and all servers
* Re generate media playlists in media server when adding new media files
* Tail logs in the local server
* Reboot the server

This module is accessed through /kame-house-groot and relies on some of the js/css frameworks being available at /kame-house. So even though the /kame-house tomcat modules don't need to be deployed on tomcat for this app to run. /kame-house frontend code still needs to be available in the apache httpd web server for /kame-house-groot

The few APIs of GRoot are built with PHP so the apache httpd server needs to have php installed as well.

To install copy `public/kame-house-groot` to the apache httpd serving directory. 
Also copy `public/index.html` to the root of the web server directory to redirect to `/kame-house` from `/`

Building the project with `build-kamehouse.sh -m groot` creates a zip file with everything in `kamehouse-groot/target/kamehouse-groot-bundle.zip`. 
This zip can be extracted to the directory where the files are served in an apache httpd server.
It also includes the root `/index.html` to redirect to `/kame-house`

# Edit users:

- KameHouse GRoot runs in apache httpd. The users are authenticated using a .htpassword file as the source located at the root of the webserver

- To add users:
```sh
htpasswd -b ${PATH_TO_HTPASSWD_FILE}/.htpasswd user pass
```

- To remove users:
```sh
htpasswd -D ${PATH_TO_HTPASSWD_FILE}/.htpasswd user
```

# Linux

- Symlink programs folder from user home to root home for `exec-script.php`
```sh
# As root:
ln -s /home/[USERNAME]/programs /root/programs
```
