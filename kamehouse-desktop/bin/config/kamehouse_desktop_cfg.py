from configparser import ConfigParser
from pathlib import Path

class KameHouseDesktopCfg():
    def __init__(self):
        homeDir = str(Path.home())
        configFiles = [homeDir + '/programs/kamehouse-desktop/conf/default-kamehouse-desktop.cfg', homeDir + '/.kamehouse/config/kamehouse-desktop.cfg']
        self.config = ConfigParser()
        self.config.read(configFiles)

    def get(self, propertyCategory, propertyKey):
        return self.config[propertyCategory][propertyKey]

    def getInt(self, propertyCategory, propertyKey):
        return int(self.config[propertyCategory][propertyKey])

    def getBoolean(self, propertyCategory, propertyKey):
        propertyValue = self.config[propertyCategory][propertyKey]
        if propertyValue is None:
          return False
        propertyValue = propertyValue = propertyValue.lower().strip()
        return propertyValue == "true"

kamehouseDesktopCfg = KameHouseDesktopCfg()