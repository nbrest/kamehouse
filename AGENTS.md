# AGENTS.md

Instructions for AI coding tools working on the KameHouse repository.

## Project overview

KameHouse is a multi-module Gradle project. Backend code is mostly Java and bash, with some PHP
and Python. Frontend code is plain JavaScript/TypeScript without a UI framework. The desktop app
uses Python/PyQt5. The Android app is built with Apache Cordova.

Main modules:

- `kamehouse-admin`, `kamehouse-auth`, `kamehouse-media`, `kamehouse-tennisworld`,
  `kamehouse-testmodule`, `kamehouse-vlcrc`: Tomcat webapps.
- `kamehouse-commons`, `kamehouse-commons-core`, `kamehouse-commons-test`: shared libraries.
- `kamehouse-cmd`: command-line client.
- `kamehouse-desktop`: Python/PyQt5 desktop app.
- `kamehouse-groot`: additional web frontend.
- `kamehouse-mobile`: Cordova Android app.
- `kamehouse-shell`: bash scripts for installation, building, deployment, Tomcat, Docker, logs,
  backups, and other operations.
- `kamehouse-snape`: supporting scripts/application.
- `kamehouse-ui`: main web frontend.

Important versions:

- See `docs/versions/versions.md` for the versions used by the tech stack

## Golden rules

- When modifying shell scripts, always run `deploy-kamehouse-dev.sh -m shell` from the root of the repository to deploy the current changes, then execute the script to test
- For local development, use the `*-dev.sh` scripts. Never use the non-dev scripts for local work. Whenever you are assigned a task, assume it is always using the dev environment.
  Examples:
  - `tomcat-status-dev.sh` instead of `tomcat-status.sh`
  - `tomcat-startup-dev.sh` instead of `tomcat-startup.sh`
  - `tomcat-stop-dev.sh` instead of `tomcat-stop.sh`
  - `tomcat-restart-dev.sh` instead of `tomcat-restart.sh`
  - `deploy-kamehouse-dev.sh` instead of `deploy-kamehouse.sh`
- The non-dev scripts target the production Tomcat/configuration and must not be used unless the
  task explicitly requires production deployment.
- Use the Gradle wrapper (`./gradlew`) rather than a system Gradle installation.
- `deploy-kamehouse-dev.sh` performs a fast build by default: it skips tests, checkstyle, SpotBugs,
  and JaCoCo. Run a full `build-kamehouse.sh` or Gradle build before validating code quality.
- Integration tests are not safe to run in the foreground. At least one test sends an ESC keypress
  that can affect the terminal. Run them detached or in a separate/background terminal.
- Do not commit generated artifacts, build output, APKs, generated Cordova `platforms`/`plugins`,
  logs, decrypted secrets, or encryption keys.
- Never commit code automatically unless explicitly required. Leave the code changes in the working dir for manual review by default
- Do not print, decrypt, or modify secret values unless explicitly requested.

## Repository and local folders

- Repository for dev work: `${HOME}/workspace/kamehouse`
- Repository for prod deployments: `${HOME}/git/kamehouse`
- KameHouse process logs: `${HOME}/logs`
- Rotated/old process logs: `${HOME}/logs/old`
- KameHouse config, shell config, keys, and encrypted secrets: `${HOME}/.kamehouse`
  - Main config: `${HOME}/.kamehouse/config/kamehouse.cfg`
  - Shell overrides: `${HOME}/.kamehouse/config/shell/*.cfg`
  - Encrypted secrets: `${HOME}/.kamehouse/config/keys/.kamehouse-secrets.cfg.enc`
  - Keys: `${HOME}/.kamehouse/config/keys/`
  - Server config: `${HOME}/.kamehouse/server-config/`
  - Application data: `${HOME}/.kamehouse/data/`
- To get the value of a kamehouse secret use the script: `get-kamehouse-secret.sh -s KAMEHOUSE_SECRET_NAME`
- Installed shell scripts: `${HOME}/programs/kamehouse-shell/bin`
- Installed python scripts: `${HOME}/programs/kamehouse-snape/bin`
- Local dev Tomcat: `${HOME}/programs/apache-tomcat-dev`
- Local dev Tomcat logs: `${HOME}/programs/apache-tomcat-dev/logs`
- Local Apache Httpd: `${HOME}/programs/apache-httpd`
- Android SDK: `${HOME}/Android/Sdk`
- Gradle JDK cache: `${HOME}/.gradle/jdks`
- Gradle distributions: `${HOME}/.gradle/wrapper/dists`
- Android debug keystore: `${HOME}/.android/debug.keystore`

