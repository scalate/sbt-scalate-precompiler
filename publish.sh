#!/bin/bash
sbt \
    "project precompiler" \
    ++2.11.12 \
    clean \
    publishSigned \
    ++2.12.16 \
    clean \
    publishSigned \
    ++2.13.9 \
    clean \
    publishSigned \
    "project plugin" \
    clean \
    publishSigned
