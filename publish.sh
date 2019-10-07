#!/bin/bash
sbt \
    "project precompiler" \
    ++2.11.12 \
    clean \
    publishSigned \
    ++2.12.10 \
    clean \
    publishSigned \
    ++2.13.1 \
    clean \
    publishSigned \
    "project plugin" \
    clean \
    publishSigned
