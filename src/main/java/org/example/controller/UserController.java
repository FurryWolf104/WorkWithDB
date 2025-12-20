package org.example.controller;

import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.example.dto.UserRequest;
import org.example.dto.UserResponse;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.example.service.UserService;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User Controller", description = "API для управления пользователями с поддержкой HATEOAS")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // CREATE

    @Operation(
            summary = "Создать нового пользователя",
            description = "Создает нового пользователя с указанными данными"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Пользователь успешно создан",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Некорректные данные пользователя (валидация не пройдена)",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Пример ошибки валидации",
                                    value = """
                {
                  "name": "Имя не может быть пустым",
                  "email": "Некорректный формат email",
                  "age": "Возраст должен быть от 0 до 150"
                }
                """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Пользователь с таким email уже существует",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Пример конфликта email",
                                    value = """
                {
                  "error": "Bad Request",
                  "message": "Пользователь с email alice@example.com уже существует"
                }
                """
                            )
                    )
            )
    })

    @PostMapping
    public ResponseEntity<EntityModel<UserResponse>> createUser(
            @Parameter(
                    description = "Данные пользователя для создания",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserRequest.class)
                    )
            )  // Описание параметра
            @Valid @RequestBody UserRequest userRequest) {
        EntityModel<UserResponse> createdUser = userService.createUser(userRequest);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    // READ ALL
    @Operation(
            summary = "Получить всех пользователей",
            description = "Возвращает список всех пользователей в системе"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Список пользователей успешно получен",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UserResponse.class)
            )
    )
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<UserResponse>>> getAllUsers() {
        CollectionModel<EntityModel<UserResponse>> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }


    // READ BY ID
    @Operation(
            summary = "Получить пользователя по ID",
            description = "Возвращает пользователя по указанному идентификатору"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Пользователь найден",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Пользователь с указанным ID не найден",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Пример ошибки 404",
                                    value = """
                {
                  "error": "Bad Request",
                  "message": "Пользователь с ID 999 не найден"
                }
                """
                            )
                    )
            )
    })

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<UserResponse>> getUserById(
            @Parameter(
            description = "ID пользователя",
            required = true,
            example = "1"
            )
            @PathVariable Long id) {
        EntityModel<UserResponse> user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    // READ BY EMAIL
    @Operation(
            summary = "Получить пользователя по email",
            description = "Возвращает пользователя по указанному email адресу"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Пользователь найден",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Пользователь с указанным email не найден"
            )
    })
    @GetMapping("/email/{email}")
    public ResponseEntity<EntityModel<UserResponse>> getUserByEmail(
            @Parameter(
                    description = "Email пользователя",
                    required = true,
                    example = "user@example.com"
            )
            @PathVariable String email) {
        EntityModel<UserResponse> user = userService.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }

    // UPDATE
    @Operation(
            summary = "Обновить данные пользователя",
            description = "Обновляет данные существующего пользователя по ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Данные пользователя успешно обновлены",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Некорректные данные пользователя",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Пример ошибки валидации",
                                    value = """
                {
                  "name": "Имя должно содержать от 2 до 50 символов",
                  "email": "Некорректный формат email"
                }
                """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Пользователь с указанным ID не найден",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Пример ошибки 404",
                                    value = """
                {
                  "error": "Bad Request",
                  "message": "Пользователь с ID 999 не найден"
                }
                """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Пользователь с таким email уже существует",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Пример конфликта email",
                                    value = """
                {
                  "error": "Bad Request",
                  "message": "Пользователь с email test@example.com уже существует"
                }
                """
                            )
                    )
            )
    })

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<UserResponse>> updateUser(
            @Parameter(
                    description = "ID пользователя для обновления",
                    required = true,
                    example = "1"
            )
            @PathVariable Long id,
            @Parameter(
                    description = "Новые данные пользователя",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserRequest.class)
                    )
            )
            @Valid @RequestBody UserRequest userRequest
    ) {
        EntityModel<UserResponse> updatedUser = userService.updateUser(id, userRequest);
        return ResponseEntity.ok(updatedUser);
    }

    // DELETE
    @Operation(
            summary = "Удалить пользователя",
            description = "Удаляет пользователя по указанному ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Пользователь успешно удален"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Пользователь с указанным ID не найден",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Пример ошибки 404",
                                    value = """
                {
                  "error": "Bad Request",
                  "message": "Пользователь с ID 999 не найден"
                }
                """
                            )
                    )
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @Parameter(
                    description = "ID пользователя для проверки",
                    required = true,
                    example = "1"
            )
            @PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    // CHECK EXISTS
    @GetMapping("/exists/{id}")
    public ResponseEntity<Boolean> userExists(@PathVariable Long id) {
        boolean exists = userService.userExists(id);
        return ResponseEntity.ok(exists);
    }
}
