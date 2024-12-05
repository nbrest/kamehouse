$file = $args[0]
$password = $args[1]

$psexec = "$env:USERPROFILE" + '\programs\pstools\psexec.exe'
$explorerArgs = '/root,' + $file
echo "explorer.exe args: $explorerArgs"

echo "Killing running vlc.exe instances"
taskkill /im vlc.exe /F

echo "Running vlc through explorer.exe to play file: $file"
&($psexec) -accepteula -nobanner -i -d -s explorer.exe $explorerArgs

# this option starts vlc directly but requires me to pass the windows user password as parameter to this script
# &($psexec) -accepteula -nobanner -i -d -x -u nbrest -p password vlc.exe $file
