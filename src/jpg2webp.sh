#!/bin/bash

echo $1
dir="$(dirname $1)"
dest=`basename $1 .jpg`
echo to $dir/${dest}.webp

cwebp -mt -q 90 "$1" -o "$dir/${dest}.webp"
