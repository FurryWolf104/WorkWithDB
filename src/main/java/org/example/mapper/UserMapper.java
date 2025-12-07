package org.example.mapper;

import org.example.dto.UserRequest;
import org.example.dto.UserResponse;
import org.example.entity.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

//Компонент для преобразования между Entity и DTO.
@Component // Аннотация, чтобы Spring мог внедрять этот маппер
public class UserMapper {
    /**
     * Преобразует UserRequest (DTO) в User (Entity) для создания нового пользователя.
     * Устанавливает текущее время в createdAt.
     */
    public User toEntity(UserRequest request) {
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setAge(request.getAge());
        user.setCreatedAt(LocalDateTime.now()); // Устанавливаем текущее время
        return user;
    }

    /**
     * Преобразует UserRequest (DTO) в User (Entity) для обновления существующего пользователя.
     * Не трогаем id и createdAt.
     */
    public User toEntity(UserRequest request, Long id) {
        User user = toEntity(request);
        user.setId(id); // Устанавливаем ID для обновления
        return user;
    }
    /**
     * Преобразует User (Entity) в UserResponse (DTO) для ответа API.
     */
    public UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getAge(),
                user.getCreatedAt()
        );
    }



}
