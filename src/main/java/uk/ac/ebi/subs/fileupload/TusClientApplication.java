package uk.ac.ebi.subs.fileupload;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Prototype application to send files to tus.io server over the HTTP protocol.
 */
@SpringBootApplication
public class TusClientApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(TusClientApplication.class, args);
    }
}
