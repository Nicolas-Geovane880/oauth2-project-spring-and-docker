package api;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;

public class IntegrationTestConfig {

    static MySQLContainer<?> mysql;

    static {
        mysql = new MySQLContainer<>()
                .withDatabaseName("api-test-config")
                .withUsername("api-test-config")
                .withPassword("api-test-config");

        mysql.start();
    }

    @DynamicPropertySource
    static void dynamicPropertySource (DynamicPropertyRegistry props) {
        props.add("spring.datasource.url", () -> mysql.getJdbcUrl());
        props.add("spring.datasource.username", () -> mysql.getUsername());
        props.add("spring.datasource.password", () -> mysql.getPassword());
        props.add("spring.datasource.driver-class-name", () -> "com.mysql.cj.jdbc.Driver");
    }
}

