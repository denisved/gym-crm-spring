package org.gymcrm.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppConfigTest {

    @Mock
    private Environment env;

    @InjectMocks
    private AppConfig appConfig;

    @Test
    void dataSource_ReturnsDataSource() {
        when(env.getRequiredProperty("spring.datasource.driver-class-name")).thenReturn("org.postgresql.Driver");
        when(env.getRequiredProperty("spring.datasource.url")).thenReturn("jdbc:postgresql://localhost:5432/gym");
        when(env.getRequiredProperty("spring.datasource.username")).thenReturn("user");
        when(env.getRequiredProperty("spring.datasource.password")).thenReturn("pass");

        DataSource dataSource = appConfig.dataSource();
        assertNotNull(dataSource);
        assertTrue(dataSource instanceof DriverManagerDataSource);
    }

    @Test
    void entityManagerFactory_ReturnsFactory() {
        when(env.getRequiredProperty("spring.datasource.driver-class-name")).thenReturn("org.postgresql.Driver");
        when(env.getRequiredProperty("spring.datasource.url")).thenReturn("jdbc:postgresql://localhost:5432/gym");
        when(env.getRequiredProperty("spring.datasource.username")).thenReturn("user");
        when(env.getRequiredProperty("spring.datasource.password")).thenReturn("pass");
        
        when(env.getProperty(eq("spring.jpa.hibernate.ddl-auto"), anyString())).thenReturn("update");
        when(env.getProperty("spring.jpa.properties.hibernate.dialect")).thenReturn("org.hibernate.dialect.PostgreSQLDialect");
        when(env.getProperty(eq("spring.jpa.show-sql"), anyString())).thenReturn("true");
        when(env.getProperty(eq("spring.jpa.properties.hibernate.format_sql"), anyString())).thenReturn("true");

        LocalContainerEntityManagerFactoryBean emf = appConfig.entityManagerFactory();
        assertNotNull(emf);
    }

    @Test
    void validator_ReturnsValidator() {
        assertNotNull(appConfig.validator());
    }
}
