package com.nicobrest.kamehouse.admin.service;

import com.nicobrest.kamehouse.main.exception.KameHouseBadRequestException;
import com.nicobrest.kamehouse.main.exception.KameHouseException;
import com.nicobrest.kamehouse.main.utils.PropertiesUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Service to execute power management commands.
 *
 * @author nbrest
 */
@Service
public class PowerManagementService {

  private static final Logger logger = LoggerFactory.getLogger(PowerManagementService.class);
  private static final int WOL_PORT = 9;

  /**
   * Wake on lan the specified server. The server should be the base of the the admin.properties
   * [server].mac and [server].broadcast.
   * For example: "media.server"
   */
  public void wakeOnLan(String server) {
    logger.trace("Waking up {}", server);
    String macAddress = PropertiesUtils.getAdminProperty(server + ".mac");
    String broadcastAddress = PropertiesUtils.getAdminProperty(server + ".broadcast");
    if (macAddress == null || broadcastAddress == null) {
      throw new KameHouseBadRequestException("Invalid server specified " + server);
    }
    wakeOnLan(macAddress, broadcastAddress);
  }

  /**
   * Wake on lan the specified MAC address in format FF:FF:FF:FF:FF:FF or FF-FF-FF-FF-FF-FF
   * using the specified broadcast address.
   */
  public void wakeOnLan(String macAddress, String broadcastAddress) {
    try {
      byte[] macAddressBytes = getMacAddressBytes(macAddress);
      byte[] wolPacketBytes = new byte[6 + 16 * macAddressBytes.length];
      for (int i = 0; i < 6; i++) {
        wolPacketBytes[i] = (byte) 0xff;
      }
      for (int i = 6; i < wolPacketBytes.length; i += macAddressBytes.length) {
        System.arraycopy(macAddressBytes, 0, wolPacketBytes, i, macAddressBytes.length);
      }

      InetAddress broadcastInetAddress = InetAddress.getByName(broadcastAddress);
      DatagramPacket wolPacket = new DatagramPacket(wolPacketBytes, wolPacketBytes.length,
          broadcastInetAddress, WOL_PORT);
      DatagramSocket datagramSocket = new DatagramSocket();
      datagramSocket.send(wolPacket);
      datagramSocket.close();
      logger.debug("WOL packet sent to {} on broadcast {}", macAddress, broadcastAddress);
    } catch (IOException e) {
      logger.error("Error sending WOL packet to {}", macAddress, e);
      throw new KameHouseException(e);
    }
  }

  private static byte[] getMacAddressBytes(String macAddress) throws KameHouseException {
    try {
      byte[] macAddressBytes = new byte[6];
      String[] hex = macAddress.split("(\\:|\\-)");
      if (hex.length != 6) {
        throw new KameHouseBadRequestException("Invalid MAC address " + macAddress);
      }
      for (int i = 0; i < 6; i++) {
        macAddressBytes[i] = (byte) Integer.parseInt(hex[i], 16);
      }
      return macAddressBytes;
    } catch (NumberFormatException e) {
      throw new KameHouseBadRequestException("Invalid MAC address " + macAddress);
    }
  }
}
