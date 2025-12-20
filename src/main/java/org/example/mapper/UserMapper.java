package org.example.mapper;

import org.example.dto.UserRequest;
import org.example.dto.UserResponse;
import org.example.entity.User;
import org.springframework.stereotype.Component;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

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
    //Создание EntityModel с HATEOAS ссылками для одного пользователя
    public EntityModel<UserResponse> toEntityModel(User user) {
        // 1. Создаем обычный UserResponse
        UserResponse userResponse = toResponse(user);

        // 2. Создаем EntityModel (ресурс со ссылками)
        EntityModel<UserResponse> resource = EntityModel.of(userResponse);

        // 3. Добавляем ссылки
        // Ссылка на самого себя
        resource.add(linkTo(methodOn(org.example.controller.UserController.class)
                .getUserById(user.getId())).withSelfRel());

        // Ссылка на обновление
        resource.add(linkTo(methodOn(org.example.controller.UserController.class)
                .updateUser(user.getId(), null)).withRel("update"));

        // Ссылка на удаление
        resource.add(linkTo(methodOn(org.example.controller.UserController.class)
                .deleteUser(user.getId())).withRel("delete"));

        // Ссылка на список всех пользователей
        resource.add(linkTo(methodOn(org.example.controller.UserController.class)
                .getAllUsers()).withRel("users"));

        return resource;
    }

    //Создание CollectionModel с HATEOAS ссылками для списка пользователей
    public CollectionModel<EntityModel<UserResponse>> toCollectionModel(List<User> users) {
        // Преобразуем каждого пользователя в EntityModel
        List<EntityModel<UserResponse>> userResources = users.stream()
                .map(this::toEntityModel)
                .collect(Collectors.toList());

        // Создаем CollectionModel из списка ресурсов
        CollectionModel<EntityModel<UserResponse>> resources = CollectionModel.of(userResources);

        // Добавляем ссылку на сам список (self)
        resources.add(linkTo(methodOn(org.example.controller.UserController.class)
                .getAllUsers()).withSelfRel());

        // Добавляем ссылку на создание нового пользователя
        resources.add(linkTo(methodOn(org.example.controller.UserController.class)
                .createUser(null)).withRel("create"));

        return resources;
    }



}
