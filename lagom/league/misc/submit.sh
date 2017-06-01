#!/bin/bash

while read p; do
    echo $p | http POST :9000/league/bundesliga/game
done < leaguedata.json

