#!/bin/sh

export PINGERLOD_PROJECT_HOME=/afs/slac/package/pinger/lod/PingERLOD/
export PINGERLOD_LIB=${PINGERLOD_PROJECT_HOME}lib
export PINGERLOD_SRC=${PINGERLOD_PROJECT_HOME}src
export PINGERLOD_BIN=${PINGERLOD_PROJECT_HOME}bin

export PINGERLOD_CP=$PINGERLOD_LIB/*:$PINGERLOD_LIB/jena/*:$PINGERLOD_LIB/sesame/*
export PINGERLOD_MAINCLASS=$PINGERLOD_SRC/edu/stanford/slac/tests/t.java
export PINGERLOD_MAINCOMPILEDCLASS="edu.stanford.slac.tests.t"

echo $PINGERLOD_PROJECT_HOME

java -cp $PINGERLOD_BIN:$PINGERLOD_CP $PINGERLOD_MAINCOMPILEDCLASS 