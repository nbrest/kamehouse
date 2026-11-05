#!/usr/bin/env python3
import json
import os
import subprocess
import sys
import tempfile
from pathlib import Path

MIN_LINE_COVERAGE = 80.0
MIN_BRANCH_COVERAGE = 60.0

DESKTOP_DIR = Path(__file__).resolve().parents[3]

INSTALL_MESSAGE = (
    "Python test dependencies are missing. Install them with:\n"
    "  python3 -m pip install --user pytest coverage loguru requests websocket websocket-client stomper\n"
    "PyQt5 must be installed separately, for example:\n"
    "  sudo apt-get install -y python3-pyqt5\n"
    "See kamehouse-desktop/README.md for details."
)


def run(command, env):
    return subprocess.run(command, cwd=DESKTOP_DIR, env=env)


def main():
    env = os.environ.copy()
    env.setdefault("QT_QPA_PLATFORM", "offscreen")

    with tempfile.TemporaryDirectory(prefix="kamehouse-desktop-coverage-") as temp_dir:
        coverage_data = str(Path(temp_dir) / ".coverage")
        coverage_json = Path(temp_dir) / "coverage.json"
        env["COVERAGE_FILE"] = coverage_data

        check = subprocess.run(
            [
                sys.executable, "-c",
                "import PyQt5, loguru, requests, websocket, stomper, pytest, coverage",
            ],
            cwd=DESKTOP_DIR,
            env=env,
            stdout=subprocess.DEVNULL,
            stderr=subprocess.DEVNULL,
        )
        if check.returncode != 0:
            print(INSTALL_MESSAGE, file=sys.stderr)
            return 2

        test_result = run(
            [
                sys.executable, "-m", "coverage", "run",
                "--branch", "--source=bin", "-m", "pytest", "src/test/python",
            ],
            env,
        )

        run([sys.executable, "-m", "coverage", "report", "-m"], env)

        if test_result.returncode != 0:
            return test_result.returncode

        json_result = run(
            [sys.executable, "-m", "coverage", "json", "-o", str(coverage_json)],
            env,
        )
        if json_result.returncode != 0:
            print("Unable to generate the coverage report", file=sys.stderr)
            return json_result.returncode

        totals = json.loads(coverage_json.read_text())["totals"]
        statements = totals.get("num_statements", 0)
        covered_lines = totals.get("covered_lines", 0)
        branches = totals.get("num_branches", 0)
        covered_branches = totals.get("covered_branches", 0)

        line_coverage = (covered_lines / statements * 100) if statements else 100.0
        branch_coverage = (covered_branches / branches * 100) if branches else 100.0

        print()
        print(f"Line coverage: {line_coverage:.2f}% (minimum {MIN_LINE_COVERAGE:.0f}%)")
        print(f"Branch coverage: {branch_coverage:.2f}% (minimum {MIN_BRANCH_COVERAGE:.0f}%)")

        failed = False
        if line_coverage < MIN_LINE_COVERAGE:
            print("Line coverage is below the minimum threshold", file=sys.stderr)
            failed = True
        if branch_coverage < MIN_BRANCH_COVERAGE:
            print("Branch coverage is below the minimum threshold", file=sys.stderr)
            failed = True
        return 1 if failed else 0


if __name__ == "__main__":
    sys.exit(main())
