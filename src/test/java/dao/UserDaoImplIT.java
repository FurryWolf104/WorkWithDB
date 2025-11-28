package dao;

import entity.User;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import util.HibernateUtil;

import java.time.LocalDateTime;
import java.util.List;

//import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertFalse;

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS) // Чтобы @BeforeAll был не статическим
public class UserDaoImplIT {
    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test")
            .withReuse(true); // Переиспользование

    private UserDao userDao;
    private User testUser;

    @BeforeAll
    void setup() {
        // Настройки Hibernate для использования тестовой БД
        System.setProperty("hibernate.connection.url", postgres.getJdbcUrl());
        System.setProperty("hibernate.connection.username", postgres.getUsername());
        System.setProperty("hibernate.connection.password", postgres.getPassword());
        System.setProperty("hibernate.hbm2ddl.auto", "create"); // Создаем таблицы заново

        userDao = new UserDaoImpl();
    }

    @BeforeEach
    void init() {
        testUser = new User("Test User", "test@example.com", 25);
        testUser.setCreatedAt(LocalDateTime.now());
    }


    @AfterEach
    void cleanup() {
        // Очищаем базу данных после каждого теста. Это нужно для изоляции каждого теста
        try (var session = HibernateUtil.getSessionFactory().openSession()) {
            var transaction = session.beginTransaction();
            session.createQuery("DELETE FROM User").executeUpdate();
            transaction.commit();
        } catch (Exception e) {
            System.err.println("Ошибка при очистке БД: " + e.getMessage());
        }

    }
    @AfterAll
    static void tearDown() {
        HibernateUtil.shutdown();
    }

    @Test
    @DisplayName("Сохранение пользователя - успех")
    void save_ShouldSaveUser_WhenValidUser() {
        // Act
        Long userId = userDao.save(testUser);

        // Assert
        assertNotNull(userId);
        assertTrue(userId > 0);
    }

    @Test
    @DisplayName("Поиск по ID - пользователь существует")
    void findById_ShouldReturnUser_WhenUserExists() {
        // Arrange
        Long userId = userDao.save(testUser);

        // Act
        User foundUser = userDao.findById(userId);

        // Assert
        assertNotNull(foundUser);
        assertEquals(userId, foundUser.getId());
        assertEquals(testUser.getEmail(), foundUser.getEmail());
        assertEquals(testUser.getName(), foundUser.getName());
    }

    @Test
    @DisplayName("Поиск по ID - пользователь не существует")
    void findById_ShouldReturnNull_WhenUserNotExists() {
        // Act
        User foundUser = userDao.findById(999L);

        // Assert
        assertNull(foundUser);
    }

    @Test
    @DisplayName("Поиск по email - email существует")
    void findByEmail_ShouldReturnUser_WhenEmailExists() {
        // Arrange
        userDao.save(testUser);

        // Act
        User foundUser = userDao.findByEmail("test@example.com");

        // Assert
        assertNotNull(foundUser);
        assertEquals(testUser.getEmail(), foundUser.getEmail());
    }

    @Test
    @DisplayName("Поиск по email - email не существует")
    void findByEmail_ShouldReturnNull_WhenEmailNotExists() {
        // Act
        User foundUser = userDao.findByEmail("nonexistent@example.com");

        // Assert
        assertNull(foundUser);
    }

    @Test
    @DisplayName("Получение всех пользователей - успех")
    void findAll_ShouldReturnAllUsers() {
        // Arrange
        userDao.save(testUser);
        User anotherUser = new User("Another User", "another@example.com", 30);
        userDao.save(anotherUser);

        // Act
        List<User> users = userDao.findAll();

        // Assert
        assertNotNull(users);
        assertEquals(2, users.size());
    }

    @Test
    @DisplayName("Обновление пользователя - успех")
    void update_ShouldUpdateUser_WhenUserExists() {
        // Arrange
        Long userId = userDao.save(testUser);
        User userToUpdate = userDao.findById(userId);
        userToUpdate.setName("Updated Name");
        userToUpdate.setAge(30);

        // Act
        userDao.update(userToUpdate);

        // Assert
        User updatedUser = userDao.findById(userId);
        assertEquals("Updated Name", updatedUser.getName());
        assertEquals(30, updatedUser.getAge());
    }

    @Test
    @DisplayName("Удаление пользователя - успех")
    void delete_ShouldDeleteUser_WhenUserExists() {
        // Arrange
        Long userId = userDao.save(testUser);
        assertNotNull(userDao.findById(userId));

        // Act
        userDao.delete(userId);

        // Assert
        assertNull(userDao.findById(userId));
    }

    @Test
    @DisplayName("Проверка существования по ID - пользователь с таким ID существует")
    void existsById_ShouldReturnTrue_WhenUserExists() {
        // Arrange
        Long userId = userDao.save(testUser);

        // Act
        boolean exists = userDao.existsById(userId);

        // Assert
        assertTrue(exists);
    }

    @Test
    @DisplayName("Проверка существования по ID - пользователь с таким ID не существует")
    void existsById_ShouldReturnFalse_WhenUserNotExists() {
        // Act
        boolean exists = userDao.existsById(999L);

        // Assert
        assertFalse(exists);
    }

    @Test
    @DisplayName("Проверка существования по email - пользователь с таким email существует")
    void existsByEmail_ShouldReturnTrue_WhenEmailExists() {
        // Arrange
        userDao.save(testUser);

        // Act
        boolean exists = userDao.existsByEmail("test@example.com");

        // Assert
        assertTrue(exists);
    }

    @Test
    @DisplayName("Проверка существования по email - пользователя с таким email не существует")
    void existsByEmail_ShouldReturnFalse_WhenEmailNotExists() {
        // Act
        boolean exists = userDao.existsByEmail("nonexistent@example.com");

        // Assert
        assertFalse(exists);
    }



}
