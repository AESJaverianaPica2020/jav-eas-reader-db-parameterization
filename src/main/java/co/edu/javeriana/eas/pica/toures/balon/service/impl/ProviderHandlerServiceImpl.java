package co.edu.javeriana.eas.pica.toures.balon.service.impl;

import co.edu.javeriana.eas.pica.toures.balon.enums.ProviderReaderExceptionCode;
import co.edu.javeriana.eas.pica.toures.balon.exceptions.AbsProviderReaderException;
import co.edu.javeriana.eas.pica.toures.balon.exceptions.impl.ProviderConnectionException;
import co.edu.javeriana.eas.pica.toures.balon.service.IProviderHandlerService;
import co.edu.javeriana.eas.pica.toures.balon.service.IProviderReaderService;
import co.edu.javeriana.eas.pica.toures.balon.utilities.JsonUtility;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Service
public class ProviderHandlerServiceImpl implements IProviderHandlerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProviderHandlerServiceImpl.class);

    private String endpoint;
    private String path;

    private KafkaSenderService kafkaSenderService;
    private IProviderReaderService providerReaderService;
    private RestTemplate restTemplate;

    @Override
    public void runProcessGetCatalog(JsonNode message) {
        try {
            LOGGER.info("INICIA PROCESO DE RECUPERACIÓN DE DATOS DE PROVEEDORES - CATALOGOS -");
            String providerType = getProviderTypeFromMessage(message);
            JsonNode providersSettings = providerReaderService.findProvidersByType(providerType);
            JsonNode catalogProviders = sendToTransformAndGetCatalog(providersSettings);
            kafkaSenderService.sendMessage(catalogProviders);
            LOGGER.info("INICIA PROCESO DE RECUPERACIÓN DE DATOS DE PROVEEDORES - FINALIZA -");
        } catch (AbsProviderReaderException ex) {

        }
    }

    private String getProviderTypeFromMessage(JsonNode message) {
        LOGGER.info("inicia busqueda de tipo de proveedor por mensaje kafka");
        String providerType = message.get("providerType").asText();
        LOGGER.info("finaliza busqueda de tipo de proveedor por mensaje kafka. [TIPO:{}]", providerType);
        return providerType;
    }

    private JsonNode sendToTransformAndGetCatalog(JsonNode payload) throws ProviderConnectionException {
        LOGGER.info("inicia integración con transformador para obtener catalogos de proveedores.");
        UriComponentsBuilder urlBuilder = UriComponentsBuilder.fromUriString(endpoint).path(path);
        URI uri = urlBuilder.build().encode().toUri();
        try {
            JsonNode responseProvidersCatalog = restTemplate.postForEntity(uri.toString(), payload, JsonNode.class).getBody();
            LOGGER.info("finaliza integración con transformador para obtener catalogos de proveedores. [DATOS:{}]", JsonUtility.getPlainJson(responseProvidersCatalog));
            return responseProvidersCatalog;
        } catch (HttpStatusCodeException ex) {
            throw new ProviderConnectionException(ProviderReaderExceptionCode.REST_ESTABLISH_CONNECTION, "ERROR en conexión con el transfomador");
        }
    }

    @Autowired
    public void setKafkaSenderService(KafkaSenderService kafkaSenderService) {
        this.kafkaSenderService = kafkaSenderService;
    }

    @Autowired
    public void setProviderReaderService(IProviderReaderService providerReaderService) {
        this.providerReaderService = providerReaderService;
    }

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Value("${toures.balon.endpoint.transformer}")
    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    @Value("${toures.balon.path.transformer}")
    public void setPath(String path) {
        this.path = path;
    }

}