package nl.uva.doubledatasourceaxon.config.axon;

import com.zaxxer.hikari.HikariDataSource;
import org.axonframework.springboot.autoconfig.JpaAutoConfiguration;
import org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy;
import org.hibernate.cfg.Environment;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Map;
import java.util.Objects;

@Configuration
@EnableTransactionManagement
@AutoConfigureBefore({JpaAutoConfiguration.class})
public class AxonEventStoreConfig {
    public static final String AXON_DATA_SOURCE = "axonDataSource";
    public static final String AXON_ENTITY_MANAGER = "axonEntityManager";
    public static final String AXON_TRANSACTION_MANAGER = "axonJpaTransactionManager";
    public static final String[] AXON_DOMAIN_PACKAGES_NAME = {
            "org.axonframework.eventhandling.tokenstore",
            "org.axonframework.eventsourcing.eventstore.jpa",
            "org.axonframework.eventhandling.deadletter.jpa",
            "org.axonframework.modelling.saga.repository.jpa"
    };

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource.axon-aohi")
    public DataSourceProperties axonDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Primary
    @Bean(AXON_DATA_SOURCE)
    @ConfigurationProperties("spring.datasource.axon-aohi.hikari")
    public DataSource axonDataSource() {
        return axonDataSourceProperties()
                .initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }

    @Primary
    @Bean(AXON_ENTITY_MANAGER)
    public LocalContainerEntityManagerFactoryBean axonEntityManagerFactory(
            @Qualifier(AXON_DATA_SOURCE) DataSource axonDataSource,
            JpaProperties jpaProperties
    ) {
        LocalContainerEntityManagerFactoryBean emfb = new LocalContainerEntityManagerFactoryBean();

        emfb.setPersistenceUnitName("axon-unit");
        emfb.setPackagesToScan(AXON_DOMAIN_PACKAGES_NAME);
        emfb.setDataSource(axonDataSource);
        emfb.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        emfb.setJpaPropertyMap(buildJpaProperties(jpaProperties));
        return emfb;
    }

    @Primary
    @Bean(AXON_TRANSACTION_MANAGER)
    public PlatformTransactionManager axonTransactionManager(
            @Qualifier(AXON_ENTITY_MANAGER) LocalContainerEntityManagerFactoryBean entityManager
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
