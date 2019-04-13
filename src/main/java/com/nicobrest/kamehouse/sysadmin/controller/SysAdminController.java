package com.nicobrest.kamehouse.sysadmin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

@Controller
@RequestMapping(value = "/api/v1/sysadmin")
public class SysAdminController {
/*
  public static void main(String[] args) throws IOException, InterruptedException {

    System.out.println("Test executing command start");

    boolean isWindowsOpSys = System.getProperty("os.name").toLowerCase(Locale.getDefault()).startsWith("windows");
    ProcessBuilder processBuilder = new ProcessBuilder();
    if (isWindowsOpSys) {
      processBuilder.command("cmd.exe", "/c", "dir");
    } else {
      processBuilder.command("sh", "-c", "ls");
    }
    processBuilder.directory(new File(System.getProperty("user.home")));
    Process process = processBuilder.start();
    StreamGobbler streamGobbler = new StreamGobbler(process.getInputStream(), System.out::println);
    Executors.newSingleThreadExecutor().submit(streamGobbler);
    int exitCode = process.waitFor();
    assert exitCode == 0;

    System.out.println("Test executing command end");
  }

  private static class StreamGobbler implements Runnable {
    private InputStream inputStream;
    private Consumer<String> consumer;

    public StreamGobbler(InputStream inputStream, Consumer<String> consumer) {
      this.inputStream = inputStream;
      this.consumer = consumer;
    }

    @Override
    public void run() {
      new BufferedReader(new InputStreamReader(inputStream)).lines().forEach(consumer);
    }
  }
  */
}
