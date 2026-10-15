@echo off
REM Compila y ejecuta la consola (requiere JDK 17+)
chcp 65001 >nul
if not exist out mkdir out
dir /s /b src\*.java > fuentes.txt
javac -encoding UTF-8 -d out @fuentes.txt
del fuentes.txt
java -Dstdout.encoding=UTF-8 -cp out consola.ConsolaVideojuegos
