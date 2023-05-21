$(document).ready(() => {
  kameHouse.util.module.waitForModules(["crudManager"], () => {    
    kameHouse.plugin.crudManager.init({
      entityName: "Vlc Player",
      url: "/kame-house-vlcrc/api/v1/vlc-rc/players",
      banner: "banner-pegasus-ryu-sei-ken",
      icon: "/kame-house/img/mplayer/vlc.png",
      infoImage: {
        img: "/kame-house/img/banners/saint-seiya/banner-athena-saints.jpg",
        title: "KameHouse Media Player",
        desc: "Control all the media stored in this KameHouse server with the greatest media player VLC",
        isReverse: false
      },
      columns: [
        { 
          name: "id",
          type: "id"
        },
        { 
          name: "hostname",
          type: "text"
        }, 
        { 
          name: "port",
          type: "number"
        }, 
        { 
          name: "username",
          type: "text"
        }, 
        { 
          name: "password",
          type: "password"
        }
      ]
    });
  });
});
