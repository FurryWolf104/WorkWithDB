package org.example.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        // Создаем сервер - указываем, где работает наше API
        Server server = new Server();
        server.setUrl("http://localhost:8081");
        server.setDescription("Локальный сервер разработки");

        // Создаем контактную информацию
        Contact contact = new Contact();
        contact.setName("Dmitriy");
        contact.setEmail("somemail@mail.ru");

        // Создаем лицензию
        License license = new License();
        license.setName("Apache 2.0");
        license.setUrl("http://springdoc.org");

        // Создаем основную информацию об API
        Info info = new Info()
                .title("User Management API")
                .version("1.0.0")
                .description("""
                        ## REST API для управления пользователями
                        
                        Этот API предоставляет полный набор операций CRUD для управления пользователями:
                        - **Создание** новых пользователей
                        - **Получение** информации о пользователях (по ID, email или всех)
                        - **Обновление** данных существующих пользователей
                        - **Удаление** пользователей
                        
                        ### Основные возможности:
                        - Валидация данных (имя, email, возраст)
                        - Проверка уникальности email
                        - Полная документация через Swagger UI
                        """)
                .contact(contact)    // Контактная информация
                .license(license);   // Информация о лицензии

        // Собираем все вместе и возвращаем
        return new OpenAPI()
                .info(info)          // Устанавливаем информацию
                .servers(List.of(server));  // Устанавливаем список серверов
    }

}

