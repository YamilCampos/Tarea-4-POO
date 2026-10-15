#!/bin/sh
# Compila y ejecuta la consola (requiere JDK 17+)
set -e
mkdir -p out
javac -encoding UTF-8 -d out $(find src -name '*.java')
java -Dstdout.encoding=UTF-8 -cp out consola.ConsolaVideojuegos
