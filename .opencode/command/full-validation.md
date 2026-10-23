---
description: Run full validation of changes (unit tests, deploy to tomcat-dev, integration tests, coverage/checkstyle/spotbugs, mobile build, sonar)
agent: build
---

Run the full validation sequence for changes. Follow these steps in order. **Retry each step up to 3 times** before considering it a failure. STOP if a step fails after 3 retries. Report results clearly.

## Steps
1. **Unit tests**: Run `./gradlew test` from repo root (retry up to 3x). Ensure all unit tests pass (*IntegrationTest excluded).
2. **Deploy to tomcat-dev**: Run `deploy-kamehouse-dev.sh` from repo root (dev scripts only, retry up to 3x). Verify deployment succeeds. After deployment, wait for Tomcat to finish deploying webapps (check `tomcat-status-dev.sh`, tail `${HOME}/programs/apache-tomcat-dev/logs/catalina.out` for deployment completion/messages like "Deployment of web application archive" or context startup, or wait until contexts are ready). Do not proceed until webapps are deployed/started.
3. **Integration tests**: Ensure secrets are loaded. Run integration tests detached/background (ESC keypress can abort in foreground). Use `nohup build-kamehouse.sh -i > /tmp/kamehouse-integration.log 2>&1 &` or `-p docker` if in container (retry up to 3x if startup/deployment not ready). Wait for completion and check logs (`/tmp/kamehouse-integration.log`, `${HOME}/logs/build-kamehouse.log`). Verify success.
4. **Full build for coverage/checkstyle/SpotBugs**: Run `build-kamehouse.sh` (full build, not `-f`, retry up to 3x). This runs tests, checkstyle, SpotBugs, JaCoCo for coverage. Verify it passes.
5. **Mobile build**: Run `build-kamehouse.sh -m mobile` (retry up to 3x). Verify APK build succeeds.
6. **SonarCloud analysis**: Run `sonarcloud-run-kamehouse.sh` (retry up to 3x). Verify it completes successfully (needs SONAR_TOKEN from `${HOME}/.kamehouse`).

## Rules
- Use dev scripts only (`*-dev.sh`). Never use prod scripts.
- If any step fails, do NOT commit. Report which step failed and why.
- For shell script changes, ensure `deploy-kamehouse-dev.sh -m shell` was done and tested.
- Check tomcat-dev status/logs if deployment fails.

$ARGUMENTS
