# Домашнее задание 6: Swagger и HATEOAS

## Задание
Добавление Swagger-документации и HATEOAS в API.

- Задокументировать существующее API (из задания 4) с помощью Swagger (Springdoc OpenAPI), чтобы можно было легко изучить и тестировать API через веб-интерфейс.
- Добавить поддержку HATEOAS, чтобы API предоставляло ссылки для навигации по ресурсам.


## Описание проекта
REST API для управления пользователями с поддержкой Spring Boot, Spring Data JPA, Swagger/OpenAPI документации и HATEOAS.

## Технологии
- Java 17
- Spring Boot 3.x
- Spring Data JPA
- H2 Database (или другая, в зависимости от настроек)
- Swagger/SpringDoc OpenAPI 2.1.0
- Spring HATEOAS

## Запуск приложения

### 1. Требования
- Java 17 или выше
- Maven
- IDE (IntelliJ IDEA, Eclipse, VS Code)

### 2. Настройка порта
Приложение использует порт **8081**

## Доступ к API
 Swagger UI (Документация и тестирование) После запуска откройте в браузере:

- Документация: http://localhost:8081/swagger-ui.html
- Спецификация OpenAPI (JSON): http://localhost:8081/v3/api-docs
- Спецификация OpenAPI (YAML): http://localhost:8081/v3/api-docs.yaml

## Основные endpoints API

|Метод|Endpoint|Описание|
|---|---|---|
|GET|/api/users|Получить всех пользователей|
|GET|/api/users/{id}|Получить пользователя по ID|
|GET|/api/users/email/{email}|Получить пользователя по email|
|POST|/api/users|Создать нового пользователя|
|PUT|/api/users/{id}|Обновить данные пользователя|
|DELETE|/api/users/{id}|Удалить пользователя|
|GET|/api/users/exists/{id}|Проверить существование пользователя|



