# AGENTS.md

Instructions for AI coding tools working on the KameHouse repository.

## Quick rules
- **Local dev only:** Use `*-dev.sh` scripts. Never use non-dev scripts unless explicitly asked for production. Examples: `tomcat-status-dev.sh`, `tomcat-startup-dev.sh`, `tomcat-stop-dev.sh`, `tomcat-restart-dev.sh`, `deploy-kamehouse-dev.sh`.
- **Shell changes:** Any shell script added or modified must use the `kamehouse-shell` framework and structure (source `common-functions.sh`, override `mainProcess`/`parseArguments`/`printHelpOptions` as needed, and call `main "$@"`; use `kamehouse-shell/bin/base-script.sh` as the template). After editing `kamehouse-shell/`, run `deploy-kamehouse-dev.sh -m shell` from repo root and test the installed copy.
- **Build tool:** Use Gradle wrapper `./gradlew` (not system Gradle). `deploy-kamehouse-dev.sh` does a fast build by default (skips tests/checkstyle/SpotBugs/JaCoCo). Run full `build-kamehouse.sh` or `./gradlew` for validation.
- **Integration tests:** Never run in foreground (ESC keypress can abort/affect terminal). Always run detached/background through `kamehouse-shell/bin/test/run-with-keyboard-sink.sh` (installed: `${HOME}/programs/kamehouse-shell/bin/test/run-with-keyboard-sink.sh`), which discards test-injected VNC keystrokes instead of letting them reach opencode. Need encrypted secrets loaded (`INTEGRATION_TESTS_CRED`, MariaDB creds).
- **Safety:** Don't commit generated artifacts, APKs, `platforms/`/`plugins/`, logs, decrypted secrets/keys. Don't commit automatically unless explicitly asked. Don't print/decrypt/modify secrets.
- **Repo layout:** Dev repo `${HOME}/workspace/kamehouse`, prod `${HOME}/git/kamehouse`. Installed scripts in `${HOME}/programs/kamehouse-shell/bin`. Config/secrets in `${HOME}/.kamehouse/`. Get secrets via `get-kamehouse-secret.sh -s NAME`. `PROJECT_DIR` defaults to prod path but build/deploy run from CWD; Docker maps host repo to `/home/goku/git/kamehouse` in container.
- When I request `run full validation of changes` it means: run unit tests, deploy the code to tomcat-dev, run integration tests, run `build-kamehouse.sh` for test coverage, checkstyle and spotbuts, run mobile build with `build-kamehouse.sh -m mobile` and run sonar with `sonarcloud-run-kamehouse.sh`. If any of these fail, the code is not good enough to be committed

## Project
**Modules:** Webapps: `admin,auth,media,tennisworld,testmodule,vlcrc` | Libs: `commons,commons-core,commons-test` | Apps: `cmd,desktop,groot,mobile,shell,snape,ui`. Backend Java/bash (+PHP/Python). Frontend plain JS/TS (no framework). Desktop PyQt5. Mobile Cordova. Versions: see `docs/versions/versions.md`.

## Build & Test
```sh
# Full build + tests + checks
./gradlew clean build
build-kamehouse.sh  # default prod profile

# Options
build-kamehouse.sh -m <module>           # admin,auth,cmd,desktop,groot,media,mobile,shell,snape,tennisworld,testmodule,ui,vlcrc
build-kamehouse.sh -p <prod|docker|ci>  # -p = profile (not port)
build-kamehouse.sh -f                   # fast (skip tests/checkstyle/SpotBugs/JaCoCo) - not for final validation
build-kamehouse.sh -r -m <module>       # resume failed build

# Tests/checks
./gradlew test                          # all unit tests (*IntegrationTest excluded)
./gradlew :<module>:test
./gradlew :<module>:check               # unit tests + checkstyle + SpotBugs + JaCoCo
./gradlew :kamehouse-mobile:check       # mobile Java unit tests
```
**Notes:** Unit tests exclude `*IntegrationTest`; `integrationTest` runs only `*IntegrationTest`. Checkstyle `checkstyle/google_checks.xml`, SpotBugs `spotbugs/excludeFilter.xml` (excludes test classes). JaCoCo >=80% line / >=60% branch. `build-kamehouse.sh` runs tests/checks unless `-f`. Mobile unit tests run via `build-kamehouse.sh -m mobile` unless `-f`/`-i`. Frontend modules: `npm install && npm run build && npm run scan`. Every shell script has `-h`.

