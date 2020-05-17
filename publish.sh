#!/bin/bash
sbt \
    "project precompiler" \
    ++2.11.12 \
    clean \
    publishSigned \
    ++2.12.11 \
    clean \
    publishSigned \
    ++2.13.2 \
    clean \
    publishSigned \
    "project plugin" \
    clean \
    publishSigned
