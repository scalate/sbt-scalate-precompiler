#!/bin/bash
sbt \
    "project precompiler" \
    clean \
    publishSigned \
    "project plugin" \
    clean \
    publishSigned
