#!/bin/bash

if [ $# -ne 1 ]; then
  echo "Usage: $0 <file name>"
  exit 1
fi

jar_filename="$1"
screen_name="mathleague"
work_dir="./MATHLEAGUE"

cd ${work_dir}

screen_id=$(screen -ls | grep "${screen_name}" | awk '{print $1}')

if [ -z "${screen_id}" ]; then
  screen -S "${screen_name}" -d -m
  screen_id=$(screen -ls | grep "${screen_name}" | awk '{print $1}')
  screen -S "${screen_id}" -X stuff "${cd_command}"$'\015'
fi

screen -S "${screen_id}" -X stuff $'\003'  # Ctrl+C
sleep 5

java_command="java -jar ${jar_filename}"

screen -S "${screen_id}" -X stuff "${java_command}"$'\015'  # Enter

cd ..