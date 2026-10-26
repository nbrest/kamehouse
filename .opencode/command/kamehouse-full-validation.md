---
description: Run full validation of changes, including API contract sync checks, Postman and .http collections, tests, builds, and Sonar
agent: build
---

Run the full validation sequence for changes. Follow these steps in order. **Retry each step up to 3 times** before considering it a failure, except the API contract and `.http` consistency checks, which must stop on the first mismatch. STOP if a step fails after 3 retries. Report results clearly.

## Steps
1. **API contract sync check (conditional)**: Inspect the uncommitted working copy for API contract changes before running the expensive validation steps.
   - Check staged, unstaged, and untracked files: `git status --short --untracked-files=all` and `git diff HEAD --`.
   - Look for changes to Java controllers, servlets, Spring Security login/logout handling, Groot PHP endpoints under `kamehouse-groot/src/main/php/kame-house-groot/api/v1/`, and request/response models that affect the HTTP contract.
   - Contract changes include adding, removing, renaming, or changing an endpoint's method, path, query/path/body parameters, authentication, response fields, or expected status codes.
   - If there are no API contract changes, continue to step 2.
   - If there are API contract changes, verify every affected endpoint is reflected in **both**:
     - `postman/collections/kame-house.postman_collection.json`
     - the corresponding `http/modules/kamehouse-*.http` file(s), including `kamehouse-groot.http` for Groot
   - Verify added endpoints have requests in both collections, changed endpoints have matching method/path/parameters/body/expected status, and deleted endpoints have no stale requests left behind.
   - Commons controller changes are exposed through the webapp contexts; ensure the Postman and `.http` requests use an appropriate context. Testmodule servlet endpoints must also remain covered. Do not add a direct request for the internal `ErrorController` `/errors` endpoint.
   - STOP immediately and report the exact missing or stale entries if either collection does not reflect the changes. Do not run the remaining steps until the collections are updated.
2. **HTTP collection consistency check**: Verify the full `.http` collection is consistent with the API contract and the Postman collection. Always run this check and STOP on the first mismatch.
   - Compare every request in `postman/collections/kame-house.postman_collection.json` with the corresponding `http/modules/kamehouse-*.http` request: method, path, query/path parameters, headers, body, authentication, expected status, and pre-request/test behavior that affects the contract.
   - Confirm every API endpoint covered by the Postman collection is also covered by the `.http` collection.
   - Confirm every `.http` request also exists in the Postman collection, so no stale requests remain.
   - Confirm the module execution order matches the Postman top-level folder order: `kamehouse-admin`, `kamehouse-auth`, `kamehouse-groot`, `kamehouse-media`, `kamehouse-tennisworld`, `kamehouse-testmodule`, `kamehouse-ui`, `kamehouse-vlcrc`, `kamehouse-httpd`.
   - Confirm all Groot PHP endpoints and testmodule servlet endpoints are covered, and the internal `ErrorController` `/errors` endpoint remains excluded.
   - Confirm the `reboot` and `shutdown` requests remain guarded by `RUN_DESTRUCTIVE_TESTS` so the automated `.http` runner skips them.
   - If there is any mismatch, STOP immediately, report the exact file/request, and update the `.http` files to match the API contract and Postman collection. Do not run the remaining steps until the collections are consistent.
3. **Unit tests**: Run `./gradlew test` from repo root (retry up to 3x). Ensure all unit tests pass (*IntegrationTest excluded).
4. **Deploy to tomcat-dev**: Run `deploy-kamehouse-dev.sh` from repo root (dev scripts only, retry up to 3x). Verify deployment succeeds. After deployment, wait for Tomcat to finish deploying webapps (check `tomcat-status-dev.sh`, tail `${HOME}/programs/apache-tomcat-dev/logs/catalina.out` for deployment completion/messages like "Deployment of web application archive" or context startup, or wait until contexts are ready). Do not proceed until webapps are deployed/started.
5. **Integration tests**: Ensure secrets are loaded. The integration tests exercise `ScreenController` and inject VNC keystrokes (ESC, ENTER, mouse clicks) into the active desktop window, so always run them through `kamehouse-shell/bin/test/run-with-keyboard-sink.sh`. This opens a full-screen keyboard-sink terminal, focuses it for the duration of the tests, and restores the previously active window afterwards. Use `-p docker` if running in a container (retry up to 3x if startup/deployment not ready). Launch detached and wait for the status file:
   ```sh
   LOG=/tmp/kamehouse-integration.log
   STATUSFILE=/tmp/kamehouse-integration.status
   rm -f "$LOG" "$STATUSFILE"
   nohup setsid bash -c '
     kamehouse-shell/bin/test/run-with-keyboard-sink.sh -- build-kamehouse.sh -i > "$1" 2>&1
     echo $? > "$2"
   ' _ "$LOG" "$STATUSFILE" </dev/null >/dev/null 2>&1 &
   echo $!
   ```
   Wait until `/tmp/kamehouse-integration.status` exists (tool timeout of at least 20 minutes), then check it and the logs (`/tmp/kamehouse-integration.log`, `${HOME}/logs/build-kamehouse.log`). Verify success. Do not type into opencode while the sink has focus, and ignore stray text injected during the tests.
6. **Postman collection**: Execute the opencode command `kamehouse-postman-run-all` from the repository root (retry up to 3x):
   ```sh
   rm -f /tmp/kamehouse-postman-run-all.status
   opencode run --command kamehouse-postman-run-all </dev/null
   ```
   Wait for it to finish, then verify `/tmp/kamehouse-postman-run-all.status` is `0` and inspect `/tmp/kamehouse-postman-run-all.log`. If non-zero, report the failing request/assertion and stop. The command uses the keyboard sink, so do not type into opencode while it runs.
7. **HTTP files collection**: Execute the opencode command `kamehouse-http-run-all` from the repository root (retry up to 3x):
   ```sh
   rm -f /tmp/kamehouse-http-run-all.status
   opencode run --command kamehouse-http-run-all </dev/null
   ```
   Wait for it to finish, then verify `/tmp/kamehouse-http-run-all.status` is `0` and inspect `/tmp/kamehouse-http-run-all.log`. If non-zero, report the failing request/assertion and stop. The runner skips `reboot` and `shutdown`, uses the keyboard sink, and must not be interrupted by typing into opencode while it runs.
8. **Full build for coverage/checkstyle/SpotBugs**: Run `build-kamehouse.sh` (full build, not `-f`, retry up to 3x). This runs tests, checkstyle, SpotBugs, JaCoCo for coverage. Verify it passes.
9. **Mobile build**: Run `build-kamehouse.sh -m mobile` (retry up to 3x). Verify APK build succeeds.
10. **SonarCloud analysis**: Run `sonarcloud-run-kamehouse.sh` (retry up to 3x). Verify it completes successfully (needs SONAR_TOKEN from `${HOME}/.kamehouse`).

## Rules
- API contract changes in the working copy must be reflected in both the Postman collection and the relevant `.http` collection(s).
- The `.http` collection must stay consistent with the Postman collection and the API contract, including request order, methods, paths, parameters, bodies and expected statuses.
- Use dev scripts only (`*-dev.sh`). Never use prod scripts.
- If any step fails, do NOT commit. Report which step failed and why.
- For shell script changes, ensure `deploy-kamehouse-dev.sh -m shell` was done and tested.
- Check tomcat-dev status/logs if deployment fails.
- If the integration test, Postman or `.http` log contains `keyboard sink unavailable`, warn that test keystrokes may have reached the active window.

$ARGUMENTS
