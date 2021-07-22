### Message every time I open a shell
if (( $EUID != 0 )); then
  # Normal user
  echo ""
  echo -e "                          ${COL_RED}まだまだだね${COL_NORMAL}"
  echo ""
else
  # root
  echo ""
  echo -e "                          ${COL_BLUE}pegasus ryu sei ken${COL_NORMAL}"
  echo ""
fi
