package org.example;

import dao.UserDao;
import dao.UserDaoImpl;
import entity.User;
import service.UserService;

import java.util.List;
import java.util.Scanner;

//В PostgreSQL уже есть тестовая БД - user_test, в ней есть сущность user, c параметрами:
/*
*   id SERIAL PRIMARY KEY
*   name VARCHAR(100) NOT NULL
*   email VARCHAR(100) UNIQUE NOT NULL
*   age INTEGER
*   created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
*/
public class Main {
    private static final UserService userService = new UserService();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("=== User Service Console Application ===");
        showMenu();
    }

    private static void showMenu() {
        while (true) {
            System.out.println("\n=== Меню ===");
            System.out.println("1. Создать пользователя");
            System.out.println("2. Найти пользователя по ID");
            System.out.println("3. Найти пользователя по email");
            System.out.println("4. Показать всех пользователей");
            System.out.println("5. Обновить пользователя");
            System.out.println("6. Удалить пользователя");
            System.out.println("0. Выход");
            System.out.print("Выберите опцию: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());

                switch (choice) {
                    case 1 -> createUser();
                    case 2 -> findUserById();
                    case 3 -> findUserByEmail();
                    case 4 -> showAllUsers();
                    case 5 -> updateUser();
                    case 6 -> deleteUser();
                    case 0 -> {
                        System.out.println("Выход из приложения...");
                        return;
                    }
                    default -> System.out.println("Неверный выбор. Попробуйте снова.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введите число от 0 до 6");
            } catch (Exception e) {
                System.out.println("Произошла ошибка: " + e.getMessage());
            }
        }
    }

    private static void createUser() {
        try {
            System.out.println("\n--- Создание пользователя ---");

            System.out.print("Введите имя: ");
            String name = scanner.nextLine().trim();

            System.out.print("Введите email: ");
            String email = scanner.nextLine().trim();

            System.out.print("Введите возраст: ");
            int age = Integer.parseInt(scanner.nextLine().trim());

            User user = userService.createUserObject(name, email, age);
            Long id = userService.createUser(user);

            System.out.println("Пользователь успешно создан с ID: " + id);

        } catch (NumberFormatException e) {
            System.out.println("Ошибка: возраст должен быть числом");
        } catch (Exception e) {
            System.out.println("Ошибка при создании пользователя: " + e.getMessage());
        }
    }

    private static void findUserById() {
        try {
            System.out.println("\n--- Поиск пользователя по ID ---");
            System.out.print("Введите ID пользователя: ");
            Long id = Long.parseLong(scanner.nextLine().trim());

            User user = userService.getUserById(id);
            if (user != null) {
                System.out.println("Найден пользователь: " + user);
            } else {
                System.out.println("Пользователь с ID " + id + " не найден");
            }

        } catch (NumberFormatException e) {
            System.out.println("Ошибка: ID должен быть числом");
        } catch (Exception e) {
            System.out.println("Ошибка при поиске пользователя: " + e.getMessage());
        }
    }

    private static void findUserByEmail() {
        try {
            System.out.println("\n--- Поиск пользователя по email ---");
            System.out.print("Введите email: ");
            String email = scanner.nextLine().trim();

            User user = userService.getUserByEmail(email);
            if (user != null) {
                System.out.println("Найден пользователь: " + user);
            } else {
                System.out.println("Пользователь с email " + email + " не найден");
            }

        } catch (Exception e) {
            System.out.println("Ошибка при поиске пользователя: " + e.getMessage());
        }
    }

    private static void showAllUsers() {
        try {
            System.out.println("\n--- Все пользователи ---");
            List<User> users = userService.getAllUsers();

            if (users.isEmpty()) {
                System.out.println("Нет пользователей в базе данных");
            } else {
                for (User user : users) {
                    System.out.println(user);
                }
                System.out.println("Всего пользователей: " + users.size());
            }

        } catch (Exception e) {
            System.out.println("Ошибка при получении пользователей: " + e.getMessage());
        }
    }

    private static void updateUser() {
        try {
            System.out.println("\n--- Обновление пользователя ---");
            System.out.print("Введите ID пользователя для обновления: ");
            Long id = Long.parseLong(scanner.nextLine().trim());

            User existingUser = userService.getUserById(id);
            if (existingUser == null) {
                System.out.println("Пользователь с ID " + id + " не найден");
                return;
            }

            System.out.println("Текущие данные: " + existingUser);

            System.out.print("Введите новое имя (текущее: " + existingUser.getName() + "): ");
            String name = scanner.nextLine().trim();
            if (name.isEmpty()) name = existingUser.getName();

            System.out.print("Введите новый email (текущий: " + existingUser.getEmail() + "): ");
            String email = scanner.nextLine().trim();
            if (email.isEmpty()) email = existingUser.getEmail();

            System.out.print("Введите новый возраст (текущий: " + existingUser.getAge() + "): ");
            String ageInput = scanner.nextLine().trim();
            Integer age = ageInput.isEmpty() ? existingUser.getAge() : Integer.parseInt(ageInput);

            User updatedUser = userService.createUserObject(name, email, age);
            updatedUser.setId(id);
            userService.updateUser(updatedUser);

            System.out.println("Пользователь успешно обновлен");

        } catch (NumberFormatException e) {
            System.out.println("Ошибка: ID и возраст должны быть числами");
        } catch (Exception e) {
            System.out.println("Ошибка при обновлении пользователя: " + e.getMessage());
        }
    }

    private static void deleteUser() {
        try {
            System.out.println("\n--- Удаление пользователя ---");
            System.out.print("Введите ID пользователя для удаления: ");
            Long id = Long.parseLong(scanner.nextLine().trim());

            // Подтверждение удаления
            User user = userService.getUserById(id);
            if (user != null) {
                System.out.println("Вы действительно хотите удалить пользователя: " + user + "?");
                System.out.print("Введите 'да' для подтверждения: ");
                String confirmation = scanner.nextLine().trim();

                if ("да".equalsIgnoreCase(confirmation)) {
                    userService.deleteUser(id);
                    System.out.println("Пользователь успешно удален");
                } else {
                    System.out.println("Удаление отменено");
                }
            } else {
                System.out.println("Пользователь с ID " + id + " не найден");
            }

        } catch (NumberFormatException e) {
            System.out.println("Ошибка: ID должен быть числом");
        } catch (Exception e) {
            System.out.println("Ошибка при удалении пользователя: " + e.getMessage());
        }
    }
}