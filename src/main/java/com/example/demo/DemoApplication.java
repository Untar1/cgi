package com.example.demo;

import com.example.demo.exceptions.StorageException;
import com.example.demo.jython.PythonService;
import com.example.demo.jython.PythonServiceFactory;
import com.example.demo.services.StorageService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Bean
    CommandLineRunner init(StorageService service) {
        return (args) -> {
            //TODO: need to remove the "deletall"
            try {
                service.loadAll();
            } catch(StorageException se) {
                service.deleteAll();
                service.init();
            }
        };
    }
    @Bean(name = "PythonServiceFactory")
    public PythonServiceFactory pythonServiceFactory() {
        return new PythonServiceFactory();
    }
    @Bean(name = "PythonServicePython")
    public PythonService pythonServicePython() throws Exception {
        return pythonServiceFactory().getObject();
    }

}
