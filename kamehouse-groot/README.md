| [Home](/README.md) | [Docs](/docs/README.md) |
---------------------------------------------------------------

*********************

# GRoot Module:

This module consists of apis to:

* Manage tomcat. Wrapper over tomcat's `/manager` app. `start/stop/deploy/undeploy` all kamehouse modules in the local server and all servers at once
* Start and stop tomcat
* Git pull from all my repos, both in local and all servers
* Tail logs in the local server
* Trigger kamehouse-shell scripts on the server over https
* Reboot the server

This module's apis are accessed through the ui module at the path `/kame-house/groot/`. So even though the /kame-house tomcat modules don't need to be deployed on tomcat for this app to run, /kame-house frontend code still needs to be available in the apache httpd web server to access the apis on `/kame-house-groot`.

The apis are served by a standalone Python web service, so apache httpd only serves the static frontend.

Install with the standard kamehouse deploy scripts.

GRoot relies on tomcat manager's app to be deployed and running.

# Python web service

`kamehouse-groot` is a standalone Python web service.

The service implements all GRoot api endpoints:

```
GET  /kame-house-groot/api/v1/admin/kamehouse-shell/execute
GET  /kame-house-groot/api/v1/admin/kamehouse-shell/scripts
POST /kame-house-groot/api/v1/auth/login
GET  /kame-house-groot/api/v1/auth/logout
GET  /kame-house-groot/api/v1/auth/authorize-page
GET  /kame-house-groot/api/v1/commons/session/status
GET  /kame-house-groot/api/v1/test/test
```

The frontend calls these endpoints directly.

The Python service is built with:

* FastAPI
* Uvicorn
* Pydantic v2
* SQLAlchemy 2.0 async
* `asyncmy`
* `bcrypt`
* `python-multipart`

The service runs independently on port `9091` and talks to the MariaDB database and the `kamehouse-shell` scripts.

## Architecture

```text
kamehouse-groot/
  pyproject.toml
  run.py
  src/main/python/kamehouse_groot/
    __main__.py                  # uvicorn entrypoint
    main.py                      # FastAPI app factory and lifespan
    config.py                    # GROOT_* settings
    logging_config.py            # file and console logging
    database.py                  # async MariaDB user/role queries
    api/
      dependencies.py            # service wiring and session/Basic auth
      errors.py                  # legacy JSON error contract
      v1/
        router.py                # API v1 router
        auth.py                  # login, logout and page authorization
        test_endpoint.py         # legacy HTML test endpoint
        admin/kamehouse_shell.py # build_admin_shell_router factory for the execute and scripts endpoints
        commons/session.py       # session status endpoint
    models/
      groot_session_status.py
      kamehouse_command_result.py
    services/
      auth_service.py            # credential, role and ROLE_KAMISAMA validation
      command_executor.py        # foreground and daemon execution
      docker_service.py          # docker container environment file
      password_verifier.py       # bcrypt verification
      secret_service.py          # reads secrets with kamehouse-shell
      session_service.py         # cookie based login sessions
      shell_execution_service.py # builds and maps exec-script.sh results
    utils/
      bash_colors.py             # ANSI color to HTML conversion
      input_validator.py         # input validation
  src/test/python/
    run_tests.py                 # unit tests with coverage thresholds
    unit/                        # unit tests
    integration/                 # opt-in tests against a running service
```

## Requirements

* Python 3.11 or newer. The development environment is tested with Python 3.12.
* Network access on the first install to download the Python dependencies into the virtual environment.
* MariaDB with the KameHouse database and users.
* `kamehouse-shell` installed at `${HOME}/programs/kamehouse-shell`.
* Encrypted KameHouse secrets, in particular `MARIADB_PASS_KAMEHOUSE`.

## Configuration

The service reads `GROOT_*` environment variables:

