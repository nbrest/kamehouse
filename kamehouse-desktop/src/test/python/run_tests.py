#!/usr/bin/env python3
"""Run the KameHouse Desktop unit tests in an isolated virtual environment."""

from __future__ import annotations

import argparse
import hashlib
import json
import os
import subprocess
import sys
import tempfile
from pathlib import Path

MIN_LINE_COVERAGE = 80.0
MIN_BRANCH_COVERAGE = 60.0

DESKTOP_DIR = Path(__file__).resolve().parents[3]
PYPROJECT = DESKTOP_DIR / "pyproject.toml"
VENV = DESKTOP_DIR / ".venv"
VENV_PYTHON = VENV / ("Scripts/python.exe" if os.name == "nt" else "bin/python")
INSTALL_STAMP = VENV / ".kamehouse-desktop-installed"


def run(command, env=None):
    print(f"Running: {' '.join(command)}")
    return subprocess.run(command, cwd=DESKTOP_DIR, env=env).returncode


def pyproject_hash():
    return hashlib.sha256(PYPROJECT.read_bytes()).hexdigest()


def ensure_venv(install, reinstall):
    if not VENV_PYTHON.exists():
        print(f"Creating virtual environment at {VENV}")
        subprocess.run([sys.executable, "-m", "venv", str(VENV)], check=True)

    if not install:
        return

    installed_hash = INSTALL_STAMP.read_text(encoding="utf-8").strip() if INSTALL_STAMP.exists() else ""
    current_hash = pyproject_hash()
    if reinstall or installed_hash != current_hash:
        result = run([str(VENV_PYTHON), "-m", "pip", "install", "--upgrade", "pip"])
        if result != 0:
            raise SystemExit("Failed to upgrade pip in the kamehouse-desktop virtual environment")
        result = run([str(VENV_PYTHON), "-m", "pip", "install", "-e", f"{DESKTOP_DIR}[dev]"])
        if result != 0:
            raise SystemExit("Failed to install kamehouse-desktop dependencies")
        INSTALL_STAMP.write_text(f"{current_hash}\n", encoding="utf-8")


def run_unit_tests():
    env = os.environ.copy()
    env.setdefault("QT_QPA_PLATFORM", "offscreen")

    with tempfile.TemporaryDirectory(prefix="kamehouse-desktop-coverage-") as temp_dir:
        coverage_data = str(Path(temp_dir) / ".coverage")
        coverage_json = Path(temp_dir) / "coverage.json"
        env["COVERAGE_FILE"] = coverage_data

        test_result = run(
            [
                str(VENV_PYTHON), "-m", "coverage", "run",
                "--branch", "--source=bin", "-m", "pytest", "src/test/python",
            ],
            env,
        )

        run([str(VENV_PYTHON), "-m", "coverage", "report", "-m"], env)

        if test_result != 0:
            return test_result

        json_result = run(
            [str(VENV_PYTHON), "-m", "coverage", "json", "-o", str(coverage_json)],
            env,
        )
        if json_result != 0:
            print("Unable to generate the coverage report", file=sys.stderr)
            return json_result

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


def parse_arguments():
    parser = argparse.ArgumentParser(description="Run KameHouse Desktop tests")
    parser.add_argument("--no-install", action="store_true", help="skip dependency installation")
    parser.add_argument("--reinstall", action="store_true", help="force dependency installation")
    return parser.parse_args()


def main():
    args = parse_arguments()
    ensure_venv(install=not args.no_install, reinstall=args.reinstall)
    return run_unit_tests()


if __name__ == "__main__":
    sys.exit(main())
