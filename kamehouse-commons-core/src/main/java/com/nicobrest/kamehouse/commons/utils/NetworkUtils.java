package com.nicobrest.kamehouse.commons.utils;

import com.nicobrest.kamehouse.commons.exception.KameHouseBadRequestException;
import com.nicobrest.kamehouse.commons.exception.KameHouseException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class to execute some network commands such as sending wol packets.
 *
 * @author nbrest
 */
public class NetworkUtils {

  private static final int WOL_PORT = 9;
  private static final Logger logger = LoggerFactory.getLogger(NetworkUtils.class);

  private NetworkUtils() {
    throw new IllegalStateException("Utility class");
  }

  /**
   * Wake on lan the specified MAC address in format FF:FF:FF:FF:FF:FF or FF-FF-FF-FF-FF-FF using
   * the specified broadcast address.
   */
  public static void wakeOnLan(String macAddress, String broadcastAddress) {
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
      DatagramPacket wolPacket =
          new DatagramPacket(wolPacketBytes, wolPacketBytes.length, broadcastInetAddress, WOL_PORT);
      DatagramSocket datagramSocket = new DatagramSocket();
      datagramSocket.send(wolPacket);
      datagramSocket.close();
      logger.debug("WOL packet sent to {} on broadcast {}", macAddress, broadcastAddress);
    } catch (IOException e) {
      handleWakeOnLanIoException(e, macAddress);
    }
  }

  /**
   * Handle IOException.
   */
  private static void handleWakeOnLanIoException(IOException exception, String macAddress) {
    logger.error("Error sending WOL packet to {}. Message: {}", macAddress, exception.getMessage());
    throw new KameHouseException(exception);
  }

  /**
   * Get the mac address as a byte array.
   */
  private static byte[] getMacAddressBytes(String macAddress) {
    try {
      byte[] macAddressBytes = new byte[6];
      String[] hex = macAddress.split("[:-]");
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
