@echo off
setlocal
powershell -ExecutionPolicy Bypass -File "%~dp0gen.ps1" %*
exit /b %errorlevel%
