| [Home](/README.md) | [Docs](/docs/README.md) |
---------------------------------------------------------------

*********************

# nbrest-logger:

Basic logging framework to format logs to the console in a node app.

# Install:

- In the root of your node app:

```sh
npm i nbrest-logger
```

# Update to latest version:

- In the root of your node app:

```sh
npm update nbrest-logger
```

# Usage:

```js
const logger = require("nbrest-logger");
logger.trace("my message");
logger.debug("my message");
logger.info("my message");
logger.warn("my message");
logger.error("my message");
```

- Change the log level during runtime:
```js
logger.setLogLevel("ERROR");
logger.setLogLevel("WARN");
logger.setLogLevel("DEBUG");
logger.setLogLevel("trace");
```

- The default log level is `INFO`, so `DEBUG` and `TRACE` entries are hidden by default. To change the log level at application startup, pass the log or LOG argument to your node app:

```sh
npm start LOG=TRACE // enables both trace and debug logs
npm start log=debug // enables debug logs
npm start log=warn // set log level to warn
npm start log=error // set log level to error
```

# Output:

```
> sample-express-server@1.0.0 start
> npm run devserver LOG=trace


> sample-express-server@1.0.0 devserver
> node ./server/node/server.js LOG=trace

2023-12-06 23:01:53 - [INFO] - Overriding logLevel with command line parameter log: trace mapped to logLevelNumber: 4
2023-12-06 23:01:54 - [TRACE] - Finished initializing nbrest-logger
2023-12-06 23:01:54 - [INFO] - Initializing endpoints
2023-12-06 23:01:54 - [INFO] - Startup server
2023-12-06 23:01:54 - [DEBUG] - my DEBUG message
2023-12-06 23:01:54 - [TRACE] - my TRACE message
2023-12-06 23:01:54 - [INFO] - Listening on http://localhost:8080
```

# Publish:

- Update version in `package.json`
- Publish changes to [npmjs](https://www.npmjs.com/package/nbrest-logger) with:

```sh
npm publish
```