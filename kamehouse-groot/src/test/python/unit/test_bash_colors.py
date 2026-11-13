"""Tests for bash color to HTML conversion."""

from __future__ import annotations

from kamehouse_groot.utils.bash_colors import (
    convert_bash_colors_to_html,
    convert_lines_to_html,
)


def test_converts_red_and_close() -> None:
    line = "[0;31mError[0m"
    assert convert_bash_colors_to_html(line) == '<span style="color:red">Error</span>'


def test_converts_green_variants() -> None:
    assert convert_bash_colors_to_html("[0;32mOK[0m") == '<span style="color:green">OK</span>'
    assert convert_bash_colors_to_html("[00;32mOK[0m") == '<span style="color:green">OK</span>'


def test_converts_yellow_and_blue() -> None:
    assert convert_bash_colors_to_html("[1;33mWarn[0m") == '<span style="color:yellow">Warn</span>'
    assert convert_bash_colors_to_html("[0;34mInfo[0m") == '<span style="color:#3996ff">Info</span>'


def test_none_returns_none() -> None:
    assert convert_bash_colors_to_html(None) is None


def test_removes_escape_sequences() -> None:
    assert convert_bash_colors_to_html("\u001b[0;31mError") == '<span style="color:red">Error'


def test_removes_literal_escape_sequences() -> None:
    assert convert_bash_colors_to_html("\\x1B[0;31mError") == '<span style="color:red">Error'
    assert convert_bash_colors_to_html("\\x1b[0;31mError") == '<span style="color:red">Error'


def test_replaces_broken_main_span() -> None:
    assert convert_bash_colors_to_html("</span>ain]") == "[main]"


def test_convert_lines_to_html() -> None:
    lines = ["[0;32mOK[0m", "plain"]
    assert convert_lines_to_html(lines) == ['<span style="color:green">OK</span>', "plain"]
