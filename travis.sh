#!/bin/bash

sbt ++2.13.2 \
    precompiler/test \
    precompiler/publishLocal \
    ++2.12.11 \
    precompiler/test \
    precompiler/publishLocal \
    ++2.11.12 \
    precompiler/test \
    precompiler/publishLocal \
    clean \
    "project plugin" \
    ^publishLocal \
    ^scripted
