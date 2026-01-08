import click
import re
from pathlib import Path
from loguru import logger

@click.command()
@click.option('--filter', '-f', 'filter_regex', help='Filter snape scrips')
def main(filter_regex):
    listSnape(filter_regex)

def listSnape(filter_regex):
    snape_bin_path = Path.home() /"programs"/"kamehouse-snape"/"bin"
    logger.info("kamehouse snape scripts")
    files = snape_bin_path.rglob('*.py')
    for file in files:
        if file.is_file():
            script = str(file).replace(str(snape_bin_path), "").lstrip("\\/") 
            if filter_regex:
                if re.search(filter_regex, script):
                    print(script)
            else:
                print(script)

if __name__ == "__main__":
    main()
