package ru.dgorokhov.dal;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.Transaction;
import ru.dgorokhov.exception.DatabaseConnectivityException;
import ru.dgorokhov.exception.DatabaseConstraintsException;
import ru.dgorokhov.exception.EntityValidationException;

import java.util.List;
import java.util.Objects;

@Slf4j
public class UserDao {

    // Создать юзера
    public User create(User user) {
        log.debug("Creating user: {}", user);
        Transaction transaction = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(user);
            transaction.commit();
            log.debug("Successfully created new {}", user);
            return user;
        } catch (Exception e) {
            throw handleException(e, transaction);
        }
    }

    // Найти по ID
    public User findById(Long id) {
        log.debug("Getting user by ID: {}", id);

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            User user = session.find(User.class, id);
            if (user != null) {
                log.debug("Successfully found {}", user);
                return user;
            } else {
                log.debug("Not found User {}", id);
                return null;
            }
        } catch (Exception e) {
            throw handleException(e, null);
        }
    }

    // Все записи
    public List<User> findAll() {
        log.debug("Finding all users");

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<User> users = session.createQuery("FROM User", User.class).list();
            log.debug("Found {} users", users.size());
            return users;
        } catch (Exception e) {
            throw handleException(e, null);
        }
    }

    // Обновить
    public User update(User user) {
        log.debug("Updating user: {}", user);
        Transaction transaction = null;

        if (user == null || user.getId() == null) {
            log.error("Failed to update user without id: {}", user);
            throw new EntityValidationException("Failed to update user without id: {}" + user);
        }

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            User existingUser = session.find(User.class, user.getId());

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
        } catch (Exception e) {
            throw handleException(e, transaction);
        }
    }

    // Удалить
    public boolean delete(Long id) {
        log.debug("Deleting user by ID: {}", id);
        Transaction transaction = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
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
        } catch (Exception e) {
            throw handleException(e, transaction);
        }
    }

    private RuntimeException handleException(Exception e, Transaction transaction) {
        if (transaction != null && transaction.isActive()) {
            try {
                transaction.rollback();
            } catch (Exception ignored) {
            }
        }

        switch (e) {
            case jakarta.validation.ConstraintViolationException ex -> {
                log.debug("Validation Error", ex);
                return new EntityValidationException("Validation Error: " + ex.getMessage());
            }
            case org.hibernate.exception.ConstraintViolationException ex -> {
                log.debug("Database Constraints Error", ex);
                return new DatabaseConstraintsException("Database Constraints Error: " + ex.getMessage());
            }
            default -> {
                log.debug("Database error: {}", e.getMessage());
                return new DatabaseConnectivityException("Database error: " + e.getMessage());
            }
        }
    }

}
