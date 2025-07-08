from configparser import ConfigParser
from pathlib import Path

class KameHouseDesktopCfg(ConfigParser):
    def __init__(self):
        super().__init__()
        homeDir = str(Path.home())
        configFiles = [homeDir + '/programs/kamehouse-desktop/conf/default-kamehouse-desktop.cfg', homeDir + '/.kamehouse/config/kamehouse-desktop.cfg']
        self.read(configFiles)
        
kamehouseDesktopCfg = KameHouseDesktopCfg()