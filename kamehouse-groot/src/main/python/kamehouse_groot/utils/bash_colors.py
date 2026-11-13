"""Convert bash ANSI color codes to HTML spans.

The mappings are kept in sync with KameHouseCommandResult.java and the groot
bash colors utility.
"""

from __future__ import annotations

SPAN_CLOSE = "</span>"

COLOR_MAPPINGS: dict[str, str] = {
    "[0;30m": '<span style="color:black">',
    "[1;30m": '<span style="color:black">',
    "[0;31m": '<span style="color:red">',
    "[1;31m": '<span style="color:red">',
    "[0;32m": '<span style="color:green">',
    "[00;32m": '<span style="color:green">',
    "[1;32m": '<span style="color:green">',
    "[0;1;32m": "",
    "[0;33m": '<span style="color:yellow">',
    "[0;1;33m": "",
    "[1;33m": '<span style="color:yellow">',
    "[0;34m": '<span style="color:#3996ff">',
    "[1;34m": '<span style="color:#3996ff">',
    "[0;35m": '<span style="color:purple">',
    "[1;35m": '<span style="color:purple">',
    "[0;36m": '<span style="color:cyan">',
    "[1;36m": '<span style="color:cyan">',
    "[36m": '<span style="color:cyan">',
    "[0;37m": '<span style="color:white">',
    "[1;37m": '<span style="color:white">',
    "[0;39m": '<span style="color:gray">',
    "[1;39m": '<span style="color:gray">',
    "[1;32;49m": '<span style="color:lightgreen">',
    "[0m": SPAN_CLOSE,
    "[00m": SPAN_CLOSE,
    "[1m": SPAN_CLOSE,
    "[0;1m": SPAN_CLOSE,
    "[m": SPAN_CLOSE,
}


def convert_bash_colors_to_html(line: str | None) -> str | None:
    """Replace bash color codes in a line with the equivalent HTML spans."""
    if line is None:
        return None

    html_line = line
    for bash_color, html_color in COLOR_MAPPINGS.items():
        html_line = html_line.replace(bash_color, html_color)

    html_line = html_line.replace("\u001b", "")
    html_line = html_line.replace("\\x1B", "")
    html_line = html_line.replace("\\x1b", "")
    html_line = html_line.replace("</span>ain]", "[main]")
    return html_line


def convert_lines_to_html(lines: list[str]) -> list[str]:
    """Convert a list of bash output lines to HTML lines."""
    return [convert_bash_colors_to_html(line) for line in lines]
