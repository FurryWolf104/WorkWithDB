package org.example;

import dao.UserDao;
import dao.UserDaoImpl;
import entity.User;

//В PostgreSQL уже есть тестовая БД - user_test, в ней есть сущность user, c параметрами:
/*
*   id SERIAL PRIMARY KEY
*   name VARCHAR(100) NOT NULL
*   email VARCHAR(100) UNIQUE NOT NULL
*   age INTEGER
*   created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
*/
public class Main {
    public static void main(String[] args) {
        UserDao userDao = new UserDaoImpl();

        // Тест создания
        User user = new User("Тестовый пользователь", "test@mail.ru", 25);
        Long id = userDao.save(user);
        System.out.println("Создан пользователь с ID: " + id);

        // Тест поиска
        User foundUser = userDao.findById(id);
        System.out.println("Найден пользователь: " + foundUser);

        // Тест получения всех
        System.out.println("Все пользователи: " + userDao.findAll());

        // Очистка
        //userDao.delete(id);

    }
}