---------------------------------------------------------------
| | |
---------------------------------------------------------------
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

# Usage:

```js
const logger = require("nbrest-logger");
logger.trace("my message");
logger.debug("my message");
logger.info("my message");
logger.warn("my message");
logger.error("my message");
```

- The default log level is `INFO`, so `DEBUG` and `TRACE` entries are hidden. To change the log level, pass the log or LOG argument to your node app:

```sh
npm start LOG=TRACE // enables both trace and debug logs
npm start log=debug // enables debug logs
```

# Publish:

- Update version in `package.json`
- Publish changes to [npmjs](https://www.npmjs.com/package/nbrest-logger) with:

```sh
npm publish
```