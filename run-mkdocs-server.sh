#!/bin/bash

docker run --rm -it -p 8000:8000 -v ${PWD}:/docs ghcr.io/ikor-gmbh/sip-framework/mkdocs-server:v1.0.0
