package co.edu.javeriana.eas.pica.toures.balon.service;

import co.edu.javeriana.eas.pica.toures.balon.exceptions.AbsProviderReaderException;
import com.fasterxml.jackson.databind.JsonNode;

public interface IProviderReaderService {

    JsonNode findProvidersByType(String type) throws AbsProviderReaderException;

    JsonNode findProviderByName(String name) throws AbsProviderReaderException;

}
