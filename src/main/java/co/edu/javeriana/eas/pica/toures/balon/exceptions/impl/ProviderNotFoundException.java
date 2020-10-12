package co.edu.javeriana.eas.pica.toures.balon.exceptions.impl;

import co.edu.javeriana.eas.pica.toures.balon.enums.ProviderReaderExceptionCode;
import co.edu.javeriana.eas.pica.toures.balon.exceptions.AbsProviderReaderException;

public class ProviderNotFoundException extends AbsProviderReaderException {

    public ProviderNotFoundException(ProviderReaderExceptionCode exceptionCode) {
        super(exceptionCode);
    }

    public ProviderNotFoundException(ProviderReaderExceptionCode exceptionCode, String causeMessage) {
        super(exceptionCode, causeMessage);
    }

    public ProviderNotFoundException(ProviderReaderExceptionCode exceptionCode, String causeMessage, Exception e) {
        super(exceptionCode, causeMessage, e);
    }

}
