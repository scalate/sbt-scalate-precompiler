#!/bin/bash

sbt ++2.13.0-M5 \
    precompiler/test \
    precompiler/publishLocal \
    ++2.12.7 \
    precompiler/test \
    precompiler/publishLocal \
    ++2.11.12 \
    precompiler/test \
    precompiler/publishLocal \
    clean \
    "project plugin" \
    ^publishLocal \
    ^scripted
