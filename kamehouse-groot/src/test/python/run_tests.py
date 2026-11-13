#!/usr/bin/env python3
"""Run the KameHouse GRoot unit and integration tests."""

from __future__ import annotations

import argparse
import json
import os
import subprocess
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[3]
VENV = ROOT / ".venv"
VENV_PYTHON = VENV / "bin" / "python"
INSTALL_STAMP = VENV / ".kamehouse-groot-installed"
COVERAGE_JSON = ROOT / "coverage.json"

MIN_LINE_COVERAGE = 80.0
MIN_BRANCH_COVERAGE = 60.0


def run(command: list[str], env: dict[str, str] | None = None) -> int:
    print(f"Running: {' '.join(command)}")
    return subprocess.run(command, cwd=ROOT, env=env, check=False).returncode


def ensure_venv(install: bool, reinstall: bool) -> None:
    if not VENV_PYTHON.exists():
        print(f"Creating virtual environment at {VENV}")
        subprocess.run([sys.executable, "-m", "venv", str(VENV)], check=True)

    if not install:
        return

    if reinstall or not INSTALL_STAMP.exists():
        run([str(VENV_PYTHON), "-m", "pip", "install", "--upgrade", "pip"])
        result = run([str(VENV_PYTHON), "-m", "pip", "install", "-e", f"{ROOT}[dev]"])
        if result != 0:
            raise SystemExit("Failed to install KameHouse GRoot dependencies")
        INSTALL_STAMP.write_text("installed\n", encoding="utf-8")


def run_unit_tests() -> int:
    result = run(
        [
            str(VENV_PYTHON),
            "-m",
            "coverage",
            "run",
            "--branch",
            "--source=kamehouse_groot",
            "-m",
            "pytest",
            "src/test/python/unit",
            "-q",
        ]
    )
    if result != 0:
        return result

    result = run([str(VENV_PYTHON), "-m", "coverage", "json", "-o", str(COVERAGE_JSON)])
    if result != 0:
        return result

    return check_coverage()


def check_coverage() -> int:
    totals = json.loads(COVERAGE_JSON.read_text(encoding="utf-8"))["totals"]
    statements = totals["num_statements"]
    covered_lines = totals["covered_lines"]
    branches = totals["num_branches"]
    covered_branches = totals["covered_branches"]

    line_percentage = (covered_lines / statements * 100) if statements else 100.0
    branch_percentage = (covered_branches / branches * 100) if branches else 100.0

    print(
        f"Coverage: lines {line_percentage:.2f}% "
        f"({covered_lines}/{statements}), branches {branch_percentage:.2f}% "
        f"({covered_branches}/{branches})"
    )

    if line_percentage < MIN_LINE_COVERAGE:
        print(f"Line coverage below {MIN_LINE_COVERAGE}%", file=sys.stderr)
        return 1
    if branch_percentage < MIN_BRANCH_COVERAGE:
        print(f"Branch coverage below {MIN_BRANCH_COVERAGE}%", file=sys.stderr)
        return 1
    return 0


def run_integration_tests() -> int:
    env = os.environ.copy()
    env["KAMEHOUSE_GROOT_INTEGRATION"] = "true"
    return run(
        [str(VENV_PYTHON), "-m", "pytest", "src/test/python/integration", "-q"],
        env=env,
    )


def parse_arguments() -> argparse.Namespace:
    parser = argparse.ArgumentParser(description="Run KameHouse GRoot tests")
    parser.add_argument(
        "--integration",
        action="store_true",
        help="run integration tests against a running kamehouse-groot service",
    )
    parser.add_argument(
        "--integration-only",
        action="store_true",
        help="run only the integration tests",
    )
    parser.add_argument("--no-install", action="store_true", help="skip dependency installation")
    parser.add_argument("--reinstall", action="store_true", help="force dependency installation")
    return parser.parse_args()


def main() -> int:
    args = parse_arguments()
    ensure_venv(install=not args.no_install, reinstall=args.reinstall)

    if not args.integration_only:
        result = run_unit_tests()
        if result != 0:
            return result

    if args.integration or args.integration_only:
        return run_integration_tests()

    return 0


if __name__ == "__main__":
    raise SystemExit(main())
