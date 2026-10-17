#!/usr/bin/env bash
set -e

INNO="/c/Users/[USER]/AppData/Local/Programs/Inno Setup 7/ISCC.exe"
# O caminho de instalação do Inno pode ser diferente.

rm -fr builded

echo "1 - Preparando imagem."

echo "#Compilando..."
mvn clean package
mkdir -p target/app
cp target/certwin.jar target/app/certwin.jar

./build/build-image.sh

echo "2 - Gerando o instalador/setup."
"$INNO" ./build/build-setup.iss

echo "#Processo concluido"
