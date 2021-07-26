package com.nicobrest.kamehouse.cmd.model;

import com.nicobrest.kamehouse.cmd.config.KameHouseCmd;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

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
  private static final List<String> ENCRYPT_OPTIONS = Arrays.asList("-if", "-of");

  private CommandLine commandLine;
  private String[] args;
  private Operation operation;

  static {
    ROOT_OPTIONS.add(new Option("h", "help", false, "Show help"));
    ROOT_OPTIONS.add(new Option("o", "operation", true,
        "Operation to execute: " + VALID_OPERATIONS));
    ROOT_OPTIONS.stream().forEach(option -> ROOT_OPTIONS_GROUP.addOption(option));
    ROOT_OPTIONS_GROUP.setRequired(true);
    ALL_OPTIONS.addOptionGroup(ROOT_OPTIONS_GROUP);

    ALL_OPTIONS.addOption(new Option("if", "input-file", true, "Input file"));
    ALL_OPTIONS.addOption(new Option("of", "output-file", true, "Output file"));
  }

  public CmdArgumentHandler(String[] args) {
    this.args = args.clone();
    parse();
  }

  /**
   * Parse all command line arguments.
   */
  private void parse() {

    try {
      commandLine = COMMAND_LINE_PARSER.parse(ALL_OPTIONS, args);
      if (commandLine.hasOption("h")) {
        help();
      }

      setOperation(commandLine);
      switch (operation) {
        case ENCRYPT:
          parseEncryptOperation();
          break;
        default:
          logger.error("Unhandled operation " + operation);
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
  @SuppressFBWarnings(value = "DM_EXIT", justification = "It's ok to exit here")
  public void help() {
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp(KameHouseCmd.class.getSimpleName(), ALL_OPTIONS);
    System.exit(0);
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
   * Parse the arguments for the encrypt operation.
   */
  private void parseEncryptOperation() {
    ENCRYPT_OPTIONS.stream().forEach(option -> {
      if (!hasArgument(option)) {
        logger.error("Argument {} is missing", option);
        help();
      }
    });
  }
}
