package service;

import dao.UserDao;
import entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserDao userDao;

    private UserService userService;
    private User testUser;

    @BeforeEach
    void setUp() {
        // Создаем тестового пользователя
        userService = new UserService(userDao);
        testUser = new User("John Doe", "john@test.com", 30);
        testUser.setId(1L);
        testUser.setCreatedAt(LocalDateTime.now());
    }
    //Успешное создание пользователя
    @Test
    void createUser_ShouldReturnUserId_WhenValidUser() {
        // Arrange
        when(userDao.save(any(User.class))).thenReturn(1L);

        // Act
        Long userId = userService.createUser(testUser);

        // Assert
        assertNotNull(userId);
        assertEquals(1L, userId);

        // Проверяем, что методы были вызваны
        verify(userDao, times(1)).save(testUser);
        verify(userDao, never()).existsByEmail(anyString()); // Должно НЕ вызываться
    }
    //Исключение при пустом email
    @Test
    void createUser_ShouldThrowException_WhenEmailIsEmpty() {
        // Arrange
        testUser.setEmail("");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.createUser(testUser)
        );

        assertEquals("Email не может быть пустым", exception.getMessage());

        verify(userDao, never()).save(any(User.class));
    }

    //Получение пользователя по ID (успех)
    @Test
    void getUserById_ShouldReturnUser_WhenUserExists() {
        // Arrange
        when(userDao.findById(1L)).thenReturn(testUser);

        // Act
        User foundUser = userService.getUserById(1L);

        // Assert
        assertNotNull(foundUser);
        assertEquals(testUser.getId(), foundUser.getId());
        assertEquals(testUser.getEmail(), foundUser.getEmail());

        verify(userDao, times(1)).findById(1L);
    }

    //Получение пользователя по ID (не найден)
    @Test
    void getUserById_ShouldReturnNull_WhenUserNotExists() {
        // Arrange
        when(userDao.findById(999L)).thenReturn(null);

        // Act
        User foundUser = userService.getUserById(999L);

        // Assert
        assertNull(foundUser);

        verify(userDao, times(1)).findById(999L);
    }

    //Получение всех пользователей
    @Test
    void getAllUsers_ShouldReturnUserList() {
        // Arrange
        List<User> users = Arrays.asList(testUser, testUser);
        when(userDao.findAll()).thenReturn(users);

        // Act
        List<User> result = userService.getAllUsers();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());

        verify(userDao, times(1)).findAll();
    }

    //Удаление пользователя (успех)
    @Test
    void deleteUser_ShouldDeleteUser_WhenUserExists() {
        // Arrange
        when(userDao.existsById(1L)).thenReturn(true);

        // Act
        userService.deleteUser(1L);

        // Assert
        verify(userDao, times(1)).existsById(1L);
        verify(userDao, times(1)).delete(1L);
    }

    //Удаление пользователя (не найден)
    @Test
    void deleteUser_ShouldThrowException_WhenUserNotExists() {
        // Arrange
        when(userDao.existsById(999L)).thenReturn(false);

        // Act
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.deleteUser(999L)
        );



        assertEquals("Пользователь не найден", exception.getMessage());
        verify(userDao, never()).delete(anyLong());
    }

}
