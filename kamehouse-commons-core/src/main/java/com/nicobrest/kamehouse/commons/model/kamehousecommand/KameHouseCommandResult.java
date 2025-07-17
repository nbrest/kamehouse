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
  @JsonIgnore
  private static final String COL_BLACK = "<span style=\"color:black\">";
  @JsonIgnore
  private static final String COL_RED = "<span style=\"color:red\">";
  @JsonIgnore
  private static final String COL_GREEN = "<span style=\"color:green\">";
  @JsonIgnore
  private static final String COL_YELLOW = "<span style=\"color:yellow\">";
  @JsonIgnore
  private static final String COL_3996FF = "<span style=\"color:#3996ff\">";
  @JsonIgnore
  private static final String COL_PURPLE = "<span style=\"color:purple\">";
  @JsonIgnore
  private static final String COL_CYAN = "<span style=\"color:cyan\">";
  @JsonIgnore
  private static final String COL_WHITE = "<span style=\"color:white\">";
  @JsonIgnore
  private static final String COL_GRAY = "<span style=\"color:gray\">";
  @JsonIgnore
  private static final String COL_LIGHT_GREEN = "<span style=\"color:lightgreen\">";
  @JsonIgnore
  private static final String SPAN_CLOSE = "</span>";

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
   * Convert the standardOutput and standardError to html and store them in the output lists.
   */
  public void setHtmlOutputs() {
    standardOutput.forEach(line -> standardOutputHtml.add(convertLineToHtml(line)));
    standardError.forEach(line -> standardErrorHtml.add(convertLineToHtml(line)));
  }

  /**
   * Convert a command line output to html.
   */
  private String convertLineToHtml(String line) {
    if (line == null) {
      return null;
    }
    final String[] htmlOutput = {line};
    COLOR_MAPPINGS.forEach(
        (bashColor, htmlColor) -> htmlOutput[0] = htmlOutput[0].replaceAll(bashColor, htmlColor));
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
    colorMappings.put("\\[0;30m", COL_BLACK);
    colorMappings.put("\\[1;30m", COL_BLACK);
    colorMappings.put("\\[0;31m", COL_RED);
    colorMappings.put("\\[1;31m", COL_RED);
    colorMappings.put("\\[0;32m", COL_GREEN);
    colorMappings.put("\\[00;32m", COL_GREEN);
    colorMappings.put("\\[1;32m", COL_GREEN);
    // remove these in-the-middle-of green span symbols on build-kamehouse
    colorMappings.put("\\[0;1;32m", "");
    colorMappings.put("\\[0;33m", COL_YELLOW);
    colorMappings.put("\\[1;33m", COL_YELLOW);
    // remove these in-the-middle-of yellow span symbols on build-kamehouse
    colorMappings.put("\\[0;1;33m", "");
    colorMappings.put("\\[0;34m", COL_3996FF);
    colorMappings.put("\\[1;34m", COL_3996FF);
    colorMappings.put("\\[0;35m", COL_PURPLE);
    colorMappings.put("\\[1;35m", COL_PURPLE);
    colorMappings.put("\\[0;36m", COL_CYAN);
    colorMappings.put("\\[1;36m", COL_CYAN);
    colorMappings.put("\\[36m", COL_CYAN);
    colorMappings.put("\\[0;37m", COL_WHITE);
    colorMappings.put("\\[1;37m", COL_WHITE);
    colorMappings.put("\\[0;39m", COL_GRAY);
    colorMappings.put("\\[1;39m", COL_GRAY);
    colorMappings.put("\\[1;32;49m", COL_LIGHT_GREEN);
    colorMappings.put("\\[0m", SPAN_CLOSE);
    colorMappings.put("\\[00m", SPAN_CLOSE);
    colorMappings.put("\\[1m", SPAN_CLOSE);
    colorMappings.put("\\[0;1m", SPAN_CLOSE);
    colorMappings.put("\\[m", SPAN_CLOSE);
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
