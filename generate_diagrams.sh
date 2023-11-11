#!/bin/bash

input_directory="./diagrams/plantUmlFiles"

plantuml="./tools/plantuml.jar"

if [ $(which dot 2>/dev/null) ]; then
  echo "Found GraphViz"
else
  echo "Did not find GraphViz"
  sudo apt install graphviz
fi

for file in "$input_directory"/*.plantuml; do
    if [ -f "$file" ]; then
        java -jar "$plantuml" "$file" -o "../"
    fi
done
