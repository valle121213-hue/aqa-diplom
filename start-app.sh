#!/bin/bash

java -jar \
-Dspring.datasource.url=jdbc:mysql://localhost:3307/app \
-Dspring.datasource.username=app \
-Dspring.datasource.password=pass \
-Dspring.credit-gate.url=http://185.119.57.197:9999/credit \
-Dspring.payment-gate.url=http://185.119.57.197:9999/payment \
artifacts/aqa-shop.jar
