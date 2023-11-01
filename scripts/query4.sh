#!/bin/bash

cd ./tmp/tpe2-g4-client-2023.2Q/ && ./query4.sh -Daddresses=\'192.168.1.38:5701\' -DinPath="$PWD/../csv" -DoutPath="$PWD/../.." -DstartDate=01/05/2021 -DendDate=31/05/2021 $*