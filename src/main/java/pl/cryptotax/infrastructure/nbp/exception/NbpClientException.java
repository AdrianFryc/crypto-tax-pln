package pl.cryptotax.infrastructure.nbp.exception;

public class NbpClientException extends RuntimeException{
    public NbpClientException(String message) {
        super(message);
    }

    public NbpClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
