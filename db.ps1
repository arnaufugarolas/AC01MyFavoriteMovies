$IPAddresses = Get-NetIPAddress -InterfaceAlias "*Ethernet*", "*Wi-Fi*" -AddressFamily IPv4 | Sort-Object IPAddress -Descending | Select-Object -First 1 -ExpandProperty IPAddress

while ($IPAddresses -eq $null)
{
    $IPAddresses = Read-Host "Enter your IP address"
}

if ($IPAddresses)
{
    json-server -H $IPAddresses -w db.json
}
else
{
    Write-Host "Invalid IP address"
}
