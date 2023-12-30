#!/bin/bash

# Import common functions
source ${HOME}/programs/kamehouse-shell/bin/common/common-functions.sh
if [ "$?" != "0" ]; then
	echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
	exit 149
fi
source ${HOME}/programs/kamehouse-shell/bin/common/audio-playlists/audio-playlists-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing audio-playlists-functions.sh\033[0;39m"
  exit 149
fi

# To use on android phones where I have both the playlists and the music files in the same storage device
# Store the playlists git repo in: SD Card/git/kamehouse-audio-playlists
# Store the music files in:        SD Card/Music/mp3

# Symlinks to test relative path kamehouse-audio-playlists simulating the structure of and sd card in drive D:
# cmd.exe "/c mklink /D D:\git C:\Users\nbrest\git"
# cmd.exe "/c mklink /D D:\Music\mp3 D:\niko9enzo\mp3"

PATH_RELATIVE_MUSIC="../../../../../Music"
PATH_PLS_DEST=${PROJECT_DIR}/audio-relative-5-subpaths-back
PATH_BASE_DEST="${PATH_RELATIVE_MUSIC}/mp3"
PATH_BASE_N2_DEST="${PATH_RELATIVE_MUSIC}/truecrypt/mp3"

main "$@"
