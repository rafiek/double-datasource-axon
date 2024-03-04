package nl.uva.doubledatasourceaxon;

import org.axonframework.springboot.autoconfig.legacyjpa.JpaJavaxAutoConfiguration;
import org.axonframework.springboot.autoconfig.legacyjpa.JpaJavaxEventStoreAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableCaching
@EnableScheduling
@ConfigurationPropertiesScan("nl.uva.doubledatasourceaxon.config")
@SpringBootApplication(exclude = {
        JpaJavaxAutoConfiguration.class,
        JpaJavaxEventStoreAutoConfiguration.class
})
public class DoubleDatasourceAxonApplication {

    public static void main(String[] args) {
        SpringApplication.run(DoubleDatasourceAxonApplication.class, args);
    }

}
