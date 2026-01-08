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

def formatMac(mac_address):
    return mac_address.replace(':', '').replace('-', '')

def validateMac(mac_address):
    if len(mac_address) != 12:
        logger.error("Invalid formatted MAC address: " + mac_address)
        sys.exit(1)

def sendWol(mac_address, broadcast_address):
    try:
        data = bytes.fromhex('FFFFFFFFFFFF' + mac_address * 16)
        with socket.socket(socket.AF_INET, socket.SOCK_DGRAM) as sock:
            sock.setsockopt(socket.SOL_SOCKET, socket.SO_BROADCAST, 1)
            sock.sendto(data, (broadcast_address, 9))
    except Exception as error:
        logger.error("Error sending magic packet to " + broadcast_address + ". Error: " + str(error))
        sys.exit(1)

if __name__ == "__main__":
    main()
