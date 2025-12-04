//package org.example;
//
//import dao.UserDao;
//import dao.UserDaoImpl;
//import entity.User;
//import service.UserService;
//
////В PostgreSQL уже есть тестовая БД - user_test, в ней есть сущность user, c параметрами:
///*
//*   id SERIAL PRIMARY KEY
//*   name VARCHAR(100) NOT NULL
//*   email VARCHAR(100) UNIQUE NOT NULL
//*   age INTEGER
//*   created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
//*/
//public class Main {
//    public static void main(String[] args) {
//        UserService userService = new UserService();
//
//        try {
//            System.out.println("=== Тестирование UserService ===");
//
//            // CREATE
//            User newUser = userService.createUserObject("Иван Тестовый", "ivan.test@mail.ru", 30);
//            Long userId = userService.createUser(newUser);
//            System.out.println("Создан пользователь с ID: " + userId);
//
//            // READ
//            User user = userService.getUserById(userId);
//            System.out.println("Найден пользователь: " + user);
//
//            // READ ALL
//            var allUsers = userService.getAllUsers();
//            System.out.println("Всего пользователей: " + allUsers.size());
//
//            // UPDATE
//            User updatedUser = userService.createUserObject("Иван Обновленный", "ivan.updated@mail.ru", 31);
//            updatedUser.setId(userId); // Устанавливаем тот же ID для обновления
//            userService.updateUser(updatedUser);
//            System.out.println("Пользователь обновлен");
//
//            // CHECK EXISTS
//            System.out.println("Пользователь существует: " + userService.userExists(userId));
//            System.out.println("Email существует: " + userService.emailExists("ivan.updated@mail.ru"));
//
//            // DELETE
//            userService.deleteUser(userId);
//            System.out.println("Пользователь удален");
//
//            System.out.println("=== Тестирование завершено ===");
//
//        } catch (Exception e) {
//            System.err.println("Ошибка: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
//}