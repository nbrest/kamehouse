package com.nicobrest.kamehouse.cmd.model;

import com.nicobrest.kamehouse.commons.utils.ProcessUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles parsing of command line arguments in kamehouse-cmd.
 *
 * @author nbrest
 */
public class CmdArgumentHandler {

  private static final Logger logger = LoggerFactory.getLogger(CmdArgumentHandler.class);

  private static final List<Option> ROOT_OPTIONS = new ArrayList<>();
  private static final Options ALL_OPTIONS = new Options();
  private static final OptionGroup ROOT_OPTIONS_GROUP = new OptionGroup();
  private static final CommandLineParser COMMAND_LINE_PARSER = new DefaultParser();
  private static final String VALID_OPERATIONS =
      Arrays.asList(Operation.values()).toString().toLowerCase(Locale.getDefault());
  private static final List<String> DECRYPT_OPTIONS = Arrays.asList("-if", "-of");
  private static final List<String> ENCRYPT_OPTIONS = Arrays.asList("-if", "-of");
  private static final List<String> JVNCSENDER_OPTIONS =
      Arrays.asList("-host", "-password", "-port");
  private static final List<String> WOL_OPTIONS = Arrays.asList("-mac", "-broadcast");
  private static final String MOUSE_CLICK = "mouseClick";

  private CommandLine commandLine;
  private Operation operation;

  static {
    ROOT_OPTIONS.add(new Option("h", "help", false, "Show help"));
    ROOT_OPTIONS.add(
        new Option("o", "operation", true, "Operation to execute: " + VALID_OPERATIONS));
    ROOT_OPTIONS.stream().forEach(ROOT_OPTIONS_GROUP::addOption);
    ROOT_OPTIONS_GROUP.setRequired(true);
    ALL_OPTIONS.addOptionGroup(ROOT_OPTIONS_GROUP);

    ALL_OPTIONS.addOption(new Option("v", "verbose", false, "Verbose mode"));
    ALL_OPTIONS.addOption(new Option("if", "input-file", true, "Input file"));
    ALL_OPTIONS.addOption(
        new Option("of", "output-file", true, "KameHouseCommandResult file"));
    ALL_OPTIONS.addOption(new Option("host", "host", true, "Host"));
    ALL_OPTIONS.addOption(new Option("password", "password", true, "Password"));
    ALL_OPTIONS.addOption(new Option("port", "port", true, "Port"));
    ALL_OPTIONS.addOption(new Option("text", "text", true, "Text"));
    ALL_OPTIONS.addOption(
        new Option(MOUSE_CLICK, MOUSE_CLICK, true, "[LEFT|RIGHT],positionX,positionY,clickCount"));
    ALL_OPTIONS.addOption(new Option("mac", "mac", true, "Mac Address"));
    ALL_OPTIONS.addOption(new Option("broadcast", "broadcast", true, "Broadcast Address"));
  }

  public CmdArgumentHandler(String[] args) {
    logger.info("Parsing command line arguments");
    parse(args);
  }

  /**
   * Parse all command line arguments.
   */
  private void parse(String[] args) {

    try {
      commandLine = COMMAND_LINE_PARSER.parse(ALL_OPTIONS, args);
      if (commandLine.hasOption("h")) {
        help();
        return;
      }

      setOperation(commandLine);
      if (operation == null) {
        throw new ParseException("Operation not set");
      }
      switch (operation) {
        case DECRYPT:
          parseDecryptOperation();
          break;
        case ENCRYPT:
          parseEncryptOperation();
          break;
        case JVNCSENDER:
          parseJvncSenderOperation();
          break;
        case WOL:
          parseWolOperation();
          break;
        default:
          logger.error("Unhandled operation {},", operation);
          help();
          break;
      }
    } catch (ParseException e) {
      logger.error("Failed to parse command line arguments.", e);
      help();
    }
  }

  /**
   * Prints help information on the console and exits the program.
   */
  public void help() {
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp("KameHouseCmd", ALL_OPTIONS);
    ProcessUtils.exitProcess(1);
  }

  /**
   * Returns the operation to execute.
   */
  public Operation getOperation() {
    return operation;
  }

  /**
   * Returns the value of a command line parameter that requires an argument value.
   */
  public String getArgument(String arg) {
    return commandLine.getOptionValue(arg);
  }

  /**
   * Checks if the specified command line argument was passed when executing the application.
   */
  public boolean hasArgument(String arg) {
    return commandLine.hasOption(arg);
  }

  /**
   * Set the operation from the command line parameters.
   */
  private void setOperation(CommandLine commandLine) {
    String operationArgument = commandLine.getOptionValue("o").toUpperCase(Locale.getDefault());
    try {
      operation = Operation.valueOf(operationArgument);
    } catch (IllegalArgumentException e) {
      logger.error("Invalid operation {}. Valid values: {}", operationArgument, VALID_OPERATIONS);
      help();
    }
  }

  /**
   * Parse the arguments for the decrypt operation.
   */
  private void parseDecryptOperation() {
    parseOperation(DECRYPT_OPTIONS);
  }

  /**
   * Parse the arguments for the encrypt operation.
   */
  private void parseEncryptOperation() {
    parseOperation(ENCRYPT_OPTIONS);
  }

  /**
   * Parse the arguments for the jvncsender operation.
   */
  private void parseJvncSenderOperation() {
    parseOperation(JVNCSENDER_OPTIONS);
    if (!hasArgument("text") && !hasArgument(MOUSE_CLICK)) {
      logger.error("Either text or mouseClick parameters need to be set");
      help();
    }
    if (hasArgument(MOUSE_CLICK)) {
      String mouseClick = getArgument(MOUSE_CLICK);
      if (!mouseClick.matches("(LEFT|RIGHT),\\d+,\\d+,\\d+")) {
        logger.error("Invalid mouseClick option value: {}", mouseClick);
        help();
      }
    }
  }

  /**
   * Parse the arguments for the wol operation.
   */
  private void parseWolOperation() {
    parseOperation(WOL_OPTIONS);
  }

  /**
   * Parse operation options.
   */
  private void parseOperation(List<String> operationOptions) {
    operationOptions.stream()
        .forEach(
            option -> {
              if (!hasArgument(option)) {
                logger.error("Argument {} is missing", option);
                help();
              }
            });
  }
}
