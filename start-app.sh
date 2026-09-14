#!/bin/bash

java -jar \
-Dspring.credit-gate.url=http://localhost:9090 \
-Dspring.payment-gate.url=http://localhost:9090 \
artifacts/aqa-shop.jar