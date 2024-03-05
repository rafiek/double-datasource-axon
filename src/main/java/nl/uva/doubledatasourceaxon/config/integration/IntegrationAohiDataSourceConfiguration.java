package nl.uva.doubledatasourceaxon.config.integration;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy;
import org.hibernate.cfg.Environment;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Map;
import java.util.Objects;

/**
 * Datasource integration-aohi
 */
@Slf4j
@Configuration
@EnableJpaRepositories(
        basePackages = IntegrationAohiDataSourceConfiguration.INTEGRATION_JPA_PACKAGE_NAME,
        entityManagerFactoryRef = IntegrationAohiDataSourceConfiguration.INTEGRATION_ENTITY_MANAGER,
        transactionManagerRef = IntegrationAohiDataSourceConfiguration.INTEGRATION_TRANSACTION_MANAGER
)
@EnableTransactionManagement
public class IntegrationAohiDataSourceConfiguration {

    public static final String INTEGRATION_DATA_SOURCE = "integrationDataSource";
    public static final String INTEGRATION_ENTITY_MANAGER = "integrationEntityManager";
    public static final String INTEGRATION_TRANSACTION_MANAGER = "integrationTransactionManager";
    public static final String INTEGRATION_DOMAIN_PACKAGE_NAME = "nl.uva.doubledatasourceaxon.jpa";
    public static final String INTEGRATION_JPA_PACKAGE_NAME = "nl.uva.doubledatasourceaxon.jpa";

    @Bean
    @ConfigurationProperties("spring.datasource.integration-aohi")
    public DataSourceProperties integrationDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(INTEGRATION_DATA_SOURCE)
    @ConfigurationProperties("spring.datasource.integration-aohi.hikari")
    public DataSource integrationDataSource() {
        return integrationDataSourceProperties()
                .initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }

    @Bean(INTEGRATION_ENTITY_MANAGER)
    public LocalContainerEntityManagerFactoryBean integrationEntityManagerFactory(
            @Qualifier(INTEGRATION_DATA_SOURCE) DataSource integrationDataSource,
            JpaProperties jpaProperties
    ) {
        LocalContainerEntityManagerFactoryBean emfb = new LocalContainerEntityManagerFactoryBean();

        emfb.setPersistenceUnitName("integration-unit");
        emfb.setPackagesToScan(INTEGRATION_DOMAIN_PACKAGE_NAME);
        emfb.setDataSource(integrationDataSource);
        emfb.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        emfb.setJpaPropertyMap(buildJpaProperties(jpaProperties));
        return emfb;
    }

    @Bean(INTEGRATION_TRANSACTION_MANAGER)
    public PlatformTransactionManager integrationTransactionManager(
            @Qualifier(INTEGRATION_ENTITY_MANAGER) LocalContainerEntityManagerFactoryBean entityManager
    ) {
        return new JpaTransactionManager(Objects.requireNonNull(entityManager.getObject()));
    }

    private Map<String, String> buildJpaProperties(JpaProperties jpaProperties) {
        Map<String, String> properties = jpaProperties.getProperties();

        properties.put(Environment.SHOW_SQL, "false");
        properties.put(Environment.FORMAT_SQL, "false");
        properties.put(Environment.HIGHLIGHT_SQL, "false");
        properties.put(Environment.GENERATE_STATISTICS, "false");

        properties.put(Environment.HBM2DDL_AUTO, "update");
        properties.put(Environment.CONNECTION_PROVIDER_DISABLES_AUTOCOMMIT, "true");
        properties.put(Environment.IMPLICIT_NAMING_STRATEGY, SpringImplicitNamingStrategy.class.getName());
        properties.put(Environment.PHYSICAL_NAMING_STRATEGY, CamelCaseToUnderscoresNamingStrategy.class.getName());

        properties.put(Environment.JDBC_TIME_ZONE, "UTC");
        properties.put(Environment.STATEMENT_BATCH_SIZE, "15");
        properties.put(Environment.ORDER_INSERTS, "true");
        properties.put(Environment.ORDER_UPDATES, "true");

        properties.put(Environment.IN_CLAUSE_PARAMETER_PADDING, "true");
        properties.put(Environment.FAIL_ON_PAGINATION_OVER_COLLECTION_FETCH, "true");
        properties.put(Environment.QUERY_PLAN_CACHE_MAX_SIZE, "4096");

        return properties;
    }
}
