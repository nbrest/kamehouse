import sys
from pathlib import Path

# Use built-in tomllib for Python 3.11+, fallback to tomli if necessary
if sys.version_info >= (3, 11):
    import tomllib
else:
    import tomli as tomllib

def deep_merge(base_dict, override_dict):
    """Recursively merges override_dict into base_dict."""
    for key, val in override_dict.items():
        if isinstance(val, dict) and key in base_dict and isinstance(base_dict[key], dict):
            deep_merge(base_dict[key], val)
        else:
            base_dict[key] = val
    return base_dict

class KameHouseDesktopCfg():
    def __init__(self):
        home_dir = Path.home()
        default_file = home_dir / 'programs/kamehouse-desktop/conf/default-kamehouse-desktop.toml'
        user_file = home_dir / '.kamehouse/config/kamehouse-desktop.toml'
        
        self.config = {}
        
        # 1. Load defaults first
        if default_file.is_file():
            with open(default_file, "rb") as f:
                self.config = tomllib.load(f)
                
        # 2. Deep merge user overrides on top so missing keys are preserved
        if user_file.is_file():
            with open(user_file, "rb") as f:
                user_config = tomllib.load(f)
                self.config = deep_merge(self.config, user_config)

    def get(self, property_category, property_key):
        return self.config[property_category][property_key]

    def getInt(self, property_category, property_key):
        return int(self.config[property_category][property_key])

    def getFloat(self, property_category, property_key):
        val = self.config[property_category][property_key]
        if val is None:
            return 0.0
        if isinstance(val, (int, float)):
            return float(val)
        return float(str(val).lower().strip())

    def getBoolean(self, property_category, property_key):
        val = self.config[property_category][property_key]
        if val is None:
            return False
        if isinstance(val, bool):
            return val
        return str(val).lower().strip() == "true"

    def getList(self, property_category, property_key):
        """Returns a native Python list (ideal for arrays like random src lists)."""
        val = self.config[property_category][property_key]
        if val is None:
            return []
        if isinstance(val, list):
            return val
        return [item.strip() for item in str(val).split(",")]

kamehouse_desktop_cfg = KameHouseDesktopCfg()