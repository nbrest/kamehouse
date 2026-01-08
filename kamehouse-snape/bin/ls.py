from pathlib import Path
from loguru import logger

def main():
    listSnape()

def listSnape():
    snapeBinPath = Path.home() /"programs"/"kamehouse-snape"/"bin"
    logger.info("snape scripts")
    files = snapeBinPath.rglob('*.py')
    for file in files:
        if file.is_file():
            print(str(file).replace(str(snapeBinPath), "").lstrip("\\/")) 

if __name__ == "__main__":
    main()
