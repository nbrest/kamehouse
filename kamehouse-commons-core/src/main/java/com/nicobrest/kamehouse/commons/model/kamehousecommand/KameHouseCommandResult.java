package com.nicobrest.kamehouse.commons.model.kamehousecommand;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nicobrest.kamehouse.commons.utils.JsonUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Represents the result of an executed kamehouse command. This is the same result that is returned
 * by groot's execute api that also executes a kamehouse shell script.
 */
public class KameHouseCommandResult {

  @JsonIgnore
  private static final Map<String, String> COLOR_MAPPINGS = getColorMappings();

  private String command;
  private int exitCode = -1;
  private int pid = -1;
  private String status = null;
  private List<String> standardOutput = new ArrayList<>();
  private List<String> standardOutputHtml = new ArrayList<>();
  private List<String> standardError = new ArrayList<>();
  private List<String> standardErrorHtml = new ArrayList<>();

  public String getCommand() {
    return command;
  }

  public void setCommand(String command) {
    this.command = command;
  }

  public int getExitCode() {
    return exitCode;
  }

  public void setExitCode(int exitCode) {
    this.exitCode = exitCode;
  }

  public int getPid() {
    return pid;
  }

  public void setPid(int pid) {
    this.pid = pid;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public List<String> getStandardOutput() {
    return List.copyOf(standardOutput);
  }

  public void setStandardOutput(List<String> standardOutput) {
    this.standardOutput = List.copyOf(standardOutput);
  }

  public List<String> getStandardOutputHtml() {
    return List.copyOf(standardOutputHtml);
  }

  public void setStandardOutputHtml(List<String> standardOutputHtml) {
    this.standardOutputHtml = List.copyOf(standardOutputHtml);
  }

  public List<String> getStandardError() {
    return List.copyOf(standardError);
  }

  public void setStandardError(List<String> standardError) {
    this.standardError = List.copyOf(standardError);
  }

  public List<String> getStandardErrorHtml() {
    return List.copyOf(standardErrorHtml);
  }

  public void setStandardErrorHtml(List<String> standardErrorHtml) {
    this.standardErrorHtml = List.copyOf(standardErrorHtml);
  }

  /**
   * Initialize the result of a kamehouse command.
   */
  public KameHouseCommandResult() {

  }

  /**
   * Initialize the result of a kamehouse command.
   */
  public KameHouseCommandResult(KameHouseCommand kameHouseCommand) {
    if (kameHouseCommand.hasSensitiveInformation()) {
      setCommand("Command executed has sensitive information");
      return;
    }
    setCommand(kameHouseCommand.getCommand());
  }

  /**
   * Convert the standardOutput and standardError to html and store them in the output lists.
   */
  public void setHtmlOutputs() {
    standardOutput.forEach(line -> {
      standardOutputHtml.add(convertLineToHtml(line));
    });
    standardError.forEach(line -> {
      standardErrorHtml.add(convertLineToHtml(line));
    });
  }

  /**
   * Convert a command line output to html.
   */
  private String convertLineToHtml(String line) {
    if (line == null) {
      return null;
    }
    final String[] htmlOutput = {line};
    COLOR_MAPPINGS.forEach((bashColor, htmlColor) -> {
      htmlOutput[0] = htmlOutput[0].replaceAll(bashColor, htmlColor);
    });
    // Remove the special character added in my bash color mappings
    htmlOutput[0] = htmlOutput[0].replaceAll("", "");
    htmlOutput[0] = htmlOutput[0].replaceAll("\\x1B", "");
    htmlOutput[0] = htmlOutput[0].replaceAll("\\x1b", "");
    htmlOutput[0] = htmlOutput[0].replaceAll("</span>ain]", "[main]");
    return htmlOutput[0];
  }

  /**
   * Get color mappings between bash and html. Mappings updated here also need to be updated in
   * kamehouse.php in groot.
   */
  private static Map<String, String> getColorMappings() {
    Map<String, String> colorMappings = new HashMap<>();
    colorMappings.put("\\[0;30m", "<span style=\"color:black\">");
    colorMappings.put("\\[1;30m", "<span style=\"color:black\">");
    colorMappings.put("\\[0;31m", "<span style=\"color:red\">");
    colorMappings.put("\\[1;31m", "<span style=\"color:red\">");
    colorMappings.put("\\[0;32m", "<span style=\"color:green\">");
    colorMappings.put("\\[00;32m", "<span style=\"color:green\">");
    colorMappings.put("\\[1;32m", "<span style=\"color:green\">");
    // remove these in-the-middle-of green span symbols on build-kamehouse
    colorMappings.put("\\[0;1;32m", "");
    colorMappings.put("\\[0;33m", "<span style=\"color:yellow\">");
    colorMappings.put("\\[1;33m", "<span style=\"color:yellow\">");
    // remove these in-the-middle-of yellow span symbols on build-kamehouse
    colorMappings.put("\\[0;1;33m", "");
    colorMappings.put("\\[0;34m", "<span style=\"color:#3996ff\">");
    colorMappings.put("\\[1;34m", "<span style=\"color:#3996ff\">");
    colorMappings.put("\\[0;35m", "<span style=\"color:purple\">");
    colorMappings.put("\\[1;35m", "<span style=\"color:purple\">");
    colorMappings.put("\\[0;36m", "<span style=\"color:cyan\">");
    colorMappings.put("\\[1;36m", "<span style=\"color:cyan\">");
    colorMappings.put("\\[36m", "<span style=\"color:cyan\">");
    colorMappings.put("\\[0;37m", "<span style=\"color:white\">");
    colorMappings.put("\\[1;37m", "<span style=\"color:white\">");
    colorMappings.put("\\[0;39m", "<span style=\"color:gray\">");
    colorMappings.put("\\[1;39m", "<span style=\"color:gray\">");
    colorMappings.put("\\[1;32;49m", "<span style=\"color:lightgreen\">");
    colorMappings.put("\\[0m", "</span>");
    colorMappings.put("\\[00m", "</span>");
    colorMappings.put("\\[1m", "</span>");
    colorMappings.put("\\[0;1m", "</span>");
    colorMappings.put("\\[m", "</span>");
    return colorMappings;
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder()
        .append(command)
        .append(exitCode)
        .append(pid)
        .append(status)
        .toHashCode();
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj instanceof KameHouseCommandResult other) {
      return new EqualsBuilder()
          .append(command, other.getCommand())
          .append(exitCode, other.getExitCode())
          .append(pid, other.getPid())
          .append(status, other.getStatus())
          .isEquals();
    } else {
      return false;
    }
  }

  @Override
  public String toString() {
    return JsonUtils.toJsonString(this, super.toString());
  }
}