The shell scripts use `PROJECT_DIR=${HOME}/git/kamehouse` by default, but build and dev deploy
scripts run against the current working directory. The Docker dev profile maps the host repository
to `/home/goku/git/kamehouse` inside the container.

## KameHouse shell installation

All scripts refer to the installed copy at `${HOME}/programs/kamehouse-shell`, not directly to the
repository copy. After editing anything under `kamehouse-shell`, reinstall the shell scripts before
testing the installed commands:

```sh
install-kamehouse-shell.sh -l INFO
```

Or deploy only the shell module:

```sh
deploy-kamehouse-dev.sh -m shell
```

The installer creates `${HOME}/logs`, `${HOME}/.kamehouse/config`, and the expected directory
structure. It also updates `${HOME}/.bashrc` so the installed scripts are on `PATH`.

- Every shell script has a `-h` option with the help menu for script usage

## Building

Full Gradle build, including unit tests and configured quality checks:

```sh
./gradlew clean build
```

Standard project build script:

```sh
build-kamehouse.sh
```

Useful build options:

```sh
build-kamehouse.sh -m <module>
build-kamehouse.sh -p <prod|docker|ci>
build-kamehouse.sh -f
build-kamehouse.sh -r -m <module>
```

- `-m <module>`: build one module. Accepted short module names are `admin`, `auth`, `cmd`,
  `desktop`, `groot`, `media`, `mobile`, `shell`, `snape`, `tennisworld`, `testmodule`, `ui`, and
  `vlcrc`.
- `-p <profile>`: build profile. Default is `prod`; other valid values are `docker` and `ci`.
  Note that for build/deploy scripts `-p` means profile, not port.
- `-f`: fast build, skipping tests, checkstyle, SpotBugs, and JaCoCo. Do not use this for final
  validation.
- `-r`: resume a failed module build. Use with `-m`.

The build script runs `npm install`, `npm run build`, and `npm run scan` for frontend modules, then
runs the Gradle build. It also generates `build-info.json` and removes generated copies afterward.

Useful direct Gradle examples:

```sh
./gradlew :kamehouse-vlcrc:test
./gradlew :kamehouse-vlcrc:check
./gradlew test checkstyleMain checkstyleTest
./gradlew clean build --console=plain
```

The shell scripts can resolve or download the expected JDK when needed.

## Unit tests and code quality

Run all unit tests:

```sh
./gradlew test
```

Run a module's unit tests:

```sh
./gradlew :<module>:test
```

Run module checks, including unit tests, checkstyle, SpotBugs, and JaCoCo:

```sh
./gradlew :<module>:check
```

Notes:

- Unit tests are all test classes except `*IntegrationTest`.
- `integrationTest` runs only `*IntegrationTest` classes.
- Checkstyle uses the pinned `checkstyle/google_checks.xml`.
- SpotBugs excludes test classes and uses `spotbugs/excludeFilter.xml`.
- JaCoCo thresholds are line coverage >= 0.80 and branch coverage >= 0.60.
- `build-kamehouse.sh` runs unit tests and quality checks unless `-f` is used.
- Mobile Java unit tests are run by `build-kamehouse.sh -m mobile` through
  `./gradlew :kamehouse-mobile:check`, unless `-f` or `-i` is used.

## Integration tests

Integration tests normally run against the Docker profile when inside a docker container, or against the dev tomcat in `${HOME}/programs/apache-tomcat-dev`. 
They require the encrypted secrets to be loaded, including `INTEGRATION_TESTS_CRED` and the MariaDB credentials.

From the host repo:

```sh
build-kamehouse.sh -i
```

Inside the Docker dev container, run from `/home/goku/git/kamehouse`:

```sh
build-kamehouse.sh -i -p docker
```

Some options:

- `-i`: run integration tests only.
- `-c`: continue after integration test failures. Use with `-i`.
- The integration test task is `integrationTest`; it uses `-PkamehouseIgnoreIntegrationTestFailures`
  when `-c` is set.

