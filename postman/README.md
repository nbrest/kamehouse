# KameHouse Postman collection

Postman collection and environments to exercise the KameHouse REST APIs.

## Contents

- `collections/kame-house.postman_collection.json`: the collection. Top-level folders:
  `kamehouse-admin`, `kamehouse-auth`, `kamehouse-groot`, `kamehouse-media`,
  `kamehouse-tennisworld`, `kamehouse-testmodule`, `kamehouse-ui`, `kamehouse-vlcrc`,
  `kamehouse-httpd`.
- `environments/`: one environment per local deployment target.
- `globals/workspace.postman_globals.json`: shared globals (invalid IDs, usernames, paths, etc.).

## Requirements

- Node.js 18+ and npm.
- Newman 6.x (Postman's CLI runner). It does not need to be installed globally: `npx` can run it.
- A running KameHouse instance and the credentials for it.

Check what is available:

```sh
node --version
npm --version
npx newman --version
```

Install Newman globally if you prefer not to download it on every `npx` invocation:

```sh
npm install -g newman@6
```

No other tooling is required. `jq` is optional, only useful to inspect the JSON files.

## Environments

| Environment file | `SERVER` | Target |
| --- | --- | --- |
| `localhost-9989-dev-tomcat.postman_environment.json` | `localhost:9989` | Local dev Apache Httpd, proxied to Tomcat on `localhost:9980` |
| `localhost-6080-dev-docker.postman_environment.json` | `localhost:6080` | Docker dev container |
| `localhost-443-prod-httpd.postman_environment.json` | `https://localhost` | Local production-style Apache Httpd |

Each environment defines `SERVER`, `USERNAME` and `PASSWORD`. Dev defaults are `seiya` / `ikki`.
The dev Tomcat environment also defines `TOMCAT_SERVER=localhost:9980` for the one request that
must bypass the Apache proxy (see `kamehouse-httpd` below).

The collection pre-request script sets `currentDate` and `futureDate` automatically.

## Run the collection

Always pass `--ignore-redirects`. Login, logout and several APIs return `302`; the redirect target
is not needed by the tests.

Run every folder against the local dev instance:

```sh
npx newman run postman/collections/kame-house.postman_collection.json \
  -e postman/environments/localhost-9989-dev-tomcat.postman_environment.json \
  -g postman/globals/workspace.postman_globals.json \
  --ignore-redirects --reporters cli --timeout-request 60000
```

Run a single folder (use the top-level folder name):

```sh
npx newman run postman/collections/kame-house.postman_collection.json \
  -e postman/environments/localhost-9989-dev-tomcat.postman_environment.json \
  -g postman/globals/workspace.postman_globals.json \
  --folder kamehouse-admin \
  --ignore-redirects --reporters cli --timeout-request 60000
```

Repeat `--folder` to run several folders. Do not pass a folder path such as `parent / child`;
Newman expects a folder name and otherwise fails with `Invalid entrypoint`.

For unattended runs, redirect the output so terminal key presses cannot interfere:

```sh
nohup npx newman run postman/collections/kame-house.postman_collection.json \
  -e postman/environments/localhost-9989-dev-tomcat.postman_environment.json \
  -g postman/globals/workspace.postman_globals.json \
  --ignore-redirects --reporters cli --timeout-request 60000 \
  > /tmp/kamehouse-postman.log 2>&1 &
```

The collection does not read keyboard input. If an ESC key press aborts a foreground run in your
terminal, use the detached form above and inspect `/tmp/kamehouse-postman.log`.

Useful options:

- `--bail`: stop on the first failing request.
- `--reporters cli,json --reporter-json-export /tmp/newman.json`: also write a JSON report.
- `--env-var tennisWorldUser=... --env-var tennisWorldPassword=...`: override variables for one run.

## Folder notes

- `kamehouse-ui` and `kamehouse-groot` need Apache Httpd serving the UI/Groot content. The dev
  environment points `SERVER` at Apache on port 9989, which proxies API calls to Tomcat on 9980.
  `kamehouse-ui` only checks the static UI pages; the UI module has no REST controllers.
- `kamehouse-groot` covers every PHP endpoint under `kamehouse-groot/src/main/php/kame-house-groot/api/v1`.
  The PHP files under `.../api/v1/kamehouse` are shared includes, not endpoints.
- `kamehouse-testmodule`: the servlet subfolder logs in through `/kame-house-auth/login` to obtain
  the session cookie expected by the servlet endpoints, then logs out.
- `kamehouse-tennisworld`: CRUD folders are self-contained (they create and delete their own users,
  booking requests and responses). The `bookings` folder calls the real TennisWorld service, so it
  depends on external credentials and network access.
- `kamehouse-vlcrc`: player status, playlist and command tests expect a local VLC player with its
  HTTP interface. The `vlc-process` requests start/stop that player.
- `kamehouse-httpd`: restarts Apache Httpd through `TOMCAT_SERVER` so stopping Apache does not kill
  the request. It runs last and briefly makes port 9989 unavailable.
- The `reboot` request is skipped by default because rebooting the host would abort the run. Set
  `RUN_DESTRUCTIVE_TESTS=true` only when you really want to execute it.

## Security notes

- The collection creates and deletes its own test data. A failed run can leave records behind.
- Do not put production passwords in the environment files. Use `--env-var` for one-off runs.

## Endpoint coverage

The collection covers every client-facing REST mapping in the `admin`, `auth`, `media`,
`tennisworld`, `testmodule`, `vlcrc` and `commons` controllers, the Spring Security
`login`/`logout` endpoints, and all Groot PHP API endpoints. Commons endpoints are exercised through the `kamehouse-testmodule`
deployment because every webapp exposes the same commons controllers.

The `testmodule` servlet endpoints are included too, even though they are servlets rather than REST
controllers. The commons `ErrorController` (`/errors`) is an internal error-dispatch endpoint used by
`web.xml`. It is not a client-facing API and cannot be invoked directly with a meaningful result,
so it has no standalone request.
