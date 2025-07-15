package com.nicobrest.kamehouse.commons.model.kamehousecommand;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * KameHouseCommandResult test
 *
 * @author nbrest
 */
class KameHouseCommandResultTest {

  private static List<String> standardOutput = getStandardOutput();
  private static List<String> standardOutputHtml = getStandardOutputHtml();

  /**
   * Test converting the standard output to html.
   */
  @Test
  void setHtmlOutputsTest() {
    KameHouseCommandResult result = new KameHouseCommandResult();
    result.setStandardOutput(standardOutput);
    result.setHtmlOutputs();
    assertEquals(standardOutputHtml, result.getStandardOutputHtml());
    assertEquals(new ArrayList<>(), result.getStandardErrorHtml());
  }

  /**
   * Get standard output for command.
   */
  private static List<String> getStandardOutput() {
    return List.of(
        "\u001b[1;36m2025-03-08 11:09:59\u001b[0;39m - [\u001b[1;34mINFO\u001b[0;39m] - \u001b[0;36mexec-script.sh\u001b[0;39m - \u001b[1;32m\u001b[0;33mStarted executing script (masked args)\u001b[0;39m",
        "\u001b[1;36m2025-03-08 11:09:59\u001b[0;39m - [\u001b[1;34mINFO\u001b[0;39m] - \u001b[0;36mexec-script.sh\u001b[0;39m - \u001b[1;32mValidating command line arguments\u001b[0;39m",
        "\u001b[1;36m2025-03-08 11:09:59\u001b[0;39m - [\u001b[1;34mINFO\u001b[0;39m] - \u001b[0;36mexec-script.sh\u001b[0;39m - \u001b[1;32mExecuting script \u001b[1;35m'/home/goku/programs/kamehouse-shell/bin/kamehouse/status-kamehouse.sh'\u001b[1;32m\u001b[0;39m",
        "\u001b[1;36m2025-03-08 11:10:00\u001b[0;39m - [\u001b[1;34mINFO\u001b[0;39m] - \u001b[0;36mstatus-kamehouse.sh\u001b[0;39m - \u001b[1;32m\u001b[0;33mStarted executing script without args\u001b[0;39m",
        "/:running:0:ROOT",
        "/docs:running:0:docs",
        "/examples:running:0:examples",
        "/host-manager:running:0:host-manager",
        "/kame-house-admin:running:0:kame-house-admin",
        "/kame-house-media:running:0:kame-house-media",
        "/kame-house-tennisworld:running:0:kame-house-tennisworld",
        "/kame-house-testmodule:running:0:kame-house-testmodule",
        "/kame-house-vlcrc:running:0:kame-house-vlcrc",
        "/kame-house:running:0:kame-house",
        "/manager:running:0:manager",
        "OK - Listed applications for virtual host [localhost]",
        "\u001b[1;36m2025-03-08 11:10:00\u001b[0;39m - [\u001b[1;34mINFO\u001b[0;39m] - \u001b[0;36mstatus-kamehouse.sh\u001b[0;39m - \u001b[1;32m\u001b[0;33mFinished executing script without args \u001b[1;34mstatus: 0\u001b[0;33m and \u001b[1;34mruntime: 0m\u001b[0;33m (1s)\u001b[0;39m",
        "\u001b[1;36m2025-03-08 11:10:00\u001b[0;39m - [\u001b[1;34mINFO\u001b[0;39m] - \u001b[0;36mexec-script.sh\u001b[0;39m - \u001b[1;32m\u001b[0;33mFinished executing script (masked args) \u001b[1;34mstatus: 0\u001b[0;33m and \u001b[1;34mruntime: 0m\u001b[0;33m (2s)\u001b[0;39m",
        ""
    );
  }

  /**
   * Get expected standard output html for command.
   */
  private static List<String> getStandardOutputHtml() {
    return List.of(
        "<span style=\"color:cyan\">2025-03-08 11:09:59<span style=\"color:gray\"> - [<span style=\"color:#3996ff\">INFO<span style=\"color:gray\">] - <span style=\"color:cyan\">exec-script.sh<span style=\"color:gray\"> - <span style=\"color:green\"><span style=\"color:yellow\">Started executing script (masked args)<span style=\"color:gray\">",
        "<span style=\"color:cyan\">2025-03-08 11:09:59<span style=\"color:gray\"> - [<span style=\"color:#3996ff\">INFO<span style=\"color:gray\">] - <span style=\"color:cyan\">exec-script.sh<span style=\"color:gray\"> - <span style=\"color:green\">Validating command line arguments<span style=\"color:gray\">",
        "<span style=\"color:cyan\">2025-03-08 11:09:59<span style=\"color:gray\"> - [<span style=\"color:#3996ff\">INFO<span style=\"color:gray\">] - <span style=\"color:cyan\">exec-script.sh<span style=\"color:gray\"> - <span style=\"color:green\">Executing script <span style=\"color:purple\">'/home/goku/programs/kamehouse-shell/bin/kamehouse/status-kamehouse.sh'<span style=\"color:green\"><span style=\"color:gray\">",
        "<span style=\"color:cyan\">2025-03-08 11:10:00<span style=\"color:gray\"> - [<span style=\"color:#3996ff\">INFO<span style=\"color:gray\">] - <span style=\"color:cyan\">status-kamehouse.sh<span style=\"color:gray\"> - <span style=\"color:green\"><span style=\"color:yellow\">Started executing script without args<span style=\"color:gray\">",
        "/:running:0:ROOT",
        "/docs:running:0:docs",
        "/examples:running:0:examples",
        "/host-manager:running:0:host-manager",
        "/kame-house-admin:running:0:kame-house-admin",
        "/kame-house-media:running:0:kame-house-media",
        "/kame-house-tennisworld:running:0:kame-house-tennisworld",
        "/kame-house-testmodule:running:0:kame-house-testmodule",
        "/kame-house-vlcrc:running:0:kame-house-vlcrc",
        "/kame-house:running:0:kame-house",
        "/manager:running:0:manager",
        "OK - Listed applications for virtual host [localhost]",
        "<span style=\"color:cyan\">2025-03-08 11:10:00<span style=\"color:gray\"> - [<span style=\"color:#3996ff\">INFO<span style=\"color:gray\">] - <span style=\"color:cyan\">status-kamehouse.sh<span style=\"color:gray\"> - <span style=\"color:green\"><span style=\"color:yellow\">Finished executing script without args <span style=\"color:#3996ff\">status: 0<span style=\"color:yellow\"> and <span style=\"color:#3996ff\">runtime: 0m<span style=\"color:yellow\"> (1s)<span style=\"color:gray\">",
        "<span style=\"color:cyan\">2025-03-08 11:10:00<span style=\"color:gray\"> - [<span style=\"color:#3996ff\">INFO<span style=\"color:gray\">] - <span style=\"color:cyan\">exec-script.sh<span style=\"color:gray\"> - <span style=\"color:green\"><span style=\"color:yellow\">Finished executing script (masked args) <span style=\"color:#3996ff\">status: 0<span style=\"color:yellow\"> and <span style=\"color:#3996ff\">runtime: 0m<span style=\"color:yellow\"> (2s)<span style=\"color:gray\">",
        ""
    );
  }
}