Run integration tests detached or in a background terminal, for example:

```sh
nohup build-kamehouse.sh -i -p docker \
  > /tmp/kamehouse-integration.log 2>&1 &
```

Then inspect `/tmp/kamehouse-integration.log` and `${HOME}/logs/build-kamehouse.log`. Do not run the
suite in the foreground: one test sends an ESC keypress that can abort the run or affect the
terminal when running as an AI agent. Running them from a standard terminal is safe. ESC key press won't abort it.

The Docker CI helper `docker-ci-integration-tests-trigger.sh` starts a CI container and runs the
integration tests there. It only runs the latest commited changes, so it can't be used for testing working copy changes.

## Deployment

Local development deployment:

```sh
deploy-kamehouse-dev.sh
```

Deploy a single module to the local dev environment:

```sh
deploy-kamehouse-dev.sh -m <module>
deploy-kamehouse-dev.sh -m mobile
deploy-kamehouse-dev.sh -m shell
```

- `deploy-kamehouse-dev.sh` deploys the current working copy.
- It uses the `prod` build profile by default but targets the local dev Tomcat and dev static file
  locations.
- It performs a fast build, so run the full build/test suite separately when validating changes with `build-kamehouse.sh` from the working copy dir
- Deploying `kamehouse-mobile` builds the APK and uploads it to Google Drive and, if configured, to
  the dev Android device.
- Deploying `kamehouse-ui` or `kamehouse-groot` updates the static frontend content served by the
  local Apache Httpd.

Production deployment (only when explicitly requested):

```sh
deploy-kamehouse.sh
deploy-kamehouse.sh -m <module>
```

`deploy-kamehouse.sh` uses production paths and can pull the latest changes from git. Do not use it
for local development.

## Tomcat development environment

Use only the dev scripts for local work:

```sh
tomcat-status-dev.sh
tomcat-startup-dev.sh
tomcat-stop-dev.sh
tomcat-restart-dev.sh
```

If the installed scripts are not on `PATH`, invoke them from the installed shell directory:

```sh
${HOME}/programs/kamehouse-shell/bin/tomcat/tomcat-status-dev.sh
```

The local dev Tomcat:

- Directory: `${HOME}/programs/apache-tomcat-dev`
- HTTP port: `9980`
- Remote debug port: `8000`
- Startup method: `catalina.sh jpda start`
- Main log: `${HOME}/programs/apache-tomcat-dev/logs/catalina.out`

Tail logs:

```sh
tail-log.sh -f tomcat-dev -n 2000
tail-log.sh -f build
tail-log.sh -f deploy
tail-log.sh -f apache
```

The non-dev Tomcat scripts use production Tomcat at `${HOME}/programs/apache-tomcat` and port
`9090`. Do not use them for local development.

The local dev UI is normally served through Apache Httpd. Start/stop it with `httpd-startup.sh` and
`httpd-stop.sh` when the dev environment is configured for native Apache instead of Docker.

## Mobile build

Build the Android APK:

```sh
build-kamehouse.sh -m mobile
```

Build and deploy/upload the APK:

```sh
deploy-kamehouse-dev.sh -m mobile
```

Mobile build details:

- Uses it's own java JDK Version and it's own gradle version
- Sets `ANDROID_SDK_ROOT` and `ANDROID_HOME` to `${HOME}/Android/Sdk` on Linux.
- Uses Cordova: `npm install`, removes `platforms` and `plugins`, then `cordova prepare` and
  `cordova build android`.
- Uses Gradle for the Cordova Android build. If that distribution is not cached, the build
  falls back to the root Gradle wrapper version from `gradle/wrapper/gradle-wrapper.properties` to generate the Android Gradle wrapper.
- Regenerates `kamehouse-mobile/platforms` and `kamehouse-mobile/plugins`; do not treat them as
  source changes.
- Builds frontend assets into `kamehouse-mobile/www/kame-house-mobile`.
- Requires `KAMEHOUSE_MOBILE_ENCRYPTION_KEY` in the encrypted secrets file.
- Backend server entries come from `kamehouse-mobile` values in
  `${HOME}/.kamehouse/config/kamehouse.cfg`.
