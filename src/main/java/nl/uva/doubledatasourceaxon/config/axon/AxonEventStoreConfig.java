package nl.uva.doubledatasourceaxon.config.axon;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.common.jpa.EntityManagerProvider;
import org.axonframework.common.transaction.TransactionManager;
import org.axonframework.spring.messaging.unitofwork.SpringTransactionManager;
import org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Configuration
@EnableTransactionManagement
public class AxonEventStoreConfig {

    @Bean("axonAohiDataSource")
    @ConfigurationProperties("spring.datasource.hikari.axon-aohi")
    public DataSource axonAohi() {
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }

    @Bean(name="axonEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("axonAohiDataSource") DataSource axonAohi
    ) {
        return builder
                .dataSource(axonAohi)
                .persistenceUnit("axonAohi")
                .properties(jpaProperties())
                .packages("org.axonframework.eventhandling.tokenstore",
                        "org.axonframework.modelling.saga.repository.jpa",
                        "org.axonframework.eventhandling.deadletter.jpa",
                        "org.axonframework.eventsourcing.eventstore.jpa"
                        )
                .build();
    }

    /**
     * For axon framework
     * @param entityManagerFactory
     * @return
     */
    @Bean
    public EntityManagerProvider entityManagerProvider(
            @Qualifier("axonEntityManagerFactory") LocalContainerEntityManagerFactoryBean entityManagerFactory
    ) {
        return () -> entityManagerFactory.getObject().createEntityManager();
    }

    @Bean
    public TransactionManager axonTransactionManager(
            @Qualifier("axonEntityManagerFactory") LocalContainerEntityManagerFactoryBean axonEntityManagerFactory
    ) {
        return new SpringTransactionManager(
                new JpaTransactionManager(Objects.requireNonNull(axonEntityManagerFactory.getObject()))
        );
    }

    private Map<String, Object> jpaProperties() {
        Map<String, Object> props = new HashMap<>();
        props.put("hibernate.physical_naming_strategy", CamelCaseToUnderscoresNamingStrategy.class.getName());
        props.put("hibernate.implicit_naming_strategy", SpringImplicitNamingStrategy.class.getName());
        props.put("hibernate.hbm2ddl.auto", "create");
        props.put("hibernate.show_sql", "update");
        props.put("hibernate.dialect", "nl.uva.doubledatasourceaxon.config.axon.AxonPostgreSQLDialect");
        return props;
    }
}
