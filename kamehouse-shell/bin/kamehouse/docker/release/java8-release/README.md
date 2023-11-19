# Java 8 releases of kamehouse (from v0.10 to v6.04)

- Use `-f` as a parameter to all scripts on the host to use for the first kame-house release (v0.10)

# On the host:
- Build a docker container for a release/tag.
```sh
java8-release-kamehouse-docker-build.sh -v "0.10"
```

- Push the image
```sh
java8-release-kamehouse-docker-push.sh
```

- Pull the image
```sh
java8-release-kamehouse-docker-pull.sh
```

- Run the image
```sh
java8-release-kamehouse-docker-run.sh
```

- Stop the image
```sh
java8-release-kamehouse-docker-stop.sh
```

- SSH into the container
```sh
java8-release-kamehouse-docker-ssh.sh
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

v6.04
  - Almost everything ok
  - Tomcat process status doesn't work (because tomcat-status.sh uses sudo and running tomcat as goku)
  - Cant' find my.scripts in groot (because my.scripts is softlinked on nbrest home)

v6.00
  - Almost everything ok
  - Tomcat process status doesn't work (because tomcat-status.sh uses sudo and running tomcat as goku)
  - Cant' find my.scripts in groot (because my.scripts is softlinked on nbrest home)

v5.00
  - Almost everything ok
  - Tomcat process status doesn't work (because tomcat-status.sh uses sudo and running tomcat as goku)
  - Cant' find my.scripts in groot (because my.scripts is softlinked on nbrest home)

v4.00
  - Almost everything ok
  - Tomcat process status doesn't work (because tomcat-status.sh uses sudo and running tomcat as goku)
  - Cant' find my.scripts in groot (because my.scripts is softlinked on nbrest home)

v3.00
  - Almost everything ok
  - Tomcat process status doesn't work (because tomcat-status.sh uses sudo and running tomcat as goku)
  - Cant' find my.scripts in groot (because my.scripts is softlinked on nbrest home)

v2.00
  - Almost everything ok
  - No groot. groot was called root and as part of my old private kh.webserver repo. No need to make that work here

v1.00
  - Almost everything ok
  - Vlc doesnt stop
  - No groot

v0.20
  - Almost everything ok
  - Vlc doesnt stop
  - No groot

v0.10
  - All ok
