package org.example.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.UserRequest;
import org.example.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.concurrent.atomic.AtomicLong;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
@DisplayName("Интеграционные тесты REST API UserController")
public class UserControllerTest {

    @Container
    private static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;
    private static final AtomicLong emailCounter = new AtomicLong(1);

    @BeforeEach
    void setUp() {
        // Очищаем базу перед каждым тестом
        userRepository.deleteAll();
    }

    private String getUniqueEmail() {
        return "test" + emailCounter.getAndIncrement() + System.currentTimeMillis() + "@example.com";
    }

    // Вспомогательный метод для извлечения ID из JSON ответа
    private Long extractUserIdFromJson(String json) throws Exception {
        JsonNode node = objectMapper.readTree(json);
        return node.get("id").asLong();
    }

    // ==================== CREATE TESTS ====================

    @Test
    @DisplayName("POST /api/users - Успешное создание пользователя с валидными данными")
    void createUser_ShouldReturn201AndUserResponse_WhenValidRequest() throws Exception {
        // Arrange
        String uniqueEmail = getUniqueEmail();
        UserRequest userRequest = new UserRequest("Иван Иванов", uniqueEmail, 30);

        // Act
        ResultActions result = mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequest)));

        // Assert
        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Иван Иванов"))
                .andExpect(jsonPath("$.email").value(uniqueEmail))
                .andExpect(jsonPath("$.age").value(30))
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    @DisplayName("POST /api/users - Ошибка 400 при создании пользователя с уже существующим email")
    void createUser_ShouldReturn400_WhenEmailAlreadyExists() throws Exception {
        // Arrange
        String duplicateEmail = getUniqueEmail();

        // Создаем первого пользователя
        UserRequest firstUser = new UserRequest("Первый", duplicateEmail, 25);
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(firstUser)));

        // Пытаемся создать второго с таким же email
        UserRequest secondUser = new UserRequest("Второй", duplicateEmail, 30);

        // Act & Assert
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(secondUser)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Пользователь с email " + duplicateEmail + " уже существует"));
    }

    @Test
    @DisplayName("POST /api/users - Ошибка 400 при создании пользователя с некорректным email")
    void createUser_ShouldReturn400_WhenEmailIsInvalid() throws Exception {
        // Arrange
        UserRequest invalidRequest = new UserRequest("Тест", "неправильный-email", 30);

        // Act & Assert
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/users - Ошибка 400 при создании пользователя с отрицательным возрастом")
    void createUser_ShouldReturn400_WhenAgeIsNegative() throws Exception {
        // Arrange
        String uniqueEmail = getUniqueEmail();
        UserRequest invalidRequest = new UserRequest("Тест", uniqueEmail, -5);

        // Act & Assert
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    // ==================== READ TESTS ====================

    @Test
    @DisplayName("GET /api/users - Успешное получение всех пользователей (пустой список)")
    void getAllUsers_ShouldReturn200AndEmptyList_WhenNoUsersExist() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @DisplayName("GET /api/users - Успешное получение всех пользователей (несколько записей)")
    void getAllUsers_ShouldReturn200AndUserList_WhenUsersExist() throws Exception {
        // Arrange
        String email1 = getUniqueEmail();
        String email2 = getUniqueEmail();

        UserRequest user1 = new UserRequest("Анна", email1, 25);
        UserRequest user2 = new UserRequest("Борис", email2, 30);

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user1)));

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user2)));

        // Act & Assert
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].email").exists())
                .andExpect(jsonPath("$[1].email").exists());
    }

    @Test
    @DisplayName("GET /api/users/{id} - Успешное получение пользователя по существующему ID")
    void getUserById_ShouldReturn200AndUser_WhenUserExists() throws Exception {
        String uniqueEmail = getUniqueEmail();
        UserRequest userRequest = new UserRequest("Тестовый", uniqueEmail, 25);

        // Создаем пользователя
        String responseJson = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long userId = extractUserIdFromJson(responseJson);

        // Act & Assert
        mockMvc.perform(get("/api/users/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.name").value("Тестовый"))
                .andExpect(jsonPath("$.email").value(uniqueEmail))
                .andExpect(jsonPath("$.age").value(25));
    }

    @Test
    @DisplayName("GET /api/users/{id} - Ошибка 400 при получении пользователя по несуществующему ID")
    void getUserById_ShouldReturn400_WhenUserNotExists() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/users/999999"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Пользователь с ID 999999 не найден"));
    }

    @Test
    @DisplayName("GET /api/users/email/{email} - Успешное получение пользователя по email")
    void getUserByEmail_ShouldReturn200AndUser_WhenEmailExists() throws Exception {
        // Arrange
        String uniqueEmail = getUniqueEmail();
        UserRequest userRequest = new UserRequest("По Email", uniqueEmail, 35);

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequest)));

        // Act & Assert
        mockMvc.perform(get("/api/users/email/" + uniqueEmail))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(uniqueEmail))
                .andExpect(jsonPath("$.name").value("По Email"));
    }

    // ==================== UPDATE TESTS ====================

    @Test
    @DisplayName("PUT /api/users/{id} - Успешное обновление пользователя")
    void updateUser_ShouldReturn200AndUpdatedUser_WhenValidRequest() throws Exception {
        // Arrange
        String oldEmail = getUniqueEmail();
        UserRequest createRequest = new UserRequest("Старое Имя", oldEmail, 25);

        // Создаем пользователя
        String responseJson = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long userId = extractUserIdFromJson(responseJson);

        // Обновляем пользователя
        String newEmail = getUniqueEmail();
        UserRequest updateRequest = new UserRequest("Новое Имя", newEmail, 30);

        // Act & Assert
        mockMvc.perform(put("/api/users/" + userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.name").value("Новое Имя"))
                .andExpect(jsonPath("$.email").value(newEmail))
                .andExpect(jsonPath("$.age").value(30));

    }

    @Test
    @DisplayName("PUT /api/users/{id} - Ошибка 400 при обновлении несуществующего пользователя")
    void updateUser_ShouldReturn400_WhenUserNotExists() throws Exception {
        // Arrange
        String uniqueEmail = getUniqueEmail();
        UserRequest updateRequest = new UserRequest("Не важно", uniqueEmail, 30);

        // Act & Assert
        mockMvc.perform(put("/api/users/999999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Пользователь с ID 999999 не найден"));
    }

    @Test
    @DisplayName("PUT /api/users/{id} - Ошибка 400 при обновлении email на уже существующий")
    void updateUser_ShouldReturn400_WhenEmailAlreadyExists() throws Exception {
        // Arrange
        String email1 = getUniqueEmail();
        String email2 = getUniqueEmail();

        // Создаем первого пользователя
        UserRequest user1 = new UserRequest("Первый", email1, 25);
        String response1 = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user1)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long userId1 = extractUserIdFromJson(response1);

        // Создаем второго пользователя
        UserRequest user2 = new UserRequest("Второй", email2, 30);
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user2)))
                .andExpect(status().isCreated());

        // Пытаемся обновить первого пользователя email'ом второго
        UserRequest updateRequest = new UserRequest("Первый", email2, 25);

        // Act & Assert
        mockMvc.perform(put("/api/users/" + userId1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Пользователь с email " + email2 + " уже существует"));
    }

    // ==================== DELETE TESTS ====================

    @Test
    @DisplayName("DELETE /api/users/{id} - Успешное удаление пользователя")
    void deleteUser_ShouldReturn204_WhenUserExists() throws Exception {
        // Arrange
        String uniqueEmail = getUniqueEmail();
        UserRequest userRequest = new UserRequest("Удаляемый", uniqueEmail, 40);

        // Создаем пользователя
        String responseJson = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long userId = extractUserIdFromJson(responseJson);

        // Act & Assert - удаляем пользователя
        mockMvc.perform(delete("/api/users/" + userId))
                .andExpect(status().isNoContent());

        // Проверяем, что пользователь действительно удален
        mockMvc.perform(get("/api/users/" + userId))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("DELETE /api/users/{id} - Ошибка 400 при удалении несуществующего пользователя")
    void deleteUser_ShouldReturn400_WhenUserNotExists() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/users/999999"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Пользователь с ID 999999 не найден"));
    }

    // ==================== UTILITY TESTS ====================

    @Test
    @DisplayName("GET /api/users/exists/{id} - Проверка существования пользователя: true для существующего")
    void userExists_ShouldReturnTrue_WhenUserExists() throws Exception {
        // Arrange
        String uniqueEmail = getUniqueEmail();
        UserRequest userRequest = new UserRequest("Для проверки", uniqueEmail, 50);

        // Создаем пользователя
        String responseJson = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long userId = extractUserIdFromJson(responseJson);

        // Act & Assert
        mockMvc.perform(get("/api/users/exists/" + userId))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    @DisplayName("GET /api/users/exists/{id} - Проверка существования пользователя: false для несуществующего")
    void userExists_ShouldReturnFalse_WhenUserNotExists() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/users/exists/999999"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    // ==================== BOUNDARY/EDGE CASES ====================

    @Test
    @DisplayName("POST /api/users - Создание пользователя с минимальным валидным возрастом (0)")
    void createUser_ShouldReturn201_WhenAgeIsMinimumValid() throws Exception {
        // Arrange
        String uniqueEmail = getUniqueEmail();
        UserRequest userRequest = new UserRequest("Минимальный возраст", uniqueEmail, 0);

        // Act & Assert
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.age").value(0));
    }

    @Test
    @DisplayName("POST /api/users - Создание пользователя с максимальным валидным возрастом (150)")
    void createUser_ShouldReturn201_WhenAgeIsMaximumValid() throws Exception {
        // Arrange
        String uniqueEmail = getUniqueEmail();
        UserRequest userRequest = new UserRequest("Максимальный возраст", uniqueEmail, 150);

        // Act & Assert
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.age").value(150));
    }

    @Test
    @DisplayName("POST /api/users - Ошибка 400 при создании пользователя без имени (пустая строка)")
    void createUser_ShouldReturn400_WhenNameIsEmpty() throws Exception {
        // Arrange
        String uniqueEmail = getUniqueEmail();
        UserRequest invalidRequest = new UserRequest("", uniqueEmail, 30);

        // Act & Assert
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/users - Ошибка 400 при создании пользователя с слишком длинным именем")
    void createUser_ShouldReturn400_WhenNameIsTooLong() throws Exception {
        // Arrange
        String uniqueEmail = getUniqueEmail();
        String longName = "Очень очень очень очень очень очень очень очень очень очень длинное имя";
        UserRequest invalidRequest = new UserRequest(longName, uniqueEmail, 30);

        // Act & Assert
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}