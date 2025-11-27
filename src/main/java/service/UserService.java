package service;

import dao.UserDao;
import dao.UserDaoImpl;
import entity.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class UserService {
    private static final Logger logger = LogManager.getLogger(UserService.class);
    private final UserDao userDao = new UserDaoImpl();

    // CREATE
    public Long createUser(User user) {
        logger.info("Создание пользователя: {}", user.getEmail());

        // Валидация
        validateUser(user);

        Long id = userDao.save(user);
        logger.info("Пользователь успешно создан с ID: {}", id);
        return id;
    }


    // READ
    public User getUserById(Long id) {
        logger.info("Поиск пользователя по ID: {}", id);
        User user = userDao.findById(id);
        if (user != null) {
            logger.info("Пользователь найден: ID={}, email={}", id, user.getEmail());
        } else {
            logger.warn("Пользователь с ID {} не найден", id);
        }
        return user;
    }

    public List<User> getAllUsers() {
        logger.info("Запрос всех пользователей");
        List<User> users = userDao.findAll();
        logger.info("Найдено {} пользователей", users.size());
        return users;
    }

    public User getUserByEmail(String email) {
        logger.info("Поиск пользователя по email: {}", email);
        User user = userDao.findByEmail(email);
        if (user != null) {
            logger.info("Пользователь с email {} найден", email);
        } else {
            logger.warn("Пользователь с email {} не найден", email);
        }
        return user;
    }

    // UPDATE
    public void updateUser(User user) {
        logger.info("Обновление пользователя: ID={}, name={}, email={}, age={}",
                user.getId(), user.getName(), user.getEmail(), user.getAge());

        // Проверяем существование пользователя
        User existingUser = getUserById(user.getId());
        if (existingUser == null) {
            logger.error("Пользователь с ID {} не найден для обновления", user.getId());
            throw new IllegalArgumentException("Пользователь не найден");
        }

        // Валидация новых данных
        validateUser(user);

        // Обновляем поля существующего пользователя
        existingUser.setName(user.getName());
        existingUser.setEmail(user.getEmail());
        existingUser.setAge(user.getAge());

        userDao.update(existingUser);
        logger.info("Пользователь ID={} успешно обновлен", user.getId());
    }

    // DELETE
    public void deleteUser(Long id) {
        logger.info("Удаление пользователя: ID={}", id);

        if (!userDao.existsById(id)) {
            logger.warn("Попытка удаления несуществующего пользователя с ID {}", id);
            throw new IllegalArgumentException("Пользователь не найден");
        }

        userDao.delete(id);
        logger.info("Пользователь ID={} успешно удален", id);
    }

    // UTILITY
    public boolean userExists(Long id) {
        boolean exists = userDao.existsById(id);
        logger.debug("Проверка существования пользователя ID={}: {}", id, exists);
        return exists;
    }

    public boolean emailExists(String email) {
        boolean exists = userDao.existsByEmail(email);
        logger.debug("Проверка существования email {}: {}", email, exists);
        return exists;
    }

        // Полная валидация пользователя

    private void validateUser(User user) {
        validateName(user.getName());
        validateEmail(user.getEmail());
        validateAge(user.getAge());
        }
    private void validateName(String name){
        if (name == null || name.trim().isEmpty()){
            throw new IllegalArgumentException("Имя не может быть пустым");
        }
        // Имя содержит только буквы, пробелы, дефисы и апострофы
        if (!name.matches("[a-zA-Zа-яА-Я\\s\\-']+")) {
            throw new IllegalArgumentException("Имя может содержать только буквы, пробелы, дефисы и апострофы");
        }
        //Проверяем длину имени
        if (name.length() < 2 || name.length() > 50) {
                throw new IllegalArgumentException("Имя должно содержать от 2 до 50 символов");
        }
    }

    // Валидация email
    private void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email не может быть пустым");
        }

        // проверка формата email
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        if (!email.matches(emailRegex)) {
            throw new IllegalArgumentException("Некорректный формат email");
        }
    }

    // Валидация возраста
    private void validateAge(Integer age) {
        if (age == null) {
            throw new IllegalArgumentException("Возраст не может быть пустым");
        }

        if (age < 0 || age > 150) {
            throw new IllegalArgumentException("Возраст должен быть в диапазоне от 0 до 150 лет");
        }
    }

    public User createUserObject(String name, String email, Integer age) {
        return new User(name, email, age);
    }
}