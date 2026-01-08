from configparser import ConfigParser
from pathlib import Path

class KameHouseDesktopCfg():
    def __init__(self):
        home_dir = str(Path.home())
        config_files = [home_dir + '/programs/kamehouse-desktop/conf/default-kamehouse-desktop.cfg', home_dir + '/.kamehouse/config/kamehouse-desktop.cfg']
        self.config = ConfigParser()
        self.config.read(config_files)

    def get(self, property_category, property_key):
        return self.config[property_category][property_key]

    def getInt(self, property_category, property_key):
        return int(self.config[property_category][property_key])

    def getFloat(self, property_category, property_key):
        property_value = self.config[property_category][property_key]
        if property_value is None:
          return 0.0
        property_value = property_value = property_value.lower().strip()
        return float(property_value)

    def getBoolean(self, property_category, property_key):
        property_value = self.config[property_category][property_key]
        if property_value is None:
          return False
        property_value = property_value = property_value.lower().strip()
        return property_value == "true"

kamehouse_desktop_cfg = KameHouseDesktopCfg()