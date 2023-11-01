#!/bin/bash

cd ./tmp/tpe2-g4-client-2023.2Q/ && ./query1.sh -Daddresses=\'192.168.1.38:5701\' -DinPath="$PWD/../csv" -DoutPath="$PWD/../.." $*