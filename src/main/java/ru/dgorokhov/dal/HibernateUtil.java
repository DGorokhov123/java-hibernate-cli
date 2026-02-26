package ru.dgorokhov.dal;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

@Slf4j
public class HibernateUtil {

    @Getter
    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            SessionFactory factory = new Configuration().configure().buildSessionFactory();
            log.info("SessionFactory initialized successfully.");
            return factory;
        } catch (Exception e) {
            log.error("Failed to initialize SessionFactory: {}", e.getMessage());
            throw new RuntimeException("Failed to initialize SessionFactory", e);
        }
    }

    public static void shutdown() {
        log.info("Shutting down SessionFactory...");
        try {
            sessionFactory.close();
            log.info("SessionFactory closed.");
        } catch (Exception e) {
            log.info("Failed to close SessionFactory.");
            throw new RuntimeException("Failed to close SessionFactory", e);
        }
    }

}
