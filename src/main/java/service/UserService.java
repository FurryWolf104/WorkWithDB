package service;

import dto.UserRequest;
import dto.UserResponse;
import entity.User;
import mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Сервисный слой с бизнес-логикой.
 *
 * Аннотация @Service:
 * 1. Помечает класс как Spring Bean (компонент бизнес-логики)
 * 2. Позволяет автоматическое обнаружение через @ComponentScan
 * 3. Позволяет внедрение зависимостей через @Autowired
 *
 * Аннотация @Transactional:
 * - Управляет транзакциями на уровне методов
 * - Все методы выполняются в транзакции
 * - При ошибке происходит автоматический rollback
 */
@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    /**
     * Конструктор для внедрения зависимостей.
     * Аннотация @Autowired не обязательна для одного конструктора в Spring 4.3+,
     * но мы явно указываем её для ясности.
     */
    @Autowired
    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    // CREATE
    public UserResponse createUser(UserRequest request) {

        validateEmailUniqueness(request.getEmail());

        User user = userMapper.toEntity(request);


        User savedUser = userRepository.save(user);

        return userMapper.toResponse(savedUser);
    }


    // READ
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        // findById возвращает Optional<User>
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь с ID " + id + " не найден"));

        return userMapper.toResponse(user);
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        // findAll() возвращает List<User>
        List<User> users = userRepository.findAll();

        // Преобразуем каждый User в UserResponse через stream
        return users.stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь с email " + email + " не найден"));

        return userMapper.toResponse(user);
    }

    // UPDATE
    public UserResponse updateUser(Long id, UserRequest request) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("Пользователь с ID " + id + " не найден");
        }

        User existingUser = userRepository.findById(id).get();
        if (!existingUser.getEmail().equals(request.getEmail())) {
            validateEmailUniqueness(request.getEmail());
        }

        User userToUpdate = userMapper.toEntity(request, id);

        User updatedUser = userRepository.save(userToUpdate);

        return userMapper.toResponse(updatedUser);
    }

    // DELETE
    public void deleteUser(Long id) {
        // 1. Проверяем существование
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("Пользователь с ID " + id + " не найден");
        }

        // 2. Удаляем (deleteById ничего не возвращает)
        userRepository.deleteById(id);
    }

    // UTILITY
    @Transactional(readOnly = true)
    public boolean userExists(Long id) {
        return userRepository.existsById(id);
    }

    private void validateEmailUniqueness(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Пользователь с email " + email + " уже существует");
        }
    }
    // Метод для создания объекта User (оставляем для обратной совместимости с тестами)
    public User createUserObject(String name, String email, Integer age) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setAge(age);
        user.setCreatedAt(java.time.LocalDateTime.now());
        return user;
    }
}