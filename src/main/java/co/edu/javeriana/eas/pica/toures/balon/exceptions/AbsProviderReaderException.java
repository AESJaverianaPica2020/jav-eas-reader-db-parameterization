package co.edu.javeriana.eas.pica.toures.balon.exceptions;

import co.edu.javeriana.eas.pica.toures.balon.enums.ProviderReaderExceptionCode;

public class AbsProviderReaderException extends Exception {

    private final ProviderReaderExceptionCode exceptionCode;

    public AbsProviderReaderException(ProviderReaderExceptionCode exceptionCode) {
        this.exceptionCode = exceptionCode;
    }

    public AbsProviderReaderException(ProviderReaderExceptionCode exceptionCode, String causeMessage) {
        super(causeMessage);
        this.exceptionCode = exceptionCode;
    }

    public AbsProviderReaderException(ProviderReaderExceptionCode exceptionCode, String causeMessage, Exception e) {
        super(causeMessage, e);
        this.exceptionCode = exceptionCode;
    }

    public ProviderReaderExceptionCode getExceptionCode() {
        return exceptionCode;
    }

}