| Variable | Default | Description |
| --- | --- | --- |
| `GROOT_HOST` | `0.0.0.0` | Address where uvicorn listens |
| `GROOT_PORT` | `9091` | Port where uvicorn listens |
| `GROOT_LOG_LEVEL` | `INFO` | Log level |
| `GROOT_DEPLOY_PATH` | `${HOME}/programs/kamehouse-groot` | Deployment directory with `logs/`, `run/` and `conf/` |
| `GROOT_USER_HOME` | `${HOME}` | Home directory used to resolve `~/logs/<script>.log` |
| `GROOT_DB_HOST` | `127.0.0.1` | MariaDB host |
| `GROOT_DB_PORT` | `3306` | MariaDB port |
| `GROOT_DB_NAME` | `kamehouse` | MariaDB database |
| `GROOT_DB_USER` | `kamehouse` | MariaDB user |
| `GROOT_DB_PASSWORD_SECRET` | `MARIADB_PASS_KAMEHOUSE` | Secret key used to retrieve the database password |
| `GROOT_SHELL_BIN` | `${HOME}/programs/kamehouse-shell/bin` | kamehouse-shell bin directory |
| `GROOT_SECRET_SCRIPT` | `${GROOT_SHELL_BIN}/secrets/get-kamehouse-secret.sh` | Script used to read secrets |
| `GROOT_COMMAND_TIMEOUT_SECONDS` | `300` | Timeout for foreground scripts |
| `GROOT_DAEMON_WAIT_SECONDS` | `5` | Wait after starting a daemon script |
| `GROOT_USE_SCRIPT_LOG` | `true` | Redirect daemon output to `~/logs/<script>.log` |
| `GROOT_SESSION_COOKIE_NAME` | `kamehouse-groot-session` | Login session cookie name |
| `GROOT_SESSION_DIR` | `${GROOT_DEPLOY_PATH}/run/sessions` | Directory where login sessions are persisted. Set it to share sessions with another web service |
| `GROOT_SCRIPTS_CSV` | `csv/csv-kamehouse-shell.sh` | Script run by the `scripts` endpoint, relative to `${GROOT_SHELL_BIN}` |
| `GROOT_SERVICE_NAME` | `kamehouse-groot` | Name used for the log files, `<service_name>.log` and `<service_name>-access.log` |

The startup script maps the kamehouse config values to the service environment:

* `GROOT_BIND_IP` -> `GROOT_HOST`
* `GROOT_BIND_PORT` -> `GROOT_PORT`

Both variables are defined in `${HOME}/.kamehouse/config/kamehouse.cfg` and default to `0.0.0.0` and `9091`.

The database password is not stored in configuration. The service runs:

```sh
${HOME}/programs/kamehouse-shell/bin/secrets/get-kamehouse-secret.sh -s MARIADB_PASS_KAMEHOUSE
```

The value is cached in memory for the lifetime of the process.

## Authentication

The service supports both cookie sessions and HTTP Basic authentication.

* `POST /auth/login` validates a KameHouse username and password, creates a session and sets the `kamehouse-groot-session` cookie.
* `GET /auth/logout` destroys the session and clears the cookie.
* `GET /commons/session/status` returns the current session status and can also create a session from a Basic auth header.
* Admin endpoints accept an admin session cookie or Basic credentials with `ROLE_KAMISAMA`.

Credentials are validated against the KameHouse MariaDB tables:

* `kamehouse_user.username`
* `kamehouse_user.password` (bcrypt hash)
* `kamehouse_role.name` for the user

Any KameHouse user can log in. Only users with the `ROLE_KAMISAMA` role can access admin endpoints.

Password verification supports the bcrypt hash variants:

* `$2a$` hashes are supported.
* `$2b$` hashes are supported.
* `$2y$` hashes are normalized to `$2b$` before calling Python `bcrypt`.

The SQL queries are parameterized and the username and password are validated before querying the database.

Sessions are stored in memory and persisted under `${GROOT_DEPLOY_PATH}/run/sessions` so they survive service restarts. Session tokens are validated before being used as file names. Another web service sharing the same sessions, like `kamehouse-batcave-groot`, sets `GROOT_SESSION_DIR` to this directory and reads the files on every request without the in-memory cache.

