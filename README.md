# Микросервисная система уведомлений

Проект демонстрирует архитектуру на основе событий (Event-Driven Architecture) с использованием двух микросервисов, взаимодействующих через Apache Kafka.

## Описание проекта

Система состоит из двух независимых микросервисов:

1. **user-service (WorkWithDB)** (порт 8081) — CRUD API для управления пользователями. При создании/удалении пользователя отправляет события в Kafka.
2. **notification-service** (порт 8082) — Получает события из Kafka и отправляет email-уведомления. Также предоставляет REST API для прямой отправки email.

## Архитектура:
- **Java 17** (LTS)
- **Spring Boot 3.4.12**
- **Apache Kafka 3.7.0** 
- **Spring Kafka** 
- **Spring Mail** 
- **PostgreSQL 17**
- **Fake SMTP Server 2.1.0** (тестовый SMTP сервер)
- **Maven** (сборка)

## Требования:
1. **Java 17+**
2. **Apache Kafka 3.7.0**
3. **Podman/Docker** (для Fake SMTP) (У меня работает через podman с настройкой Docker compatibility - Enabled)
4. **Postman** или другой REST клиент для тестирования

## Для работы приложения нужно запустить:

# Терминал 1: Запуск ZooKeeper
cd [Путь]\kafka
.\bin\windows\zookeeper-server-start.bat .\config\zookeeper.properties

# Терминал 2: Запуск Kafka
cd [Путь]\kafka
.\bin\windows\kafka-server-start.bat .\config\server.properties

# Терминал 3: Создание топика (если не создан)
cd [Путь]\kafka
.\bin\windows\kafka-topics.bat --create --topic user-events-topic --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1

# Запуск Fake SMTP через Podman/Docker
podman run -d -p 2525:8025 -p 8085:8080 --name fake-smtp gessnerfl/fake-smtp-server:2.1.0
- Веб-интерфейс Fake SMTP: http://localhost:8085
- SMTP порт: localhost:2525

# API Endpoints
**User Service**

Метод       Endpoint                    Описание
POST	    /api/users             	    Создать пользователя
GET	        /api/users             	    Получить всех пользователей
GET	        /api/users/{id}      	    Получить пользователя по ID
PUT 	    /api/users/{id}      	    Обновить пользователя
DELETE	    /api/users/{id}      	    Удалить пользователя
GET	        /api/users/exists/{id}	    Проверить существование пользователя

**Notification Service**
Метод	    Endpoint	                            Описание
POST	    /api/notifications/email	            Отправить произвольный email
POST	    /api/notifications/user-created	        Отправить уведомление о создании пользователя
POST	    /api/notifications/user-deleted	        Отправить уведомление об удалении пользователя