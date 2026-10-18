#!/usr/bin/env bash
set -e

JPACKAGE="/c/Program Files/Java/jdk-21.0.11/bin/jpackage.exe"

echo "#Criando imagem..."

"$JPACKAGE" \
  --type app-image \
  --name certwin \
  --input target/app \
  --main-jar certwin.jar \
  --dest builded/app-image \
  --app-version 1.0.0 \
  --vendor "Nicolas Basilio" \
  --description "Aplicativo de gerenciamento de certificador TLS para windows" \
  --win-console
  