## Integration tests
```sh
build-kamehouse.sh -i                    # host repo
build-kamehouse.sh -i -p docker          # inside Docker dev container at /home/goku/git/kamehouse
# detached through the keyboard sink (required to avoid ESC keypress issues):
LOG=/tmp/kamehouse-integration.log
STATUSFILE=/tmp/kamehouse-integration.status
nohup setsid bash -c '
  kamehouse-shell/bin/test/run-with-keyboard-sink.sh -- build-kamehouse.sh -i -p docker > "$1" 2>&1
  echo $? > "$2"
' _ "$LOG" "$STATUSFILE" </dev/null >/dev/null 2>&1 &
echo $!
```
Options: `-i` only, `-c` continue on errors (passes `-PkamehouseIgnoreIntegrationTestFailures`). Always wrap the integration test command with `run-with-keyboard-sink.sh`; never run it directly. Run against Docker profile in container or dev Tomcat at `${HOME}/programs/apache-tomcat-dev`. Inspect `/tmp/kamehouse-integration.log` and `${HOME}/logs/build-kamehouse.log`. `docker-ci-integration-tests-trigger.sh` starts CI container and runs tests on latest committed changes only (not working tree).

## Deploy
```sh
# Dev (current working copy)
deploy-kamehouse-dev.sh
deploy-kamehouse-dev.sh -m <module>     # admin,auth,cmd,desktop,groot,media,mobile,shell,snape,tennisworld,testmodule,ui,vlcrc

# Prod (only if explicitly requested)
deploy-kamehouse.sh
deploy-kamehouse.sh -m <module>
deploy-kamehouse.sh -c                  # deploy from CWD without git pull
```
**Notes:** `deploy-kamehouse-dev.sh` uses prod build profile by default but targets dev Tomcat/static paths; does fast build - run full `build-kamehouse.sh` separately for validation. Mobile deploy builds APK and uploads to Google Drive and/or configured dev device. `ui/groot` update Apache Httpd static content. Shell install: `install-kamehouse-shell.sh -l INFO` or `deploy-kamehouse-dev.sh -m shell`.

## Runtimes
**Tomcat (dev only):** `tomcat-status-dev.sh`/`startup-dev.sh`/`stop-dev.sh`/`restart-dev.sh`. Installed at `${HOME}/programs/apache-tomcat-dev` (HTTP 9980, JPDA 8000, `catalina.sh jpda start`). Logs: `tail-log.sh -f tomcat-dev -n 2000` or `${HOME}/programs/apache-tomcat-dev/logs/catalina.out`. Prod uses `${HOME}/programs/apache-tomcat` port 9090 - don't use for dev. Apache Httpd: `httpd-startup.sh`/`httpd-stop.sh` for native dev.

**Mobile:** `build-kamehouse.sh -m mobile` builds; `deploy-kamehouse-dev.sh -m mobile` builds+uploads. Needs its own JDK/Gradle, Android SDK at `${HOME}/Android/Sdk`, `KAMEHOUSE_MOBILE_ENCRYPTION_KEY` in secrets, server entries from `kamehouse-mobile` in `${HOME}/.kamehouse/config/kamehouse.cfg`. Cordova flow regenerates `platforms/`/`plugins/` (don't edit). APK: `kamehouse-mobile/platforms/android/app/build/outputs/apk/debug/app-debug.apk`. Mock: add `?mockCordova=true&logLevel=trace` (see `kamehouse-mobile/README.md`).

**Docker dev:** `${HOME}/programs/kamehouse-shell/bin/docker/docker-{run,reinit,ssh,shell,stop}-kamehouse.sh -p dev` maps host `${HOME}/workspace/kamehouse` to `/home/goku/git/kamehouse` in container. Deploy from inside: `cd /home/goku/git/kamehouse && deploy-kamehouse.sh -c`. See `docs/docker/docker-setup.md`.

**SonarCloud:** `sonarcloud-run-kamehouse.sh` (loads `SONAR_TOKEN` from `${HOME}/.kamehouse`). Or `./gradlew clean build sonar -Psonar --console=plain -Dsonar.projectKey=nbrest_kamehouse ...` (`sonar.organization`/`sonar.host.url` in `gradle.properties`). Issues: `curl -s 'https://sonarcloud.io/api/issues/search?componentKeys=nbrest_kamehouse&resolved=false&ps=500'`. GitHub Actions runs on `dev`.

## References & Validation
**Docs:** `README.md`, `docs/README.md`, `docs/dev-environment/dev-environment-setup.md`, `docs/execution/execution.md`, `docs/docker/docker-setup.md`, `docs/versions/versions.md`, module READMEs.

**Logs:** Build `${HOME}/logs/build-kamehouse.log`, deploy `${HOME}/logs/deploy-kamehouse-*.log`, dev Tomcat `${HOME}/programs/apache-tomcat-dev/logs/catalina.out`/`kameHouse.log`, Apache `${HOME}/programs/apache-httpd/logs/`. Use `tail-log.sh`.

**Quick validation:**
```sh
./gradlew test && ./gradlew :<changed-module>:check && build-kamehouse.sh  # general
build-kamehouse.sh -m mobile && ./gradlew :kamehouse-mobile:check         # mobile
deploy-kamehouse-dev.sh -m shell                                          # shell (reinstalls)
# local deploy: use dev scripts + check tomcat-status-dev.sh + dev Tomcat logs
```
