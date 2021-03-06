#!/bin/bash -e
export INSTALL_PATH="/applications/yellowfin"
APPSERVER_DIR="$INSTALL_PATH/appserver"

if [ ! -d "$APPSERVER_DIR" ] ; then
  echo "Yellowfin installation not found!"
  echo "  proceeding with automatic installation..."

  java -jar "$YELLOWFIN_JAR" -silent install.properties

  if [ -d "$APPSERVER_DIR" ] ; then
    echo "Installation success!"
  else
    echo "Installation failed!"
    exit 1
  fi
fi

echo "Starting Yellowfin..."
"$APPSERVER_DIR/bin/catalina.sh" run
