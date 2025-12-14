if [ "${TERM_PROGRAM}" == "tmux" ]; then
  # default tmux options
  tmux set history-limit 99999
  tmux set mouse on
fi