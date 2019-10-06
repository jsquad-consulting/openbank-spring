package se.jsquad;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableEncryptableProperties
public class OpenbankSpringApplication {

    public static void main(String[] args) {
        SpringApplication.run(OpenbankSpringApplication.class, args);
    }

}
