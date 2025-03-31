package tesfaye.venieri;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

@SpringBootApplication
@EnableCaching
public class SoftwareApplication {
    private static final Logger logger = LoggerFactory.getLogger(SoftwareApplication.class);

    public static void main(String[] args) {
        try {
            logger.info("Avvio dell'applicazione...");
            SpringApplication.run(SoftwareApplication.class, args);
            logger.info("Applicazione avviata con successo");
        } catch (Exception e) {
            logger.error("Errore durante l'avvio dell'applicazione", e);
            throw e;
        }
    }

    @Bean
    @Profile("!prod")
    public CommandLineRunner initializeTestData() {
        return args -> {
            try {
                logger.info("Inizializzazione dei dati di test...");
                // Qui verranno aggiunti i dati di esempio per il testing
                logger.info("Dati di test inizializzati con successo");
            } catch (Exception e) {
                logger.error("Errore durante l'inizializzazione dei dati di test", e);
                throw e;
            }
        };
    }
}