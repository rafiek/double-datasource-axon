package nl.uva.doubledatasourceaxon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableCaching
@EnableScheduling
@SpringBootApplication
@ConfigurationPropertiesScan("nl.uva.doubledatasourceaxon.config")
public class DoubleDatasourceAxonApplication {
    public static void main(String[] args) {
        SpringApplication.run(DoubleDatasourceAxonApplication.class, args);
    }
}
