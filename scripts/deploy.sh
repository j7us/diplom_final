#!/bin/bash
set -e

echo "Запуск мавен"

mvn clean package -DskipTests

echo "Ищу докер файл"

cd ./mvc

docker-compose up --build -d --remove-orphans

echo "Успешно собрано"