#!/bin/bash

echo $1
dir="$(dirname $1)"
dest=`basename $1 .png`
echo to $dir/${dest}.webp

cwebp -mt -q 90 "$1" -o "$dir/${dest}.webp"
