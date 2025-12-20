package org.example.dto;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;
/**
 * DTO для входящих запросов: создания и обновления пользователя.
 * Используется в POST /api/users и PUT /api/users/{id}
 */
public class UserRequest {

    @Schema(
            description = "Имя пользователя",
            example = "Иван Иванов",
            requiredMode = Schema.RequiredMode.REQUIRED,
            minLength = 2,
            maxLength = 50
    )
    @NotBlank(message = "Имя не может быть пустым")
    @Size(min = 2, max = 50, message = "Имя должно содержать от 2 до 50 символов")
    private String name;

    @Schema(
            description = "Email пользователя",
            example = "ivan@example.com",
            requiredMode = Schema.RequiredMode.REQUIRED,
            format = "email"
    )
    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Некорректный формат email")
    private String email;

    @Schema(
            description = "Возраст пользователя",
            example = "25",
            requiredMode = Schema.RequiredMode.REQUIRED,
            minimum = "0",
            maximum = "150"
    )
    @NotNull(message = "Возраст не может быть null")
    @Min(value = 0, message = "Возраст не может быть отрицательным")
    @Max(value = 150, message = "Возраст не может превышать 150 лет")
    private Integer age;

    // Конструкторы
    public UserRequest() {
    }

    public UserRequest(String name, String email, Integer age) {
        this.name = name;
        this.email = email;
        this.age = age;
    }

    // Геттеры и сеттеры
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }


}