### Authentication errors

| Situation | Status | Message |
| --- | --- | --- |
| Missing session and `Authorization` header | `401` | `Login as admin to /kame-house/groot/ to access this endpoint` |
| Invalid username or password | `401` | `Invalid username and password` |
| Valid user without `ROLE_KAMISAMA` | `401` | `Invalid username and password` |

A `WWW-Authenticate: Basic` challenge is returned with `401` responses.

## Extending kamehouse-groot

Other web services can build on `kamehouse-groot` instead of duplicating its wiring. `kamehouse-batcave-groot` in the kamehouse-batcave repository extends it this way to expose the batcave shell endpoints on port `9092`:

* Subclass `Settings` with its own `env_prefix`, `port`, `deploy_path`, `shell_bin`, `secret_script`, `session_dir`, `scripts_csv` and `service_name`.
* Subclass `ShellExecutionService` and override `is_forbidden_script` to use its own forbidden script list.
* Build its app with `create_base_app(settings, title=..., root_path=...)`, reusing the exception handlers and lifespan.
* Mount the shell endpoints with `build_admin_shell_router(prefix, tags)`.
* Wire its services with `build_container(settings, shell_execution_service=..., session_service=...)`.
* Share the login sessions with this service by pointing `session_dir` at `${HOME}/programs/kamehouse-groot/run/sessions` and constructing `SessionService(settings, cache_sessions=False)` so sessions created or deleted by either service are picked up on every request.

## Endpoint contract

### Execute a kamehouse-shell script

```http
GET /kame-house-groot/api/v1/admin/kamehouse-shell/execute
```

#### Query parameters

| Parameter | Required | Description |
| --- | --- | --- |
| `script` | yes | Script path relative to `${HOME}/programs/kamehouse-shell/bin` |
| `args` | no | Arguments passed to the script |
| `executeOnDockerHost` | no | `true` or `TRUE` to execute on the docker host |
| `daemon` | no | `true` or `TRUE` to start the script detached |
| `isDaemon` | no | Legacy alias for `daemon` |

Example:

```sh
curl -u seiya:ikki \
  'http://localhost:9091/kame-house-groot/api/v1/admin/kamehouse-shell/execute?script=is-linux-host.sh'
```

#### Successful response

```json
{
  "command": "/home/kamehouse/programs/kamehouse-shell/bin/sudoers/www-data/exec-script.sh -s is-linux-host.sh",
  "exitCode": 0,
  "pid": -1,
  "status": "completed",
  "standardOutput": ["true"],
  "standardOutputHtml": ["true"],
  "standardError": [],
  "standardErrorHtml": []
}
```

Fields:

| Field | Description |
| --- | --- |
| `command` | Executed command line |
| `exitCode` | Process exit code. `-1` for daemons and timeouts |
| `pid` | Daemon process id, or `-1` |
| `status` | `completed`, `failed` or `running` |
| `standardOutput` | Raw standard output lines |
| `standardOutputHtml` | Standard output with ANSI colors converted to HTML |
| `standardError` | Raw standard error lines |
| `standardErrorHtml` | Standard error with ANSI colors converted to HTML |

Status rules:

* Exit code `0` -> `completed`
* Exit code greater than `0` -> `failed`
* Timeout -> `running` with `exitCode=-1`
* Daemon -> `running` with `exitCode=-1` and the process id in `pid`

#### Error responses

Errors use the legacy JSON contract:

```json
{
  "code": 400,
  "message": "Invalid input"
}
```

| Situation | Status | Message |
| --- | --- | --- |
| Missing or invalid `script` or `args` | `400` | `Invalid input` |
| Forbidden script | `403` | `Script not allowed for execution: <script>` |
| Missing or invalid credentials | `401` | See authentication errors |
| Unexpected server error | `500` | `Internal server error` |

### List kamehouse-shell scripts

```http
GET /kame-house-groot/api/v1/admin/kamehouse-shell/scripts
```

Admin authentication is required. The response is a JSON array of script paths relative to `${HOME}/programs/kamehouse-shell/bin`.

