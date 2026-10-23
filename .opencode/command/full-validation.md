---
description: Run full validation of changes (unit tests, deploy to tomcat-dev, integration tests, coverage/checkstyle/spotbugs, mobile build, sonar)
agent: build
---

Run the full validation sequence for changes. Follow these steps in order and STOP if any step fails. Report results clearly.

## Steps
1. **Unit tests**: Run `./gradlew test` from repo root. Ensure all unit tests pass (*IntegrationTest excluded).
2. **Deploy to tomcat-dev**: Run `deploy-kamehouse-dev.sh` from repo root (dev scripts only). Verify deployment succeeds and tomcat-dev is ready.
3. **Integration tests**: Run integration tests detached/background (ESC keypress can abort in foreground). Use `nohup build-kamehouse.sh -i > /tmp/kamehouse-integration.log 2>&1 &` or `-p docker` if in container. Wait for completion and check logs (`/tmp/kamehouse-integration.log`, `${HOME}/logs/build-kamehouse.log`). Ensure secrets are loaded.
4. **Full build for coverage/checkstyle/SpotBugs**: Run `build-kamehouse.sh` (full build, not `-f`). This runs tests, checkstyle, SpotBugs, JaCoCo for coverage. Verify it passes.
5. **Mobile build**: Run `build-kamehouse.sh -m mobile`. Verify APK build succeeds.
6. **SonarCloud analysis**: Run `sonarcloud-run-kamehouse.sh`. Verify it completes successfully (needs SONAR_TOKEN from `${HOME}/.kamehouse`).

## Rules
- Use dev scripts only (`*-dev.sh`). Never use prod scripts.
- If any step fails, do NOT commit. Report which step failed and why.
- For shell script changes, ensure `deploy-kamehouse-dev.sh -m shell` was done and tested.
- Check tomcat-dev status/logs if deployment fails.

$ARGUMENTS
