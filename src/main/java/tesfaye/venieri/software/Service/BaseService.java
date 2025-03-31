package tesfaye.venieri.software.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import tesfaye.venieri.software.Exception.ResourceNotFoundException;

/**
 * Servizio base che fornisce funzionalità comuni per tutti i servizi
 * come logging e gestione delle eccezioni.
 */
public abstract class BaseService {
    protected final Logger logger;

    protected BaseService() {
        this.logger = LoggerFactory.getLogger(getClass());
    }

    /**
     * Gestisce le eccezioni in modo uniforme per tutti i servizi.
     *
     * @param e L'eccezione da gestire
     * @param message Il messaggio di errore da loggare
     * @throws RuntimeException L'eccezione wrappata con informazioni aggiuntive
     */
    protected void handleException(Exception e, String message) {
        logger.error(message, e);
        if (e instanceof ResourceNotFoundException) {
            throw (ResourceNotFoundException) e;
        }
        throw new RuntimeException(message, e);
    }

    /**
     * Logga l'inizio di un'operazione.
     *
     * @param operation Il nome dell'operazione
     * @param details Dettagli aggiuntivi dell'operazione
     */
    protected void logOperationStart(String operation, String details) {
        logger.debug("Inizio operazione: {} - {}", operation, details);
    }

    /**
     * Logga il completamento di un'operazione.
     *
     * @param operation Il nome dell'operazione
     * @param result Il risultato dell'operazione
     */
    protected void logOperationComplete(String operation, Object result) {
        logger.debug("Completamento operazione: {} - Risultato: {}", operation, result);
    }
}