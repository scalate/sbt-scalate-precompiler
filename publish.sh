#!/bin/bash
sbt \
    "project precompiler" \
    ++2.11.x \
    clean \
    publishSigned \
    ++2.12.x \
    clean \
    publishSigned \
    ++2.13.x \
    clean \
    publishSigned \
    ++3.3.x \
    clean \
    publishSigned \
    "project plugin" \
    clean \
    publishSigned