Example:

```sh
curl -u seiya:ikki \
  'http://localhost:9091/kame-house-groot/api/v1/admin/kamehouse-shell/scripts'
```

The endpoint runs `csv/csv-kamehouse-shell.sh` and preserves the CSV-to-array conversion, including the leading and trailing empty entries. If the script produces no output, it returns `["couldn-find-scripts.sh"]`.

### Login

```http
POST /kame-house-groot/api/v1/auth/login
```

Form fields:

| Field | Required | Description |
| --- | --- | --- |
| `username` | yes | KameHouse username |
| `password` | yes | KameHouse password |
| `referrer` | no | Redirect target. Only `/kame-house/groot/...` paths are accepted |

Successful login redirects to `referrer` when it starts with `/kame-house/groot/`, otherwise to `/kame-house/groot/`, and sets the session cookie. Missing or invalid credentials redirect to `/kame-house/groot/login.html?error=true`.

### Logout

```http
GET /kame-house-groot/api/v1/auth/logout
```

Destroys the current session, clears the cookie and redirects to `/kame-house/groot/login.html?logout=true`.

### Authorize page

```http
GET /kame-house-groot/api/v1/auth/authorize-page
```

Returns an empty `200` response for an admin session. Otherwise it redirects to:

```
/kame-house/groot/login.html?unauthorizedPageAccess=true&referrer=<request-uri>
```

### Session status

```http
GET /kame-house-groot/api/v1/commons/session/status
```

Returns the current session status:

```json
{
  "server": "kamehouse-host",
  "username": "seiya",
  "isLinuxHost": true,
  "isLinuxDockerHost": false,
  "isDockerContainer": false,
  "dockerControlHost": false,
  "roles": ["ROLE_KAMISAMA"]
}
```

Without a session or Basic credentials, `username` is `anonymousUser` and `roles` is empty. A valid Basic auth header creates a session and sets the session cookie. An invalid Basic auth header destroys the existing session.

Docker flags are read from `${HOME}/.kamehouse/config/.kamehouse-docker-container-env`. Only the values `true` and `TRUE` are considered true.

### Test page

```http
GET /kame-house-groot/api/v1/test/test
```

Returns the legacy HTML test page with the request headers, `testFunction: mada mada dane` and the host OS example. It does not require authentication.

## Shell execution

The service runs scripts through:

```sh
${HOME}/programs/kamehouse-shell/bin/sudoers/www-data/exec-script.sh -s <script> [-a <args>] [--execute-on-docker-host] [--daemon]
```

The service runs as the kamehouse user, so it calls `exec-script.sh` directly

Behavior preserved from the original implementations:

* Scripts are resolved relative to `${HOME}/programs/kamehouse-shell/bin`.
* Input length and forbidden characters are validated.
* Foreground scripts time out after 300 seconds.
* Daemon scripts are started detached, the service waits 5 seconds and returns `running`.
* Daemon output is redirected to `~/logs/<script>.log`, for example `tomcat-status.sh` -> `~/logs/tomcat-status.log`.
* ANSI color codes are converted to HTML for the `*Html` fields.

## Database

The service connects to:

```
mysql+asyncmy://<user>:<password>@<host>:<port>/<database>
```

User lookup:

```sql
SELECT u.id, u.username, u.password
FROM kamehouse_user u
WHERE u.username = :username
```

Role lookup:

```sql
SELECT r.name
FROM kamehouse_role r
WHERE r.kamehouse_user_id = :user_id
```

The database password is retrieved from the encrypted KameHouse secrets store and cached in memory.

## Running locally

From the `kamehouse-groot` directory:

```sh
python3 -m venv .venv
.venv/bin/pip install -e '.[dev]'
.venv/bin/python -m kamehouse_groot
```

The service listens on `0.0.0.0:9091` by default.

OpenAPI documentation is available at:

* `http://localhost:9091/kame-house-groot/docs`
* `http://localhost:9091/kame-house-groot/redoc`
* `http://localhost:9091/kame-house-groot/openapi.json`

