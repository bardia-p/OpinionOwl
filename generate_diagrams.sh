#!/bin/bash

input_directory="./diagrams/plantUmlFiles"

plantuml="./tools/plantuml.jar"

for file in "$input_directory"/*.plantuml; do
    if [ -f "$file" ]; then
        java -jar "$plantuml" "$file" -o "../"
    fi
done
