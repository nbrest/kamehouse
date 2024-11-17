| [Home](/README.md) | [Docs](/docs/README.md) |
---------------------------------------------------------------

*********************

# Media Module:

This module handles the following functionality:

- Get all the video playlists and load them to kamehouse

- The playlists are expected to be in `.m3u` format

- These playlists are used by other modules to start vlc player to play either an entire playlist or a single video within a playlist

### Create your own playlists

- These playlists will then be accessed by all servers running kamehouse

- Generate the playlists for your video files and put them in your own private git repository `${HOME}/git/kamehouse-video-playlists/` and inside there create a `/playlists` folder

- Clone this repository with the playlists on any server running kamehouse

### Playlists folder structure

- Create 3 separate directories under `/playlists`:

    - `video-kamehouse-windows`: If the media server is a windows box, put the playlists here that will be accessed by a kamehouse instance running locally on the media server

    - `video-kamehouse-linux`: If the media server is a linux box, put the playlists here that will be accessed by a kamehouse instance running locally on the media server

    - `video-kamehouse-remote`: Put the playlists here to be accessed by kamehouse instances running on all other servers except the media server. These instances will access the video files remotely

### Playlist categories

- The playlists in each of those directories need to be divided into `categories`, and each category is a folder. For example under `video-kamehouse-windows` you could have the subfolders `anime`, `cartoons`, `futbol`, `movies`, `series`, `tennis`

- Put all the `m3u` for each category in the proper folder. For example you could **(and should!)** have a `dragonball.m3u` playlist under `anime`

- Then vlc player ui will render the contents of `dragonball.m3u` and allow you to play either the entire playlist or any element of the playlist individually

### Generate remote playlists

- The remote playlists should be identical to the local windows or linux playlists, except that the prefix for each file will be changed. Instead of pointing to a local file, they will point to the remote media server that should be exposing the media files somehow

- One way to expose the video files in the media server to other servers, could be through the `http` server that runs kamehouse's ui in the media server

- For example, assuming you are running the media server on windows, you could have a playlist `${HOME}/git/kamehouse-video-playlists/playlists/video-kamehouse-windows/anime/dragonball.m3u` with the content:

```
#EXTM3U
#EXTINF:0,\anime\dragonball\001-DBZ.avi
D:\media\videos\anime\dragonball\001-DBZ.avi
#EXTINF:0,\anime\dragonball\002-DBZ.avi
D:\media\videos\anime\dragonball\002-DBZ.avi
#EXTINF:0,\anime\dragonball\003-DBZ.avi
D:\media\videos\anime\dragonball\003-DBZ.avi
```

- For that same playlist, you then create a remote playlist  `${HOME}/git/kamehouse-video-playlists/playlists/video-kamehouse-remote/anime/dragonball.m3u` with the content:

```
#EXTM3U
#EXTINF:0,\anime\dragonball\001-DBZ.avi
https://192.168.0.2/kame-house-streaming/media-server/media/videos/anime/dragonball/001-DBZ.avi
#EXTINF:0,\anime\dragonball\002-DBZ.avi
https://192.168.0.2/kame-house-streaming/media-server/media/videos/anime/dragonball/002-DBZ.avi
#EXTINF:0,\anime\dragonball\003-DBZ.avi
https://192.168.0.2/kame-house-streaming/media-server/media/videos/anime/dragonball/003-DBZ.avi
```

- Assuming that your media server's ip is `192.168.0.2`

### Expose videos in media server to other local servers

- Then expose your media server's `media` folder through `http` to your local network so that other servers can access the video files through http

- One way to do that is creating a symlink from the `media` folder containing all your videos into your media server's http root content directory structure. Then all your videos in your media server should be accessible through `http` by your other devices in your local network

- You could also create the remote playlists using a network protocol different to `http`, like `sftp` or `smb` and create the remote playlists with that protocol instead
