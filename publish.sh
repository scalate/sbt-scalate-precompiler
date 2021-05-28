#!/bin/bash
sbt \
    "project precompiler" \
    ++2.11.12 \
    clean \
    publishSigned \
    ++2.12.14 \
    clean \
    publishSigned \
    ++2.13.6 \
    clean \
    publishSigned \
    "project plugin" \
    clean \
    publishSigned
