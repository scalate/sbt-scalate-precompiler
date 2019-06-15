#!/bin/bash
sbt \
    "project precompiler" \
    ++2.11.12 \
    clean \
    publishSigned \
    ++2.12.8 \
    clean \
    publishSigned \
    ++2.13.0 \
    clean \
    publishSigned \
    "project plugin" \
    clean \
    publishSigned
