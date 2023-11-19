# Java 11 releases of kamehouse (from v6.05 to v8.14)

- From v8.15 onward, I can build a docker image for any release tag using the standard docker build script

# On the host:
- Build a docker container for a release/tag.
```sh
java11-release-kamehouse-docker-build.sh -v "6.05"
```

- Push the image
```sh
java11-release-kamehouse-docker-push.sh
```

- Pull the image
```sh
java11-release-kamehouse-docker-pull.sh
```

- Run the image
```sh
java11-release-kamehouse-docker-run.sh
```

- Stop the image
```sh
java11-release-kamehouse-docker-stop.sh
```

- SSH into the container
```sh
java11-release-kamehouse-docker-ssh.sh
```

# Inside the container:
- Tail logs
```sh
tail-log.sh
```

- Deploy KameHouse
```sh
deploy-kamehouse.sh
```

- Current release version
```sh
release-version.sh
```

- Database status
```sh
mariadb-status.sh
```

# Releases status:

v8.14
  - Almost everything ok

v8.00
  - Almost everything ok

v7.00
  - Almost everything ok
  - Tomcat process status doesn't work (because tomcat-status.sh uses sudo and running tomcat as goku)
  - Cant' find my.scripts in groot (because my.scripts is softlinked on nbrest home)

v6.05
  - Almost everything ok
  - Tomcat process status doesn't work (because tomcat-status.sh uses sudo and running tomcat as goku)
  - Cant' find my.scripts in groot (because my.scripts is softlinked on nbrest home)
