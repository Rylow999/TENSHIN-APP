@echo off
color 0a
title ENTRATI TRANSMISSION SYSTEM - PROTOCOL 1999
cls
echo .
echo  [ HÖLLVANIA SYSTEM DETECTED ]
echo  [ INITIATING SPECTRUM DATA TRANSFER ]
echo .
timeout /t 1 >nul
echo  >> Accessing Sainan API files...
timeout /t 1 >nul
echo  >> Synchronizing with the Void...
timeout /t 1 >nul

:: Create folder on the PC
if not exist "C:\Tenshin1999" mkdir "C:\Tenshin1999"

:: Copy files (the EXE must be in the same folder as this BAT)
copy "TenshinBridge.exe" "C:\Tenshin1999\" /y

:: Add to Windows Startup
reg add "HKCU\Software\Microsoft\Windows\CurrentVersion\Run" /v "TenshinBridge" /t REG_SZ /d "C:\Tenshin1999\TenshinBridge.exe" /f

:: Configure Firewall (Requires permissions)
netsh advfirewall firewall add rule name="Tenshin Bridge 1999" dir=in action=allow protocol=TCP localport=8080

echo .
echo  [ INSTALLATION COMPLETE ]
echo  [ ARTHUR IS READY FOR SYNCHRONIZATION ]
echo .
pause
start C:\Tenshin1999\TenshinBridge.exe
exit
