#!/bin/bash


cd ..

#----------------ENVIROMENT VARIABLES!!----------------#
server=$STAS_SERVER
passphrase=$STAS_PASSPHRASE

#----------------FILE NAMES----------------#
key_path="$STAS_SSH"
libs_path="./build/libs/"


#----------------Gradle path----------------#
GRADLE_PATH=./gradlew
BUILD_TASK=build


if [ ! -f "$GRADLE_PATH" ]; then
    echo "ERROR: Gradle not found"
    exit 1
fi

$GRADLE_PATH $BUILD_TASK

jar_file=$(find $libs_path -name "*.jar" -type f -exec basename {} \; -quit)
filename="$libs_path$jar_file"

if [ -n "$jar_file" ]; then
  echo "OK, jar has been created"
else
  echo "ERROR: can't find jar file in path $libs_path"
  exit 1
fi

expect -c "
spawn scp -i $key_path $filename $server:~/MATHLEAGUE
expect \"Enter passphrase for key '$key_path':\"
send \"$passphrase\r\"
expect eof"