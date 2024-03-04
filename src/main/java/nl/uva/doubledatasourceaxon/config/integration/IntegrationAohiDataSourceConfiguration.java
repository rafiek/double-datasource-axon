package nl.uva.doubledatasourceaxon.config.integration;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import nl.uva.doubledatasourceaxon.jpa.Integration;
import org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


/**
 * Datasource integration-aohi
 */
@Slf4j
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackageClasses = Integration.class,
        entityManagerFactoryRef = "integrationAohiEntityManager",
        transactionManagerRef = "integrationTransactionManager"
)
public class IntegrationAohiDataSourceConfiguration {

    @Primary
    @Bean("integrationAohi")
    @ConfigurationProperties("spring.datasource.hikari.integration-aohi")
    public DataSource integrationAohi() {
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }

    @Primary
    @Bean(name = "integrationAohiEntityManager")
    public LocalContainerEntityManagerFactoryBean integrationAohiEntityManager(
            EntityManagerFactoryBuilder entityManagerFactoryBuilder,
            DataSource integrationAohi
    ) {
        return  entityManagerFactoryBuilder.dataSource(integrationAohi)
                .persistenceUnit("integrationAohi")
                .packages(Integration.class)
                .properties(jpaProperties())
                .build();
    }

    @Bean(name = "integrationTransactionManager")
    public PlatformTransactionManager integrationTransactionManager(
            @Qualifier("integrationAohiEntityManager") LocalContainerEntityManagerFactoryBean integrationEntityManagerFactory) {
        log.info("Creating integrationTransactionManager");
        return new JpaTransactionManager(Objects.requireNonNull(integrationEntityManagerFactory.getObject()));
    }

    private Map<String, Object> jpaProperties() {
        Map<String, Object> props = new HashMap<>();
        props.put("hibernate.physical_naming_strategy", CamelCaseToUnderscoresNamingStrategy.class.getName());
        props.put("hibernate.implicit_naming_strategy", SpringImplicitNamingStrategy.class.getName());
        props.put("hibernate.hbm2ddl.auto", "update");
        props.put("hibernate.show_sql", "false");
        props.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        return props;
    }
}
