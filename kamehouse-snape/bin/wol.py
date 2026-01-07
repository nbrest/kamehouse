import socket
import struct
import sys
import click
from loguru import logger

@click.command()
@click.option('--mac-address', required=True, help='MAC address. ej: aa:bb:cc:dd:ee:ff')
@click.option('--broadcast-address', required=True, help='Broadcast address. ej: 192.168.0.255')
def main(mac_address, broadcast_address):
    logger.info("Waking up: " + mac_address + " using broadcast: " + broadcast_address)
    formatted_mac = formatMac(mac_address)
    validateMac(formatted_mac)
    sendWol(formatted_mac, broadcast_address)
    logger.info("Magic packet sent to wake up " + mac_address + " to " + broadcast_address)

def formatMac(macAddress):
    return macAddress.replace(':', '').replace('-', '')

def validateMac(macAddress):
    if len(macAddress) != 12:
        logger.error("Invalid formatted MAC address: " + macAddress)
        sys.exit(1)

def sendWol(macAddress, broadcastAddress):
    data = bytes.fromhex('FFFFFFFFFFFF' + macAddress * 16)
    with socket.socket(socket.AF_INET, socket.SOCK_DGRAM) as sock:
        sock.setsockopt(socket.SOL_SOCKET, socket.SO_BROADCAST, 1)
        sock.sendto(data, (broadcastAddress, 9))

if __name__ == "__main__":
    main()
