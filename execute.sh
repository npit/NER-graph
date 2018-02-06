#!/usr/bin/env bash
echo "$(find $(pwd) -iname '*.jar' | tr '\n' ':')" > cpath 
echo "$(find ~/.m2/ -iname '*.jar' | tr '\n' ':')" >> cpath 

java -cp "$(cat cpath)" TextComparator config.properties
