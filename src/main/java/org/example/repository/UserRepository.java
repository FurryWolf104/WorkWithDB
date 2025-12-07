package org.example.repository;

import org.example.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA Repository.
 * Наследуемся от JpaRepository<User, Long>:
 * - User: тип Entity
 * - Long: тип первичного ключа
 *
 * Spring автоматически создаст реализацию этого интерфейса
 * со всеми CRUD методами: save(), findById(), findAll(), deleteById() и т.д.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Поиск пользователя по email.
     * Spring Data JPA автоматически сгенерирует запрос по имени метода!
     * SQL: SELECT * FROM users WHERE email = ?
     */
    Optional<User> findByEmail(String email);

    /**
     * Проверка существования пользователя по email.
     * SQL: SELECT COUNT(*) > 0 FROM users WHERE email = ?
     */
    boolean existsByEmail(String email);
}
