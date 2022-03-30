#!/bin/bash

# Import common functions
source ${HOME}/programs/kamehouse-shell/bin/common/common-functions.sh
if [ "$?" != "0" ]; then
  echo -e "\033[1;36m$(date +%Y-%m-%d' '%H:%M:%S)\033[0;39m - [\033[1;31mERROR\033[0;39m] - \033[1;31mAn error occurred importing common-functions.sh\033[0;39m"
  exit 1
fi

mainProcess() {
  FILES_TO_DELETE=${HOME}/logs/files_to_delete.txt
  echo "" > ${FILES_TO_DELETE}
  PATH_CURRENT_VIDEO_FILES=//niko-server/media-drive/movies
  MIN_FILESIZE="50000000"
  VIDEO_TYPES="avi\|mpg\|mpeg\|mp4\|mkv\|ogg\|ogm\|AVI\|MPG\|MPEG\|MP4\|MKV\|OGG\|OGM"
  find ${PATH_CURRENT_VIDEO_FILES} | grep -e "${VIDEO_TYPES}" | sort | while read FILE; do 
    FILE_SIZE=`stat --printf="%s" "${FILE}"`
    if [ "${FILE_SIZE}" -lt "${MIN_FILESIZE}" ]; then
        log.info "To delete: ${FILE_SIZE} ${FILE}"
        echo "${FILE}" >> ${FILES_TO_DELETE}
    fi 
  done
}

main "$@"
