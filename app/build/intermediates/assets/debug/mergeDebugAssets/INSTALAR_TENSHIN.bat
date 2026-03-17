@echo off
color 0a
title SISTEMA DE TRANSMISION ENTRATI - PROTOCOLO 1999
cls
echo .
echo  [ SISTEMA HÖLLVANIA DETECTADO ]
echo  [ INICIANDO TRANSFERENCIA DE DATOS DE EL ESPECTRO ]
echo .
timeout /t 1 >nul
echo  >> Accediendo a archivos de Sainan...
timeout /t 1 >nul
echo  >> Sincronizando con el vacio...
timeout /t 1 >nul

:: Crear carpeta en la PC
if not exist "C:\Tenshin1999" mkdir "C:\Tenshin1999"

:: Copiar archivos (el EXE debe estar en la misma carpeta que este BAT)
copy "TenshinBridge.exe" "C:\Tenshin1999\" /y

:: Agregar al inicio de Windows
reg add "HKCU\Software\Microsoft\Windows\CurrentVersion\Run" /v "TenshinBridge" /t REG_SZ /d "C:\Tenshin1999\TenshinBridge.exe" /f

:: Configurar Firewall (Requiere permisos)
netsh advfirewall firewall add rule name="Tenshin Bridge 1999" dir=in action=allow protocol=TCP localport=8080

echo .
echo  [ INSTALACION COMPLETADA ]
echo  [ ARTHUR ESTA LISTO PARA LA SINCRONIZACION ]
echo .
pause
start C:\Tenshin1999\TenshinBridge.exe
exit
