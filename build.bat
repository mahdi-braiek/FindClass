@echo off
echo ============================================
echo   FindClass - Build Script
echo ============================================
echo.

REM --- Nettoyage ---
if exist bin rmdir /S /Q bin
if exist dist rmdir /S /Q dist
mkdir bin
mkdir dist

echo [1/3] Compilation des sources Java...
echo.

REM --- Collecter tous les .java ---
dir /S /B src\*.java > sources.txt

REM --- Compiler avec le driver MySQL dans le classpath ---
javac -cp "src\lib\mysql-connector-j-9.5.0.jar" -d bin -encoding UTF-8 @sources.txt

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERREUR] La compilation a echoue !
    del sources.txt
    pause
    exit /b 1
)

del sources.txt
echo [OK] Compilation reussie !
echo.

echo [2/3] Creation du JAR executable...

REM --- Creer le manifest ---
echo Main-Class: Main> manifest.txt
echo Class-Path: lib/mysql-connector-j-9.5.0.jar>> manifest.txt

REM --- Creer le JAR ---
jar cfm dist\FindClass.jar manifest.txt -C bin .
del manifest.txt

REM --- Copier le driver dans dist/lib ---
mkdir dist\lib
copy src\lib\mysql-connector-j-9.5.0.jar dist\lib\ >nul

echo [OK] JAR cree: dist\FindClass.jar
echo.

echo [3/3] Creation du lanceur...

REM --- Creer le lanceur .bat dans dist ---
(
echo @echo off
echo title FindClass - Smart University Room Management
echo echo Demarrage de FindClass...
echo java -jar "%%~dp0FindClass.jar"
echo if %%ERRORLEVEL%% NEQ 0 ^(
echo     echo.
echo     echo [ERREUR] Verifiez que Java est installe et que MySQL est demarre.
echo     pause
echo ^)
) > dist\FindClass.bat

echo [OK] Lanceur cree: dist\FindClass.bat
echo.
echo ============================================
echo   Build termine avec succes !
echo.
echo   Pour lancer l'application:
echo     cd dist
echo     FindClass.bat
echo.
echo   Ou double-cliquez sur dist\FindClass.bat
echo ============================================
pause
