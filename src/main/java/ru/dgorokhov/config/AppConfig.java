package ru.dgorokhov.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.sql.DataSource;
import java.util.Properties;

@Slf4j
@Configuration
@EnableWebMvc
@ComponentScan("ru.dgorokhov")
@EnableJpaRepositories("ru.dgorokhov")
@PropertySource(factory = YamlPropertySourceFactory.class, value = "classpath:application.yaml")
public class AppConfig implements WebMvcConfigurer {

    @Bean
    public DataSource dataSource(
            @Value("${spring.datasource.url}") String url,
            @Value("${spring.datasource.username}") String username,
            @Value("${spring.datasource.password}") String password,
            @Value("${spring.datasource.driver-class-name}") String driverClassName
    ) {
        log.info("Read config values url {}, username {}, password {}, driver class {}", url, username, password, driverClassName);

        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setUrl(url);
        ds.setUsername(username);
        ds.setPassword(password);
        ds.setDriverClassName(driverClassName);
        return ds;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            DataSource dataSource,
            @Value("${spring.jpa.properties.hibernate.format_sql: none}") boolean formatSql,
            @Value("${spring.jpa.hibernate.ddl-auto: none}") String hbm2ddl,
            @Value("${spring.jpa.show-sql:false}") boolean showSql
    ) {
        log.info("Read config values formatSql {}, ddl-auto {}, showSql {}", formatSql, hbm2ddl, showSql);

        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(dataSource);
        emf.setPackagesToScan("ru.dgorokhov");

        HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
        emf.setJpaVendorAdapter(adapter);

        Properties jpaProps = new Properties();
        jpaProps.setProperty("hibernate.hbm2ddl.auto", hbm2ddl);
        jpaProps.setProperty("hibernate.show_sql", String.valueOf(showSql));
        jpaProps.setProperty("hibernate.format_sql", String.valueOf(formatSql));
        emf.setJpaProperties(jpaProps);
        return emf;
    }

    @Bean
    public PlatformTransactionManager transactionManager(
            LocalContainerEntityManagerFactoryBean entityManagerFactory
    ) {
        JpaTransactionManager tx = new JpaTransactionManager();
        tx.setEntityManagerFactory(entityManagerFactory.getObject());
        return tx;
    }

}
