| [Home](/README.md) | [Docs](/docs/README.md) |
---------------------------------------------------------------

*********************

# Snape Module:

This module contains most of my python scripts. Named after the best Slytherin wizard of all, Severus Snape

## Install

- Install with `deploy-kamehouse.sh -m snape` or `install-kamehouse-snape.sh` 
- The deployment creates a shared python virtual environment at `${HOME}/programs/kamehouse-snape/venv` and installs the dependencies declared in `pyproject.toml` for all snape scripts
- Python 3.11 or newer with the `venv` and `ensurepip` modules is required. The installer installs the OS `venv` package when it's missing on Linux
- Reinstall the virtual environment dependencies manually with `install-kamehouse-snape.sh -f`

## Run

- All snape scripts are executed via the shell script `snape.sh` installed with `kamehouse-shell`

- For example `snape.sh test.py -arg1 val1 -arg2 val2`
