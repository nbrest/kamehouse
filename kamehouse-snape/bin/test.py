import sys

from config.kamehouse_snape_cfg import kamehouse_snape_cfg

print("snape: mada mada dane. args:")
print(sys.argv[1:])

value = kamehouse_snape_cfg.get('test', 'test_key')
print("config test: test_key:" + value)