## Install and deploy

The Python service is installed by `deploy-kamehouse.sh` when the `groot` module is deployed. The deployment:

1. Runs `kamehouse-shell/bin/groot/install-kamehouse-groot-python.sh`.
2. Copies the Python sources to `${HOME}/programs/kamehouse-groot`.
3. Installs `python3-venv` (or the equivalent OS package) when the Python `venv`/`ensurepip` modules are missing.
4. Creates `${HOME}/programs/kamehouse-groot/venv` if needed or if its `pip` is missing.
5. Installs the package and its dependencies in editable mode.
6. Generates `${HOME}/programs/kamehouse-groot/conf/build-info.json`.
7. Restarts the Python service when `DEPLOYMENT_RESTART_GROOT=true`.

The deployment only requires Python 3.11 or newer to be installed. It bootstraps the virtual environment, `pip` and all Python dependencies from scratch. Installing the OS `venv` package requires `sudo` permissions on the server.

Deploy only the groot module:

```sh
IS_DEV_ENV=true deploy-kamehouse.sh -m groot
```

Install or reinstall the Python service manually from the repository root:

```sh
kamehouse-shell/bin/groot/install-kamehouse-groot-python.sh
kamehouse-shell/bin/groot/install-kamehouse-groot-python.sh -f
```

Use `-f` to force reinstalling the virtual environment dependencies. Use `-p` to install from the prod repository path `${HOME}/git/kamehouse` instead of the current directory.

## Service management

```sh
kamehouse-groot-startup.sh
kamehouse-groot-stop.sh
kamehouse-groot-restart.sh
kamehouse-groot-status.sh
kamehouse-groot-version.sh
```

The installed scripts are in `${HOME}/programs/kamehouse-shell/bin/groot`.

The service pid is stored in:

```
${HOME}/programs/kamehouse-groot/run/kamehouse-groot.pid
```

The deployment directory layout is:

```text
${HOME}/programs/kamehouse-groot/
  conf/build-info.json
  logs/kamehouse-groot.log
  logs/kamehouse-groot-access.log
  logs/kamehouse-groot-startup.log
  run/kamehouse-groot.pid
  run/sessions/
  src/main/python/kamehouse_groot/
  venv/
  pyproject.toml
  README.md
```

## Logs

Tail the groot application log:

```sh
tail-log.sh -f groot
```

Tail the groot access log:

```sh
tail-log.sh -f groot-access
```

Log files:

| File | Description |
| --- | --- |
| `kamehouse-groot.log` | Application log with rotation |
| `kamehouse-groot-access.log` | Uvicorn access log with rotation |
| `kamehouse-groot-startup.log` | Standard output and error from the startup script |

The startup log captures the standard output of the current run and logs the same as the application log, so `tail-log.sh -f groot` only tails the application log. It's still kept for troubleshooting failed startups.

The application log entries follow the kamehouse java log format and the access log entries follow the apache httpd access log format, so `tail-log.sh` shows them colorized through `format-tail-log.awk`, with the log level filter applied to both the log level tags and the http response codes.

## Unit tests

Unit tests run from the `kamehouse-groot` directory:

```sh
python3 src/test/python/run_tests.py
```

The runner:

1. Creates `.venv` if it doesn't exist.
2. Installs `.[dev]` on the first run.
3. Runs the unit tests under `src/test/python/unit`.
4. Generates `coverage.json`.
5. Fails when line coverage is below `80%` or branch coverage is below `60%`.

Options:

| Option | Description |
| --- | --- |
| `--no-install` | Skip dependency installation |
| `--reinstall` | Force reinstalling dependencies |
| `--integration` | Run unit tests and then integration tests |
| `--integration-only` | Run only integration tests |

`build-kamehouse.sh` runs the groot unit tests unless it's a fast build or an integration test build. Deploy does a fast build by default, so run `build-kamehouse.sh` separately for full validation. With `-i`, the build runs the groot integration tests instead of the unit tests.

## Integration tests

Integration tests run against a live service:

