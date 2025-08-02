package org.haykal.emr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
public class EmrApplication {

    public static void main(String[] args) {
        SpringApplication.run(EmrApplication.class, args);
    }

}
