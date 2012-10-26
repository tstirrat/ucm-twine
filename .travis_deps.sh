#!/bin/sh

FILE=deps.zip
wget -q http://stirrat.org/$FILE

openssl aes-256-cbc -d -in $FILE -out deps.tar.gz -pass env:DEC

mkdir ucm-twine-deps
tar -zxf deps.tar.gz -C ucm-twine-deps
ucm-twine-deps/install.sh

rm -f $FILE
rm -f deps.tar.gz
rm -rf ucm-twine-deps/
