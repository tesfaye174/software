package tesfaye.venieri.software;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class SoftwareApplication {

    private static final Logger logger = LoggerFactory.getLogger(SoftwareApplication.class);
    
    @Autowired
    private Environment environment;

    public static void main(String[] args) {
        try {
            SpringApplication app = new SpringApplication(SoftwareApplication.class);
            app.run(args);
            logger.info("Applicazione avviata con successo");
        } catch (Exception e) {
            logger.error("Errore critico durante l'avvio dell'applicazione", e);
            System.exit(1);
        }
    }

    @Bean
    @Profile("!prod")
    public CommandLineRunner initData() {
        return args -> {
            try {
                logger.info("Ambiente attivo: {}", String.join(", ", environment.getActiveProfiles()));
                logger.info("Inizializzazione dei dati di esempio...");
                
                // Qui verranno aggiunti i dati di esempio quando verranno create le entità
                logger.info("Inizializzazione dei dati completata con successo");
            } catch (Exception e) {
                logger.error("Errore durante l'inizializzazione dei dati", e);
                throw e;
            }
        };
    }
}
