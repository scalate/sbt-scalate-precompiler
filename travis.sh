#!/bin/bash
sbt ++2.12.3 \
    precompiler/test \
    precompiler/publishLocal \
    ++2.11.11 \
    precompiler/test \
    precompiler/publishLocal \
    ++2.10.6 \
    precompiler/test \
    precompiler/publishLocal \
    clean \
    `project plugin` \
    ^publishLocal \
    ^scripted
