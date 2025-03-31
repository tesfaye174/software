package tesfaye.venieri.software.Exception;

public class GameSessionException extends RuntimeException {
    public GameSessionException(String message) {
        super(message);
    }
    
    public GameSessionException(String message, Throwable cause) {
        super(message, cause);
    }
} 