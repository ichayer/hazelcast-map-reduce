#!/bin/bash

cd ./tmp/tpe2-g4-client-2023.2Q/ && ./query2.sh -Daddresses=\'127.0.0.1:5701\' -DinPath="$PWD/../csv" -DoutPath="$PWD/../.." -Dn=4 $*