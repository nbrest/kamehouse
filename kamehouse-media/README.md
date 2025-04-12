| [Home](/README.md) | [Docs](/docs/README.md) |
---------------------------------------------------------------

*********************

# Media Module:

- Update the `kamehouse-media` values in `${HOME}/.kamehouse/config/kamehouse.cfg` to match your local media setup

This module handles the following functionality:

- Get all the video playlists and load them to kamehouse

- The playlists are expected to be in `.m3u` format

- These playlists are used by other modules to start vlc player to play either an entire playlist or a single video within a playlist

### Create your own playlists

- These playlists will then be accessed by all servers running kamehouse

- Generate the playlists for your video files and put them in your own path inside your user's home

- Set the value of `PLAYLISTS_PATH` in `${HOME}/.kamehouse/config/kamehouse.cfg` with the path to the root directory that contains your playlists

- **IMPORTANT**: When running kamehouse on a docker container controlling a host, the value of `PLAYLISTS_PATH` inside the docker container's `${HOME}/.kamehouse/config/kamehouse.cfg` must match the value on the host's `${HOME}/.kamehouse/config/kamehouse.cfg` for the playlists to be populated properly on the vlc UI running on the container

- This is a sample [kamehouse.cfg](/docker/config/kamehouse.cfg) with all the possible configurations

### Playlists folder structure

- The playlists folder should contain the following structure

- In the root level of the playlists folder, there should be only subfolders. Each folder refers to a playlist **category**: like `music`, `music_videos`, `anime`, `cartoons`, `futbol`, `movies`, `series`, `tennis`

- By convention, when loading playlists under the category `music`, in the subfolder `music` at the root of the playlists folder, vlc player starts minimized. It also starts minimized if the file to play is a single `mp3` file

- In each **category** folder, there should be a folder for each playlist, with the exact name of the playlist. For example, if you have the category `anime`, then you can **(and should!)** have a `dragonball_all` folder. Inside that `dragonball_all` folder, you would add the `dragonball_all.m3u` playlist

- Then vlc player ui will render the contents of `dragonball.m3u` and allow you to play either the entire playlist or any element of the playlist individually

- The playlists folder structure could look something like this:
```sh
${HOME}/.kamehouse/data/playlists/anime/dragonball_all/dragonball_all.m3u
${HOME}/.kamehouse/data/playlists/anime/saint_seiya_all/saint_seiya_all.m3u
${HOME}/.kamehouse/data/playlists/movies/movies_dc_all/movies_dc_all.m3u
${HOME}/.kamehouse/data/playlists/movies/movies_marvel_all/movies_marvel_all.m3u
${HOME}/.kamehouse/data/playlists/series/game_of_thrones_all/game_of_thrones_all.m3u
```
- The docker [playlist](/docker/media/playlist/) folder contains a sample structure of the playlists used in docker demo

### Playlists content

- The playlists could point to local files to be loaded from the filesystem or to remote files to be loaded externally

- For example, the content of `${HOME}/.kamehouse/data/playlists/anime/dragonball_all/dragonball_all.m3u` could be pointing to local files:
```sh
#EXTM3U
#EXTINF:0,\anime\dragonball\001-DBZ.avi
D:\media\videos\anime\dragonball\001-DBZ.avi
#EXTINF:0,\anime\dragonball\002-DBZ.avi
D:\media\videos\anime\dragonball\002-DBZ.avi
#EXTINF:0,\anime\dragonball\003-DBZ.avi
D:\media\videos\anime\dragonball\003-DBZ.avi
```

- Or they could be pointing to remote files to be loaded via `http` streamed through another server
```sh
#EXTM3U
#EXTINF:0,\anime\dragonball\001-DBZ.avi
https://192.168.0.2/kame-house-streaming/media-server/media/videos/anime/dragonball/001-DBZ.avi
#EXTINF:0,\anime\dragonball\002-DBZ.avi
https://192.168.0.2/kame-house-streaming/media-server/media/videos/anime/dragonball/002-DBZ.avi
#EXTINF:0,\anime\dragonball\003-DBZ.avi
https://192.168.0.2/kame-house-streaming/media-server/media/videos/anime/dragonball/003-DBZ.avi
```
