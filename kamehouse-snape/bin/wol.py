import socket
import struct
import sys
import click
from loguru import logger

@click.command()
@click.option('--mac-address', required=True, help='MAC address. ej: aa:bb:cc:dd:ee:ff')
@click.option('--broadcast-address', required=True, help='Broadcast address. ej: 192.168.0.255')
def main(mac_address, broadcast_address):
    sendWol(mac_address, broadcast_address)

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