- APK output: `kamehouse-mobile/platforms/android/app/build/outputs/apk/debug/app-debug.apk`.
- `build-kamehouse.sh -m mobile` builds only. `deploy-kamehouse-dev.sh -m mobile` builds and uploads
  to Google Drive and the configured dev device.

Run mobile Java unit tests:

```sh
./gradlew :kamehouse-mobile:check
```

Test the mobile web UI without Cordova using the Apache/mock setup described in
`kamehouse-mobile/README.md`. Add `?mockCordova=true&logLevel=trace` to a page URL to mock Cordova.

## SonarCloud

Preferred command:

```sh
sonarcloud-run-kamehouse.sh
```

This loads the encrypted `SONAR_TOKEN` from `${HOME}/.kamehouse` and runs:

```sh
./gradlew clean build sonar -Psonar --console=plain \
  -Dsonar.projectKey=nbrest_kamehouse \
  -Dsonar.organization=nbrest \
  -Dsonar.host.url=https://sonarcloud.io \
  -Dsonar.token="${SONAR_TOKEN}"
```

Direct Gradle equivalent, with `SONAR_TOKEN` already exported:

```sh
./gradlew clean build sonar -Psonar --no-daemon --console=plain \
  -Dsonar.projectKey=nbrest_kamehouse
```

Sonar properties `sonar.organization` and `sonar.host.url` are in `gradle.properties`. The
SonarQube plugin is only applied when `-Psonar` is passed.

Verify open issues with:

```sh
curl -s 'https://sonarcloud.io/api/issues/search?componentKeys=nbrest_kamehouse&resolved=false&ps=500'
```

The GitHub Actions workflow is `.github/workflows/build.yml`. It runs on pushes to `dev` and executes the same Gradle build plus Sonar analysis, using the repository's `SONAR_TOKEN` secret.

## Docker development environment

An alternative to native Tomcat/Apache development is the Docker dev environment:

```sh
${HOME}/programs/kamehouse-shell/bin/docker/docker-run-kamehouse.sh -p dev
${HOME}/programs/kamehouse-shell/bin/docker/docker-reinit-container.sh -p dev
${HOME}/programs/kamehouse-shell/bin/docker/docker-ssh-kamehouse.sh -p dev
${HOME}/programs/kamehouse-shell/bin/docker/docker-shell-kamehouse.sh -p dev
${HOME}/programs/kamehouse-shell/bin/docker/docker-stop-kamehouse.sh -p dev
```

The Docker dev profile maps host `${HOME}/workspace/kamehouse` to `/home/goku/git/kamehouse` in the
container. Deploy changes from inside the container with:

```sh
cd ${HOME}/git/kamehouse
deploy-kamehouse.sh -c
```

See `docs/docker/docker-setup.md` for demo profiles, ports, credentials, and reinitialization
details.

## Logging

KameHouse shell scripts write a process log per script to `${HOME}/logs/<script-name>.log` and rotate
previous logs into `${HOME}/logs/old`.

Useful logs:

- Build: `${HOME}/logs/build-kamehouse.log`
- Deployment: `${HOME}/logs/deploy-kamehouse-dev.log` or `${HOME}/logs/deploy-kamehouse.log`
- Dev Tomcat: `${HOME}/programs/apache-tomcat-dev/logs/catalina.out`
- Dev Tomcat application log: `${HOME}/programs/apache-tomcat-dev/logs/kameHouse.log`
- Apache: `${HOME}/programs/apache-httpd/logs/`

Use `tail-log.sh` rather than manually tailing files when possible.

## Documentation

Consult these before changing behavior:

- `README.md`
- `docs/README.md`
- `docs/dev-environment/dev-environment-setup.md`
- `docs/execution/execution.md`
- `docs/docker/docker-setup.md`
- `docs/versions/versions.md`
- Module-specific `README.md` files under each `kamehouse-*` directory

## Validation checklist

For code changes, prefer:

```sh
./gradlew test
./gradlew :<changed-module>:check
build-kamehouse.sh
```

For mobile changes:

```sh
build-kamehouse.sh -m mobile
./gradlew :kamehouse-mobile:check
```

For shell-script changes, reinstall the shell before running the installed command:

```sh
deploy-kamehouse-dev.sh -m shell
```

For local deployment verification, use only the dev scripts and check `tomcat-status-dev.sh` plus
the dev Tomcat logs.
