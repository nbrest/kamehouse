#!/bin/bash

echo "$(date) - Starting disable-swap.sh"
sudo dphys-swapfile swapoff
sudo dphys-swapfile uninstall
sudo update-rc.d dphys-swapfile remove
echo "$(date) - Finished disable-swap.sh"

