#!/bin/bash

sbt ++2.13.1 \
    precompiler/test \
    precompiler/publishLocal \
    ++2.12.10 \
    precompiler/test \
    precompiler/publishLocal \
    ++2.11.12 \
    precompiler/test \
    precompiler/publishLocal \
    clean \
    "project plugin" \
    ^publishLocal \
    ^scripted
