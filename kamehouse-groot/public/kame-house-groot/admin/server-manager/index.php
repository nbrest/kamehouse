<?php require_once("../../api/v1/auth/authorize-admin-page.php") ?>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width">
<meta name="author" content="nbrest">
<meta name="description" content="kame-house application">
<meta name="keywords" content="kame-house nicobrest nbrest">
<meta name="mobile-web-app-capable" content="yes">

<title>GRoot - Server Manager</title>

<link rel="shortcut icon" href="/kame-house-groot/favicon.ico" type="image/x-icon" />
<script src="/cordova.js"></script>
<script src="/kame-house/lib/js/jquery.js"></script>
<script src="/kame-house/kamehouse/js/kamehouse.js" id="kamehouse-data" data-authorized-roles="ROLE_KAMISAMA"></script>
<script src="/kame-house-groot/kamehouse-groot/js/kamehouse-groot.js"></script>
<link rel="stylesheet" href="/kame-house/lib/css/bootstrap.min.css" />
<link rel="stylesheet" href="/kame-house/kamehouse/css/kamehouse.css" />
<link rel="stylesheet" href="/kame-house-groot/kamehouse-groot/css/kamehouse-groot.css" />
<link rel="stylesheet" href="/kame-house-groot/css/admin/groot-server-manager.css" />
</head>
<body>
  <div class="banner-wrapper">
  <div id="banner" class="fade-in-out-15s banner-dbz-messi-maradona">
    <div class="default-layout banner-text">
      <h1>Server Manager</h1>
      <div id="banner-server-name"></div>
    </div>
  </div>  
  </div>
  <div id="groot-menu-wrapper"></div>
  <div class="server-manager-tabs">
  <div class="default-layout">
    <table class="table-kh server-manager-tabs-table">
      <caption class="hidden-kh">Server Manager Tabs</caption>
      <thead class="hidden-kh"><tr><th>Header</th></tr></thead>
      <tbody>
        <tr>
          <td  id="tab-deployment-link" class="tab-kh-link"
            onclick="kameHouse.util.tab.openTab('tab-deployment', 'kh-groot-server-manager')">Deployment</td>  
          <td id="tab-git-link" class="tab-kh-link"
            onclick="kameHouse.util.tab.openTab('tab-git', 'kh-groot-server-manager')">Git</td>     
          <td id="tab-media-link" class="tab-kh-link"
            onclick="kameHouse.util.tab.openTab('tab-media', 'kh-groot-server-manager')">Media</td>  
          <td id="tab-power-link" class="tab-kh-link"
            onclick="kameHouse.util.tab.openTab('tab-power', 'kh-groot-server-manager')">Power</td>  
          <td id="tab-tail-log-link" class="tab-kh-link"
            onclick="kameHouse.util.tab.openTab('tab-tail-log', 'kh-groot-server-manager')">Tail Log</td>
        </tr>
      </tbody>
    </table>
  </div>
  </div>

    <div id="tab-deployment" class="default-layout tab-content-kh p-7-d-kh">

      <br>
      <h4 class="h4-kh txt-l-d-kh txt-c-m-kh">Deployment</h4>
      <p class="default-layout tomcat-description">Manage all the <span class="highlight">Kame</span><span class="bold-kh">House</span> modules installed in the current server. <span class="bold-kh">Login to kame-house to get the current build version and date of the tomcat modules</span>.</p> 
      <p class="default-layout tomcat-description">You can also deploy to all servers using the cloud buttons. Deploying all servers also deploys the non-tomcat modules. As well as check the status of the current tomcat process and start and stop the process when required.</p>
      <span class="bold-kh">Deploy all modules: </span>
      <img class="img-btn-kh m-10-d-r-kh" onclick="kameHouse.extension.deploymentManager.deployAllModules()" 
        src="/kame-house/img/other/rocket-green.png" alt="Deploy All Modules" title="Deploy All Modules"/>
      <img class="img-btn-kh" onclick="kameHouse.extension.deploymentManager.deployAllModulesAllServers()" 
        src="/kame-house/img/other/cloud-up-down-green.png" alt="Deploy All Modules - All Servers" title="Deploy All Modules - All Servers"/>

      <img class="img-btn-kh m-5-d-kh m-5-d-kh fl-r-d-kh" onclick="kameHouse.extension.deploymentManager.refreshServerView()"
        src="/kame-house/img/other/sync-btn-info.png" alt="Refresh" title="Refresh"/>

      <table id="mst-admin" 
        class="table-kh">
        <tr class="table-kh-header">
          <td>module</td>
          <td class="tomcat-modules-table-path">path</td>
          <td>status</td>
          <td class="tomcat-modules-table-build-version">build version</td>
          <td class="tomcat-modules-table-build-date">build date</td>
          <td class="tomcat-modules-table-controls">controls</td>
          <td class="tomcat-modules-table-deployment">deployment</td>
        </tr>
        <tr>
          <td><div id="mst-admin-header-val">admin</div></td>
          <td>/kame-house-admin</td>
          <td id="mst-admin-status-val"><img class="img-tomcat-manager-status" src="/kame-house/img/other/ball-blue.png" alt="status" title="status"/></td>
          <td id="mst-admin-build-version-val">N/A</td>
          <td id="mst-admin-build-date-val">N/A</td>
          <td>
            <img class="img-btn-kh m-7-d-r-kh" onclick="kameHouse.extension.deploymentManager.startModule('admin')" 
              src="/kame-house/img/mplayer/play-green.png" alt="Start" title="Start"/>
            <img class="img-btn-kh" onclick="kameHouse.extension.deploymentManager.stopModule('admin')" 
              src="/kame-house/img/mplayer/stop.png" alt="Stop" title="Stop"/>
          </td>
          <td>
            <img class="img-btn-kh m-10-d-r-kh" onclick="kameHouse.extension.deploymentManager.undeployModule('admin')" 
              src="/kame-house/img/other/cancel.png" alt="Undeploy" title="Undeploy"/>
            <img class="img-btn-kh m-10-d-r-kh" onclick="kameHouse.extension.deploymentManager.deployModule('admin')" 
              src="/kame-house/img/other/rocket-green.png" alt="Deploy" title="Deploy"/>
            <img class="img-btn-kh" onclick="kameHouse.extension.deploymentManager.deployModuleAllServers('admin')" 
              src="/kame-house/img/other/cloud-up-down-green.png" alt="Deploy - All Servers" title="Deploy - All Servers"/>
          </td>
        </tr>
        <tr>
          <td><div id="mst-media-header-val">media</div></td>
          <td>/kame-house-media</td>
          <td id="mst-media-status-val"><img class="img-tomcat-manager-status" src="/kame-house/img/other/ball-blue.png" alt="status" title="status"/></td>
          <td id="mst-media-build-version-val">N/A</td>
          <td id="mst-media-build-date-val">N/A</td>
          <td>
            <img class="img-btn-kh m-7-d-r-kh" onclick="kameHouse.extension.deploymentManager.startModule('media')" 
              src="/kame-house/img/mplayer/play-green.png" alt="Start" title="Start"/>
            <img class="img-btn-kh" onclick="kameHouse.extension.deploymentManager.stopModule('media')" 
              src="/kame-house/img/mplayer/stop.png" alt="Stop" title="Stop"/>
          </td>
          <td>
            <img class="img-btn-kh m-10-d-r-kh" onclick="kameHouse.extension.deploymentManager.undeployModule('media')" 
              src="/kame-house/img/other/cancel.png" alt="Undeploy" title="Undeploy"/>
            <img class="img-btn-kh m-10-d-r-kh" onclick="kameHouse.extension.deploymentManager.deployModule('media')" 
              src="/kame-house/img/other/rocket-green.png" alt="Deploy" title="Deploy"/>
            <img class="img-btn-kh" onclick="kameHouse.extension.deploymentManager.deployModuleAllServers('media')" 
              src="/kame-house/img/other/cloud-up-down-green.png" alt="Deploy - All Servers" title="Deploy - All Servers"/>
          </td>
        </tr>
        <tr>
          <td><div id="mst-tennisworld-header-val">tennisworld</div></td>
          <td>/kame-house-tennisworld</td>
          <td id="mst-tennisworld-status-val"><img class="img-tomcat-manager-status" src="/kame-house/img/other/ball-blue.png" alt="status" title="status"/></td>
          <td id="mst-tennisworld-build-version-val">N/A</td>
          <td id="mst-tennisworld-build-date-val">N/A</td>
          <td>
            <img class="img-btn-kh m-7-d-r-kh" onclick="kameHouse.extension.deploymentManager.startModule('tennisworld')" 
              src="/kame-house/img/mplayer/play-green.png" alt="Start" title="Start"/>
            <img class="img-btn-kh" onclick="kameHouse.extension.deploymentManager.stopModule('tennisworld')" 
              src="/kame-house/img/mplayer/stop.png" alt="Stop" title="Stop"/>
          </td>
          <td>
            <img class="img-btn-kh m-10-d-r-kh" onclick="kameHouse.extension.deploymentManager.undeployModule('tennisworld')" 
              src="/kame-house/img/other/cancel.png" alt="Undeploy" title="Undeploy"/>
            <img class="img-btn-kh m-10-d-r-kh" onclick="kameHouse.extension.deploymentManager.deployModule('tennisworld')" 
              src="/kame-house/img/other/rocket-green.png" alt="Deploy" title="Deploy"/>
            <img class="img-btn-kh" onclick="kameHouse.extension.deploymentManager.deployModuleAllServers('tennisworld')" 
              src="/kame-house/img/other/cloud-up-down-green.png" alt="Deploy - All Servers" title="Deploy - All Servers"/>
          </td>
        </tr>
        <tr>
          <td><div id="mst-testmodule-header-val">testmodule</div></td>
          <td>/kame-house-testmodule</td>
          <td id="mst-testmodule-status-val"><img class="img-tomcat-manager-status" src="/kame-house/img/other/ball-blue.png" alt="status" title="status"/></td>
          <td id="mst-testmodule-build-version-val">N/A</td>
          <td id="mst-testmodule-build-date-val">N/A</td>
          <td>
            <img class="img-btn-kh m-7-d-r-kh" onclick="kameHouse.extension.deploymentManager.startModule('testmodule')" 
              src="/kame-house/img/mplayer/play-green.png" alt="Start" title="Start"/>
            <img class="img-btn-kh" onclick="kameHouse.extension.deploymentManager.stopModule('testmodule')" 
              src="/kame-house/img/mplayer/stop.png" alt="Stop" title="Stop"/>
          </td>
          <td>
            <img class="img-btn-kh m-10-d-r-kh" onclick="kameHouse.extension.deploymentManager.undeployModule('testmodule')" 
              src="/kame-house/img/other/cancel.png" alt="Undeploy" title="Undeploy"/>
            <img class="img-btn-kh m-10-d-r-kh" onclick="kameHouse.extension.deploymentManager.deployModule('testmodule')" 
              src="/kame-house/img/other/rocket-green.png" alt="Deploy" title="Deploy"/>
            <img class="img-btn-kh" onclick="kameHouse.extension.deploymentManager.deployModuleAllServers('testmodule')" 
              src="/kame-house/img/other/cloud-up-down-green.png" alt="Deploy - All Servers" title="Deploy - All Servers"/>
          </td>
        </tr>
        <tr>
          <td><div id="mst-ui-header-val">ui</div></td>
          <td>/kame-house</td>
          <td id="mst-ui-status-val"><img class="img-tomcat-manager-status" src="/kame-house/img/other/ball-blue.png" alt="status" title="status"/></td>
          <td id="mst-ui-build-version-val">N/A</td>
          <td id="mst-ui-build-date-val">N/A</td>
          <td>
            <img class="img-btn-kh m-7-d-r-kh" onclick="kameHouse.extension.deploymentManager.startModule('ui')" 
              src="/kame-house/img/mplayer/play-green.png" alt="Start" title="Start"/>
            <img class="img-btn-kh" onclick="kameHouse.extension.deploymentManager.stopModule('ui')" 
              src="/kame-house/img/mplayer/stop.png" alt="Stop" title="Stop"/>
          </td>
          <td>
            <img class="img-btn-kh m-10-d-r-kh" onclick="kameHouse.extension.deploymentManager.undeployModule('ui')" 
              src="/kame-house/img/other/cancel.png" alt="Undeploy" title="Undeploy"/>
            <img class="img-btn-kh m-10-d-r-kh" onclick="kameHouse.extension.deploymentManager.deployModule('ui')" 
              src="/kame-house/img/other/rocket-green.png" alt="Deploy" title="Deploy"/>
            <img class="img-btn-kh" onclick="kameHouse.extension.deploymentManager.deployModuleAllServers('ui')" 
              src="/kame-house/img/other/cloud-up-down-green.png" alt="Deploy - All Servers" title="Deploy - All Servers"/>
          </td>
        </tr>
        <tr>
          <td><div id="mst-vlcrc-header-val">vlcrc</div></td>
          <td>/kame-house-vlcrc</td>
          <td id="mst-vlcrc-status-val"><img class="img-tomcat-manager-status" src="/kame-house/img/other/ball-blue.png" alt="status" title="status"/></td>
          <td id="mst-vlcrc-build-version-val">N/A</td>
          <td id="mst-vlcrc-build-date-val">N/A</td>
          <td>
            <img class="img-btn-kh m-7-d-r-kh" onclick="kameHouse.extension.deploymentManager.startModule('vlcrc')" 
              src="/kame-house/img/mplayer/play-green.png" alt="Start" title="Start"/>
            <img class="img-btn-kh" onclick="kameHouse.extension.deploymentManager.stopModule('vlcrc')" 
              src="/kame-house/img/mplayer/stop.png" alt="Stop" title="Stop"/>
          </td>
          <td>
            <img class="img-btn-kh m-10-d-r-kh" onclick="kameHouse.extension.deploymentManager.undeployModule('vlcrc')" 
              src="/kame-house/img/other/cancel.png" alt="Undeploy" title="Undeploy"/>
            <img class="img-btn-kh m-10-d-r-kh" onclick="kameHouse.extension.deploymentManager.deployModule('vlcrc')" 
              src="/kame-house/img/other/rocket-green.png" alt="Deploy" title="Deploy"/>
            <img class="img-btn-kh" onclick="kameHouse.extension.deploymentManager.deployModuleAllServers('vlcrc')" 
              src="/kame-house/img/other/cloud-up-down-green.png" alt="Deploy - All Servers" title="Deploy - All Servers"/>
          </td>
        </tr>
      </table>
      <br>
      <table class="table-kh">
        <tr class="table-kh-header">
          <td>module</td>
          <td class="non-tomcat-modules-table-build-version">build version</td>
          <td class="non-tomcat-modules-table-build-date">build date</td>
          <td class="non-tomcat-modules-table-deployment">deployment</td>
        </tr>
        <tr>
          <td><div id="mst-cmd-header-val">cmd</div></td>
          <td id="mst-cmd-build-version-val">N/A</td>
          <td id="mst-cmd-build-date-val">N/A</td>
          <td>
            <img class="img-btn-kh m-10-d-r-kh" onclick="kameHouse.extension.deploymentManager.deployModule('cmd')" 
              src="/kame-house/img/other/rocket-green.png" alt="Deploy" title="Deploy"/>
            <img class="img-btn-kh" onclick="kameHouse.extension.deploymentManager.deployModuleAllServers('cmd')" 
              src="/kame-house/img/other/cloud-up-down-green.png" alt="Deploy - All Servers" title="Deploy - All Servers"/>
          </td>
        </tr>
        <tr>
          <td><div id="mst-groot-header-val">groot</div></td>
          <td id="mst-groot-build-version-val">N/A</td>
          <td id="mst-groot-build-date-val">N/A</td>
          <td>
            <img class="img-btn-kh m-10-d-r-kh" onclick="kameHouse.extension.deploymentManager.deployModule('groot')" 
              src="/kame-house/img/other/rocket-green.png" alt="Deploy" title="Deploy"/>
            <img class="img-btn-kh" onclick="kameHouse.extension.deploymentManager.deployModuleAllServers('groot')" 
              src="/kame-house/img/other/cloud-up-down-green.png" alt="Deploy - All Servers" title="Deploy - All Servers"/>
          </td>
        </tr>
        <tr>
          <td><div id="mst-shell-header-val">shell</div></td>
          <td id="mst-shell-build-version-val">N/A</td>
          <td id="mst-shell-build-date-val">N/A</td>
          <td>
            <img class="img-btn-kh m-10-d-r-kh" onclick="kameHouse.extension.deploymentManager.deployModule('shell')" 
              src="/kame-house/img/other/rocket-green.png" alt="Deploy" title="Deploy"/>
            <img class="img-btn-kh" onclick="kameHouse.extension.deploymentManager.deployModuleAllServers('shell')" 
              src="/kame-house/img/other/cloud-up-down-green.png" alt="Deploy - All Servers" title="Deploy - All Servers"/>
          </td>
        </tr>        
      </table>

      <pre class="kamehouse-shell-output tomcat-process-kamehouse-shell-output"><div id="tomcat-process-status-val">Tomcat process status not available at the moment</div></pre>
      <span class="bold-kh">Tomcat Process: </span>
      <img class="img-btn-kh m-7-d-r-kh" onclick="kameHouse.extension.deploymentManager.restartTomcat()" 
        src="/kame-house/img/mplayer/resume.png" alt="Restart Tomcat" title="Restart Tomcat"/>
      <span class="tomcat-debug-mode-span">Debug Mode: <input id="tomcat-debug-mode" class="tomcat-debug-mode" type="checkbox" name="tomcat-debug-mode"></span>
      <br><br>

    </div> <!-- tab-deployment -->

    <div id="tab-git" class="default-layout tab-content-kh p-7-d-kh w-70-pc-kh w-100-pc-m-kh">
      <div class="groot-image-info-wrapper">
        <table class="info-image-table">
          <caption class="hidden-kh">Image-Info</caption>
          <thead class="hidden-kh"><tr><th>Image-Info</th></tr></thead>
          <tbody>
            <tr>
              <td class="info-image-img">
                <img src="/kame-house/img/dbz/gotenks-fusion.jpg" alt="info image"/>
              </td>
              <td class="info-image-info">
                <div class="info-image-title">
                  Synchronize
                </div>
                <div class="info-image-desc">
                  <p>Keep my git repos in perfect sync like goten and trunks doing the fusion</p>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <div class="link-image-table-wrapper">
        <table class="link-image-table link-image-table-reverse">
          <caption class="hidden-kh">Image-Links</caption>
          <thead class="hidden-kh"><tr><th>Image-Links</th></tr></thead>
          <tbody>
            <tr>
              <td>
                <a><img class="link-image-img" src="/kame-house/img/other/git-pull-request-blue.png" alt="Git Pull" title="Git Pull" onclick="kameHouse.extension.gitManager.pullAll()"/></a><br>
                <a><img class="link-image-img" src="/kame-house/img/other/cloud-up-down-blue.png" alt="Git Pull All Servers" title="Git Pull All Servers" onclick="kameHouse.extension.gitManager.pullAllAllServers()"/></a>
              </td>
              <td>
                <div class="link-image-text">Git Pull</div>
                <div class="link-image-desc">Pull latest changes in all my git repos. Pull in all servers using the cloud button</div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      <br><br>

    </div> <!-- tab-git -->

    <div id="tab-media" class="default-layout tab-content-kh p-7-d-kh w-50-pc-kh w-100-pc-m-kh">

      <div class="groot-image-info-wrapper">
        <table class="info-image-table info-image-table-reverse">
          <caption class="hidden-kh">Image-Info</caption>
          <thead class="hidden-kh"><tr><th>Image-Info</th></tr></thead>
          <tbody>
            <tr>
              <td class="info-image-img">
                <img src="/kame-house/img/dbz/gohan-studying.jpg" alt="info image"/>
              </td>
              <td class="info-image-info">
                <div class="info-image-title">
                  Media Library
                </div>
                <div class="info-image-desc">
                  <p>Regenerate my media library in the streaming media server</p>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <div class="link-image-table-wrapper">
        <table class="link-image-table">
          <caption class="hidden-kh">Image-Links</caption>
          <thead class="hidden-kh"><tr><th>Image-Links</th></tr></thead>
          <tbody>
            <tr>
              <td>
                <a><img class="link-image-img" src="/kame-house/img/mplayer/playlist-blue.png" alt="Create Playlists" title="Create Playlists" onclick="kameHouse.extension.serverManager.createAllVideoPlaylists()"/></a>
              </td>
              <td>
                <div class="link-image-text">Update my media database</div>
                <div class="link-image-desc">Click to regenerate all video playlists. This command can only be executed in the media server</div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      <br><br>

    </div> <!-- tab-media -->

    <div id="tab-power" class="tab-content-kh">
      <div class="default-layout p-7-d-kh w-40-pc-kh w-100-pc-m-kh">
        <div class="groot-image-info-wrapper">
          <table class="info-image-table">
            <caption class="hidden-kh">Image-Info</caption>
            <thead class="hidden-kh"><tr><th>Image-Info</th></tr></thead>
            <tbody>
              <tr>
                <td class="info-image-img">
                  <img src="/kame-house/img/marvel/spiderman-swinging.jpg" alt="info image"/>
                </td>
                <td class="info-image-info">
                  <div class="info-image-title">
                    Power Management
                  </div>
                  <div class="info-image-desc">
                    <p>With Great Power Comes Great Responsibility</p>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <div class="link-image-table-wrapper">
          <table class="link-image-table">
            <caption class="hidden-kh">Image-Links</caption>
            <thead class="hidden-kh"><tr><th>Image-Links</th></tr></thead>
            <tbody>
              <tr>
                <td>
                  <a><img class="link-image-img" src="/kame-house/img/pc/shutdown-red.png" alt="Reboot" title="Reboot" onclick="kameHouse.extension.serverManager.confirmRebootServer()"/></a>
                </td>
                <td>
                  <div class="link-image-text">Restart the server</div>
                  <div class="link-image-desc">Click to restart now. If I need to schedule a shutdown or hibernate, I can do it from /kame-house's server management page</div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
        <br><br>
      </div>
    </div> <!-- tab-power -->

    <div id="tab-tail-log" class="default-layout tab-content-kh p-15-d-kh">

      <div class="groot-image-info-wrapper">
        <table class="info-image-table info-image-table-reverse">
          <caption class="hidden-kh">Image-Info</caption>
          <thead class="hidden-kh"><tr><th>Image-Info</th></tr></thead>
          <tbody>
            <tr>
              <td class="info-image-img">
                <img src="/kame-house/img/dbz/goku-chibi-tail.jpg" alt="info image"/>
              </td>
              <td class="info-image-info">
                <div class="info-image-title">
                  Tail Logs
                </div>
                <div class="info-image-desc">
                  <p>Good luck figuring out what's going on in the backend</p>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <div class="link-image-table-wrapper">
        <table class="link-image-table">
          <caption class="hidden-kh">Image-Links</caption>
          <thead class="hidden-kh"><tr><th>Image-Links</th></tr></thead>
          <tbody>
            <tr>
              <td>
                <a><img id="toggle-tail-log-img" class="link-image-img" src="/kame-house/img/mplayer/play-gray.png" alt="Toggle Tail Log" title="Toggle Tail Log" onclick="kameHouse.extension.tailLogManagerWrapper.toggleTailLog()"/></a>
              </td>
              <td>
                <div class="link-image-text">Tail the logs of the current processes running in the server</div>
                <div class="link-image-desc">Once tail log is started, you can switch between logs to tail and the number of lines without the need for stopping and starting</div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      <br><br>

      <div id="log-selector" class="log-selector">
        <span class="bold-kh p-15-d-kh">File: </span>
        <select class="select-kh-dark m-10-d-r-kh m-10-m-r-kh" id="tail-log-dropdown">
          <option value="common/logs/cat-backup-server-log.sh">backup-server</option>
          <option value="common/logs/cat-create-all-video-playlists-log.sh">create-all-video-playlists</option>
          <option value="common/logs/cat-deploy-all-servers-log.sh">deploy-all-servers</option>
          <option value="common/logs/cat-deploy-kamehouse-log.sh" selected>deploy-kamehouse</option>
          <option value="common/logs/cat-git-pull-all-log.sh">git-pull-all</option>
          <option value="common/logs/cat-git-pull-all-all-servers-log.sh">git-pull-all-all-servers</option>
          <option value="common/logs/cat-httpd-log.sh">httpd</option>
          <option value="common/logs/cat-httpd-error-log.sh">httpd-error</option>
          <option value="common/logs/cat-kamehouse-log.sh">kamehouse</option>
          <option value="common/logs/cat-resync-subtitles-log.sh">resync-subtitles</option>
          <option value="common/logs/cat-tomcat-log.sh">tomcat</option>
          <option value="common/logs/cat-world-cup-bookings-log.sh">world-cup-bookings</option>
        </select>
        <div id="tail-log-num-lines">
          <span class="bold-kh p-15-d-kh">Number of lines: </span>
          <select class="select-kh-dark m-10-d-r-kh m-10-m-r-kh" id="tail-log-num-lines-dropdown">
            <option value="50" selected>50</option>
            <option value="150">150</option>
            <option value="350">350</option>
            <option value="500">500</option>
            <option value="1000">1000</option>
            <option value="1500">1500</option>
            <option value="2000">2000</option>
          </select>
        </div>
        <div id="tail-log-level">
          <span class="bold-kh p-15-d-kh">Log level: </span>
          <select class="select-kh-dark m-10-d-r-kh m-10-m-r-kh" id="tail-log-level-dropdown">
            <option value="ERROR">ERROR</option>
            <option value="WARN">WARN</option>
            <option value="INFO">INFO</option>
            <option value="DEBUG">DEBUG</option>
            <option value="TRACE">TRACE</option>
            <option value="ALL" selected>ALL</option>
          </select>
        </div>
      </div>
      <button id="tail-log-output-wrapper" class="collapsible-kh collapsible-kh-btn">Tail Log Output</button>
      <div class="collapsible-kh-content">
        <button class="btn-svg-scroll-down fl-r-d-kh"
          onclick="kameHouse.core.scrollToBottom('btn-tail-log-scroll-up')">
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 12 6"><path d="M12 6H0l6-6z"/></svg>
        </button>
        <!-- pre and table need to be in the same line or it prints some extra lines -->
        <pre class="kamehouse-shell-output"><table class="kamehouse-shell-output-table">
            <caption class="hidden-kh">Tail Log Output</caption>
            <tr class="hidden-kh">
              <th scope="row">Tail Log Output</th>
            </tr>
            <tbody id="tail-log-output-table-body">
              <tr><td>Tail log not triggered yet...</td></tr>
            </tbody>
        </table></pre>
        <button class="btn-svg-scroll-up fl-r-d-kh" id="btn-tail-log-scroll-up" 
          onclick="kameHouse.core.scrollToTop('tail-log-output-wrapper')">
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 12 6"><path d="M12 6H0l6-6z"/></svg>
        </button>
      </div>
    
    </div> <!-- tab-tail-log -->

  <div class="default-layout p-7-d-kh">
    <button id="command-output-wrapper" class="collapsible-kh collapsible-kh-btn">Command Output</button>
    <div class="collapsible-kh-content">
      <button class="btn-svg-scroll-down fl-r-d-kh"
        onclick="kameHouse.core.scrollToTop('btn-command-output-scroll-up')">
        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 12 6"><path d="M12 6H0l6-6z"/></svg>
      </button>
      
        <!-- pre and the divs need to be in the same line or it prints some extra lines -->
      <pre id="kamehouse-shell-output-executing-wrapper" class="kamehouse-shell-output-executing-wrapper hidden-kh"><div id="kamehouse-shell-output-executing" class="txt-c-d-kh txt-c-m-kh"></div><br><div class="txt-c-d-kh txt-c-m-kh">Please wait...</div><div class="spinning-wheel"></div></pre>

      <!-- pre and table need to be in the same line or it prints some extra lines -->
      <pre id="kamehouse-shell-output" class="kamehouse-shell-output"><table class="kamehouse-shell-output-table">
          <caption class="hidden-kh">Script Output</caption>
          <tr class="hidden-kh">
            <th scope="row">Script Output</th>
          </tr>
          <tbody id="kamehouse-shell-output-table-body">
            <tr><td>No command executed yet...</td></tr>
          </tbody>
      </table></pre>
      <button class="btn-svg-scroll-up fl-r-d-kh" id="btn-command-output-scroll-up" 
        onclick="kameHouse.core.scrollToTop('command-output-wrapper')">
        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 12 6"><path d="M12 6H0l6-6z"/></svg>
      </button>
    </div>
    <br>
  </div>
  <span id="debug-mode-wrapper"></span>
  <script src="/kame-house/js/admin/tomcat-module-status-manager.js"></script>
  <script src="/kame-house-groot/js/admin/kamehouse-shell/tail-log-manager.js"></script>
  <script src="/kame-house-groot/kamehouse-groot/js/kamehouse-shell.js"></script>
  <script src="/kame-house-groot/js/admin/server-manager/groot-server-manager.js"></script>
</body>
</html>
