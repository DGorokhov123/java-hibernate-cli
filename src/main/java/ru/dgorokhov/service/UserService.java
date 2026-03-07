package ru.dgorokhov.service;

import ru.dgorokhov.dal.User;
import ru.dgorokhov.dal.UserDao;
import ru.dgorokhov.dal.UserDaoImpl;
import ru.dgorokhov.dto.UserCreateDto;
import ru.dgorokhov.dto.UserResponseDto;
import ru.dgorokhov.dto.UserUpdateDto;
import ru.dgorokhov.mapper.UserMapper;

import java.util.List;

public class UserService {

    private final UserDao userDao;

    public UserService() {
        this.userDao = new UserDaoImpl();
    }

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    /*
    Все юзеры в виде листа dto
     */
    public List<UserResponseDto> findAll() {
        List<User> users = userDao.findAll();
        return users.stream()
                .map(UserMapper::toDto)
                .toList();
    }

    /*
    Возвращает dto по id, либо null если не найдена
     */
    public UserResponseDto findById(Long id) {
        User user = userDao.findById(id);
        return UserMapper.toDto(user);
    }

    /*
    Обновляет пользователя по dto. Обновляет только не-null поля.
    Возвращает измененное dto, либо null если запись по id не найдена
     */
    public UserResponseDto update(UserUpdateDto userUpdateDto) {
        if (userUpdateDto == null) return null;
        userUpdateDto.validate();
        User updatedUser = userDao.update(UserMapper.fromDto(userUpdateDto));
        return UserMapper.toDto(updatedUser);
    }

    /*
    Удаляет пользователя по id. Возвращает true при успешном удалении, либо false
     */
    public boolean delete(Long id) {
        return userDao.delete(id);
    }

    /*
    Добавляет пользователя по dto и возвращает добавленного
     */
    public UserResponseDto create(UserCreateDto userCreateDto) {
        if (userCreateDto == null) return null;
        userCreateDto.validate();
        User user = UserMapper.fromDto(userCreateDto);
        User createdUser = userDao.create(user);
        return UserMapper.toDto(createdUser);
    }

    /*
    Проверяет пользователя по id. Возвращает true если существует, либо false
     */
    public boolean existsById(Long id) {
        return userDao.existsById(id);
    }

    /*
    Возвращает пользователя по email, либо null если не найден
     */
    public UserResponseDto findByEmail(String email) {
        User user = userDao.findByEmail(email);
        return UserMapper.toDto(user);
    }

}
