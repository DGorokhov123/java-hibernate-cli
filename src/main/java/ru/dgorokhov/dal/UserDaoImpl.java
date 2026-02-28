package ru.dgorokhov.dal;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Objects;

@Slf4j
public class UserDaoImpl implements UserDao {

    private final SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

    /*
    Добавляет запись в БД и возвращает добавленную сущность
     */
    @Override
    public User create(User user) {
        log.debug("Creating user: {}", user);
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.persist(user);
            transaction.commit();
            log.debug("Successfully created new {}", user);
            return user;
        }
    }

    /*
    Возвращает сущность по id, либо null если не найдена
     */
    @Override
    public User findById(Long id) {
        log.debug("Getting user by ID: {}", id);
        try (Session session = sessionFactory.openSession()) {
            User user = session.find(User.class, id);
            if (user != null) {
                log.debug("Successfully found {}", user);
                return user;
            } else {
                log.debug("Not found User {}", id);
                return null;
            }
        }
    }

    /*
    Возвращает все записи из БД в виде листа сущностей
     */
    @Override
    public List<User> findAll() {
        log.debug("Finding all users");
        try (Session session = sessionFactory.openSession()) {
            List<User> users = session.createQuery("FROM User", User.class).list();
            log.debug("Found {} users", users.size());
            return users;
        }
    }

    /*
    Обновляет запись БД по Id сущности. Обновляет только не-null поля.
    Возвращает измененную сущность, либо null если запись по id не найдена
     */
    @Override
    public User update(User user) {
        log.debug("Updating user: {}", user);
        Transaction transaction = null;

        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            User existingUser = session.find(User.class, user.getId());

            if (existingUser == null) {
                log.debug("User not found with ID {}", user.getId());
                return null;
            }

            if (user.getName() != null && !user.getName().isBlank() && !Objects.equals(user.getName(), existingUser.getName())) {
                existingUser.setName(user.getName());
            }
            if (user.getEmail() != null && !user.getEmail().isBlank() && !Objects.equals(user.getEmail(), existingUser.getEmail())) {
                existingUser.setEmail(user.getEmail());
            }
            if (user.getAge() != null && !Objects.equals(user.getAge(), existingUser.getAge())) {
                existingUser.setAge(user.getAge());
            }

            session.merge(existingUser);
            transaction.commit();
            log.debug("User updated successfully: {}", existingUser);
            return existingUser;
        }
    }

    /*
    Удаляет запись по id. Возвращает true при успешном удалении, либо false
     */
    @Override
    public boolean delete(Long id) {
        log.debug("Deleting user by ID: {}", id);
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            User user = session.find(User.class, id);
            if (user != null) {
                session.remove(user);
                transaction.commit();
                log.debug("User deleted successfully with ID: {}", id);
                return true;
            } else {
                log.debug("User not found with ID: {}", id);
                return false;
            }
        }
    }

    /*
    Проверяет запись по id. Возвращает true если запись существует, либо false
     */
    @Override
    public boolean existsById(Long id) {
        log.debug("Checking user by ID: {}", id);
        try (Session session = sessionFactory.openSession()) {
            User user = session.find(User.class, id);
            if (user != null) {
                log.debug("Successfully found {}", user);
                return true;
            } else {
                log.debug("Not found User {}", id);
                return false;
            }
        }
    }

    /*
    Возвращает сущность по уникальному полю email, либо null если не найдена
     */
    @Override
    public User findByEmail(String email) {
        log.debug("Getting user by Email: {}", email);
        try (Session session = sessionFactory.openSession()) {
            User user = session.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
                    .setParameter("email", email)
                    .uniqueResult();
            if (user != null) {
                log.debug("Successfully found {}", user);
                return user;
            } else {
                log.debug("Not found User with email {}", email);
                return null;
            }
        }
    }

}