```sh
KAMEHOUSE_GROOT_INTEGRATION=true python3 src/test/python/run_tests.py --integration-only
```

They can also be run through the build script:

```sh
build-kamehouse.sh -m groot -i
```

Configuration:

| Variable | Default | Description |
| --- | --- | --- |
| `KAMEHOUSE_GROOT_INTEGRATION` | unset | Must be `true` to enable integration tests |
| `KAMEHOUSE_GROOT_TEST_URL` | `http://localhost:9091` | Base URL of the service |
| `KAMEHOUSE_TEST_USERNAME` | `seiya` | Test username |
| `KAMEHOUSE_TEST_PASSWORD` | `ikki` | Test password |

The integration tests cover:

* Missing authentication returns `401`.
* Invalid credentials return `401`.
* Executing `is-linux-host.sh` returns `completed` with exit code `0`.
* Missing, invalid and forbidden execute inputs return `400` or `403`.
* `scripts` returns the script list.
* `session/status` returns the anonymous, Basic-auth and invalid Basic-auth states.
* Login, logout and `authorize-page` redirects work, including referrer handling.
* The HTML test endpoint renders.

Start the service before running the integration tests:

```sh
kamehouse-groot-startup.sh
```

## Postman and .http collections

The Postman collection includes a `kamehouse-groot` folder

```sh
GROOT_SERVER=localhost:9091 \
kamehouse-shell/bin/test/kamehouse-postman-run.sh -p localhost
```

`GROOT_SERVER` defaults to `localhost:9091` in the Postman environments and to `http://localhost:9091` in `http/config/http-client.env.json`.

The `.http` requests are in `http/modules/kamehouse-groot.http`.

## Migration roadmap

The Python service implements the groot endpoints used by the frontend:

* `admin/kamehouse-shell/execute`
* `admin/kamehouse-shell/scripts`
* `auth/login`, `auth/logout`, `auth/authorize-page`
* `commons/session/status`
* `test/test`

The frontend uses these routes directly.

Tomcat, git, logs and system power operations are executed through the `kamehouse-shell/execute` and `kamehouse-shell/scripts` endpoints.

## Troubleshooting

### The service fails to start

Check:

```sh
kamehouse-groot-status.sh
tail-log.sh -f groot
```

Common causes:

* The virtual environment was not installed. Run `install-kamehouse-groot-python.sh`.
* Port `9091` is already in use.
* The MariaDB password secret cannot be read. Verify `get-kamehouse-secret.sh -s MARIADB_PASS_KAMEHOUSE`.
* MariaDB is not running or the database user cannot connect.

### Authentication always fails

* Verify the user exists in `kamehouse_user`.
* Verify the user has `ROLE_KAMISAMA` in `kamehouse_role`.
* Verify the password hash is a bcrypt hash.
* Check `kamehouse-groot.log` for the authentication failure reason.

### Script execution fails with a forbidden script error

The script is in the forbidden list. Use a dedicated kamehouse-shell script instead.

### Daemon output is missing

Daemon output is written to `~/logs/<script>.log` when `GROOT_USE_SCRIPT_LOG=true`. If the file doesn't exist, check that the kamehouse user can write to `${HOME}/logs`.

### First install requires network access

The virtual environment installs FastAPI, Uvicorn, SQLAlchemy, `asyncmy`, `bcrypt`, `python-multipart` and the test dependencies from PyPI. The installer reinstalls dependencies when `pyproject.toml` changes. Use `-f` to force a reinstall.

# Install:

* [optional] Update `GROOT_API_BASIC_AUTH` in `${HOME}/.kamehouse/config/keys/.kamehouse-secrets.cfg` with base64 encoded user:pass to execute deploy/git pull on remote servers from groot

## Linux:

* Run `install-kamehouse-groot.sh -u kamehouseUsername` and `set-kamehouse-sudoers-permissions.sh -u kamehouseUsername` to allow `www-data` user to execute `kamehouse-shell` scripts from kamehouse-groot. These scripts are executed from `install-kamehouse.sh`
