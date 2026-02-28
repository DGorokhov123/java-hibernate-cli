package ru.dgorokhov.dal;

import java.util.List;

public interface UserDao {

    /*
    Добавляет запись в БД и возвращает добавленную сущность
     */
    User create(User user);

    /*
    Возвращает сущность по id, либо null если не найдена
     */
    User findById(Long id);

    /*
    Возвращает все записи из БД в виде листа сущностей
     */
    List<User> findAll();

    /*
    Обновляет запись БД по Id сущности. Обновляет только не-null поля.
    Возвращает измененную сущность, либо null если запись по id не найдена
     */
    User update(User user);

    /*
    Удаляет запись по id. Возвращает true при успешном удалении, либо false
     */
    boolean delete(Long id);

    /*
    Проверяет запись по id. Возвращает true если запись существует, либо false
     */
    boolean existsById(Long id);

    /*
    Возвращает сущность по уникальному полю email, либо null если не найдена
     */
    User findByEmail(String email);

}
