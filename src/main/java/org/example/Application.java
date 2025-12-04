package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Главный класс Spring Boot приложения.
 *
 * Аннотация @SpringBootApplication включает в себя:
 * 1. @Configuration - отмечает класс как источник конфигурации бинов
 * 2. @EnableAutoConfiguration - включает автоконфигурацию Spring Boot
 * 3. @ComponentScan - сканирует текущий пакет и подпакеты на наличие компонентов
 *
 * При запуске этого класса Spring Boot:
 * 1. Запустит встроенный веб-сервер (Tomcat по умолчанию)
 * 2. Настроит всё автоматически на основе зависимостей в pom.xml
 * 3. Сканирует и регистрирует все компоненты (@Service, @Repository, @Controller)
 */
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        // Запускаем Spring Boot приложение
        SpringApplication.run(Application.class, args);

    }
}