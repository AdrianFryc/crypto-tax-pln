package pl.cryptotax.domain.exception;

public class InvalidTaxYearException extends RuntimeException{
    public InvalidTaxYearException(String message) {
        super(message);
    }
}
