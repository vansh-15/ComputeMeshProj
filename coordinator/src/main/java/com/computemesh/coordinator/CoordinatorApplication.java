
package com.computemesh.coordinator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {
        DataSourceAutoConfiguration.class
})
public class CoordinatorApplication {

    public static void main(String[] args) {
        SpringApplication.run(CoordinatorApplication.class, args);
    }
}
