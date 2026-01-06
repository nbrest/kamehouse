import socket
import struct
import sys
from loguru import logger

# 1st arg: mac address: aa:bb:cc:dd:ee:ff
# 2nd arg: broadcast address: 192.168.x.255

def main():
    sendWol(sys.argv[1], sys.argv[2])

def sendWol(macAddress, broadcastAddress):
    logger.info("Waking up: " + macAddress + " using broadcast: " + broadcastAddress)
    plainMac = macAddress.replace(':', '').replace('-', '')    
    if len(plainMac) != 12:
        logger.error("Invalid MAC address format: " + macAddress)
        sys.exit(1)
    data = bytes.fromhex('FFFFFFFFFFFF' + plainMac * 16)
    with socket.socket(socket.AF_INET, socket.SOCK_DGRAM) as sock:
        sock.setsockopt(socket.SOL_SOCKET, socket.SO_BROADCAST, 1)
        sock.sendto(data, (broadcastAddress, 9))
        logger.info("Magic packet sent to wake up " + macAddress + " to " + broadcastAddress)

if __name__ == "__main__":
    main()
