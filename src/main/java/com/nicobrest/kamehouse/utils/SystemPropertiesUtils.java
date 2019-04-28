package com.nicobrest.kamehouse.utils;

import java.util.Locale;

public class SystemPropertiesUtils {

  public static final boolean IS_WINDOWS_HOST = isWindowsHost();

  public static boolean isWindowsHost() {
    return System.getProperty("os.name").toLowerCase(Locale.getDefault()).startsWith("windows");
  }

  // This returns the home of the user running the application server.
  public static String getUserHome() {
    return System.getProperty("user.home");
  }
}
