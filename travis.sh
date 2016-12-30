#!/bin/bash
sbt ++2.12.1 \
    precompiler/test \
    precompiler/publishLocal \
    ++2.11.8 \
    precompiler/test \
    precompiler/publishLocal \
    ++2.10.6 \
    precompiler/test \
    precompiler/publishLocal \
    ++2.10.6 clean \
    plugin/publishLocal \
    plugin/scripted
