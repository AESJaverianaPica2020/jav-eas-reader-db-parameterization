package co.edu.javeriana.eas.pica.toures.balon.service;

import com.fasterxml.jackson.databind.JsonNode;

public interface IProviderHandlerService {

    void runProcessGetCatalog(JsonNode messageKafka);

}
