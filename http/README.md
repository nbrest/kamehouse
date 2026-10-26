# Kamehouse HTTP requests

The `.http` files in [`modules`](modules) mirror the requests, order,
pre-request scripts and tests from
[`postman/collections/kame-house.postman_collection.json`](../postman/collections/kame-house.postman_collection.json).

They are executed with [httpyac](https://httpyac.github.io/), which supports
pre-request and post-request JavaScript, `test()` assertions, variables and a
CLI. The old VS Code REST Client environment variables were moved to
[`http-client.env.json`](http-client.env.json).

## Requirements

- Node.js 20 or newer.
- The httpyac VS Code extension (`anweber.vscode-httpyac`) or the CLI.
- Install the local CLI dependency:

```sh
cd http
npm install
```

## Environments

[`http-client.env.json`](http-client.env.json) contains the environments that
were previously in `.vscode/settings.json`:

| Environment | `SERVER` | `TOMCAT_SERVER` |
| --- | --- | --- |
| `localhost-dev-tomcat` | `http://localhost:9989` | `http://localhost:9980` |
| `localhost-dev-docker` | `http://localhost:6080` | `http://localhost:6080` |
| `localhost-prod-https` | `https://localhost` | `https://localhost` |

The common values (`API_VERSION`, `invalid*`, etc.) and the public test
credentials (`seiya` / `ikki`) are duplicated in each environment because
httpyac does not support the JetBrains `$shared` section.

The Postman collection hardcodes `http://localhost:9989` for the Groot
requests. These files use `{{SERVER}}` instead so the same requests work with
the Tomcat, Docker and production environments.

To override values locally, create `http-client.private.env.json`, which is
ignored by git and is loaded on top of the public file:

```json
{
  "localhost-dev-tomcat": {
    "USERNAME": "seiya",
    "PASSWORD": "ikki",
    "tennisWorldUser": "your-tennis-world-user",
    "tennisWorldPassword": "your-tennis-world-password"
  }
}
```

## Run from the CLI

Run every module in Postman collection order:

```sh
cd http
npm run send:dev-tomcat
npm run send:dev-docker
npm run send:prod-https
```

These scripts run the full collection: they create, update and delete test
entities and send system/VLC commands. Use them against a dev environment and
review the requests before pointing them at production. The reboot and shutdown
requests are skipped by default; see below to enable them.

From the repository root, the kamehouse-shell wrapper runs the `dev-tomcat`
collection with `--bail` and forces the destructive requests to be skipped:

```sh
kamehouse-shell/bin/test/kamehouse-http-run.sh
```

Pass `-d` to also run the `reboot` and `shutdown` requests:

```sh
kamehouse-shell/bin/test/kamehouse-http-run.sh -d
```

The opencode command `kamehouse-http-run-all` wraps the same script with
`run-with-keyboard-sink.sh`, because the admin screen requests inject VNC
keystrokes into the active desktop window.

Run one file, or one request in a file:

```sh
npx httpyac send modules/kamehouse-admin.http --env localhost-dev-tomcat --all
npx httpyac send modules/kamehouse-admin.http --env localhost-dev-tomcat --name "kamehouse-admin / admin / system-state / / uptime"
npx httpyac send modules/kamehouse-admin.http --env localhost-dev-tomcat --line <line-number>
```

`--name` executes every request with that name. Some Postman request names
repeat (for example the GET and POST of the same path), so use `--line` or a
unique name when you only want one request.

Override a variable for one run:

```sh
npx httpyac send modules/kamehouse-admin.http --env localhost-dev-tomcat --all \
  --var USERNAME=seiya --var PASSWORD=ikki
```

The `reboot` and `shutdown` requests are skipped unless destructive tests are
explicitly enabled. To run them one at a time:

```sh
npx httpyac send modules/kamehouse-admin.http --env localhost-dev-tomcat \
  --name "kamehouse-admin / admin / power-management / reboot / /" \
  --var RUN_DESTRUCTIVE_TESTS=true
```

```sh
npx httpyac send modules/kamehouse-admin.http --env localhost-dev-tomcat \
  --name "kamehouse-admin / admin / power-management / shutdown / /" \
  --var RUN_DESTRUCTIVE_TESTS=true
```

The `kamehouse-httpd` module contains the HTTPD restart request from the
Postman collection. It is run last by the `send:*` scripts; remove that file
from the command if you do not want to restart Apache.

## Scripts and variables

The collection-level pre-request script sets `$global.currentDate` and
`$global.futureDate` at the top of every module file. Runtime variables set by
responses are stored in httpyac's `$global` scope and referenced as
`{{$global.id}}`, `{{$global.username}}`, etc.

Postman dynamic variables are mapped to httpyac variables:

| Postman | httpyac |
| --- | --- |
| `{{$randomInt}}` | `{{$random.integer(0, 999)}}` |
| `{{$randomUserName}}` | `{{$random.alphabetic(10)}}` |
| `{{$randomPassword}}` | `{{$random.alphanumeric(12)}}` |
| `{{$randomEmail}}` | `{{$random.email}}` |
| `{{$randomFirstName}}` | `{{$random.alphabetic(8)}}` |
| `{{$randomLastName}}` | `{{$random.alphabetic(8)}}` |

`tennisWorldUser` and `tennisWorldPassword` are empty by default, matching the
Postman globals. Set them in `http-client.private.env.json` or with `--var`
before running real Tennis World booking requests.

The authentication login/logout/authorize requests use `# @noRedirect`,
because httpyac follows redirects by default while Postman does not.

## VSCode

- Install anweber httpYac plugin to execute these requests via vscode
- In editor settings configure the plugin httpyac to allow selecting the environment from the status bar at the bottom
