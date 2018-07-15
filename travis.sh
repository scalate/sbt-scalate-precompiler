#!/bin/bash

sbt ++2.12.6 \
    precompiler/test \
    precompiler/publishLocal \
    ++2.11.12 \
    precompiler/test \
    precompiler/publishLocal \
    clean \
    "project plugin" \
    ^publishLocal \
    ^scripted
