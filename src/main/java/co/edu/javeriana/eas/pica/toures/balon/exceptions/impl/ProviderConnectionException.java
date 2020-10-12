package co.edu.javeriana.eas.pica.toures.balon.exceptions.impl;

import co.edu.javeriana.eas.pica.toures.balon.enums.ProviderReaderExceptionCode;
import co.edu.javeriana.eas.pica.toures.balon.exceptions.AbsProviderReaderException;

public class ProviderConnectionException extends AbsProviderReaderException {

    public ProviderConnectionException(ProviderReaderExceptionCode exceptionCode) {
        super(exceptionCode);
    }

    public ProviderConnectionException(ProviderReaderExceptionCode exceptionCode, String causeMessage) {
        super(exceptionCode, causeMessage);
    }

    public ProviderConnectionException(ProviderReaderExceptionCode exceptionCode, String causeMessage, Exception e) {
        super(exceptionCode, causeMessage, e);
    }

}
