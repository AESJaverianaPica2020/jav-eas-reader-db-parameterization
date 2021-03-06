package co.edu.javeriana.eas.pica.toures.balon.service.impl;

import co.edu.javeriana.eas.pica.toures.balon.enums.ProviderReaderExceptionCode;
import co.edu.javeriana.eas.pica.toures.balon.exceptions.AbsProviderReaderException;
import co.edu.javeriana.eas.pica.toures.balon.exceptions.impl.ProviderConnectionException;
import co.edu.javeriana.eas.pica.toures.balon.service.IProviderHandlerService;
import co.edu.javeriana.eas.pica.toures.balon.service.IProviderReaderService;
import co.edu.javeriana.eas.pica.toures.balon.utilities.JsonUtility;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProviderHandlerServiceImpl implements IProviderHandlerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProviderHandlerServiceImpl.class);

    private static final String RESERVE_TYPE = "reserve";

    private enum EProcessType {
        RESERVE, CATALOG;
    }

    private String endpoint;
    private String path;

    private KafkaSenderService kafkaSenderService;
    private IProviderReaderService providerReaderService;
    private RestTemplate restTemplate;

    @Override
    public void runProcessGetCatalog(String message) {
        try {
            LOGGER.info("INICIA PROCESO DE RECUPERACIÓN DE DATOS DE PROVEEDORES - CATALOGOS -");
            JsonNode serializeMessage = getMessageInFormatJson(message);
            EProcessType processType = defineProcessType(serializeMessage);
            String providerType = getProviderTypeFromMessage(serializeMessage);
            JsonNode providersSettings;
            String uuid = serializeMessage.get("Uuid").asText();
            if (processType == EProcessType.RESERVE) {
                String providerName = serializeMessage.get("Nombre_proveedor").asText();
                providersSettings = providerReaderService.findProviderByNameAndType(providerName, providerType);
            } else {
                providersSettings = providerReaderService.findProvidersByType(providerType);
                sendTopicToRules(providersSettings, uuid);
            }
            ((ObjectNode) providersSettings).set("parameters", serializeMessage.get("Parametros"));
            ((ObjectNode) providersSettings).put("processType", processType.name());
            ((ObjectNode) providersSettings).put("Uuid", uuid);
            JsonNode catalogProviders = sendToTransformAndGetCatalog(providersSettings);
            ((ObjectNode) catalogProviders).put("type", serializeMessage.get("Tipo_proveedor").asText());
            ((ObjectNode) catalogProviders).set("parameters", serializeMessage.get("Parametros"));
            kafkaSenderService.sendMessage(catalogProviders, null);
            LOGGER.info("INICIA PROCESO DE RECUPERACIÓN DE DATOS DE PROVEEDORES - FINALIZA -");
        } catch (AbsProviderReaderException | JsonProcessingException | NullPointerException ex) {
            LOGGER.error("ERROR EN RECUPERACIÓN DE CATALOGOS DE PROVEEDORES.", ex);
        }
    }

    private void sendTopicToRules(JsonNode providersSettings, String uuid) {
        LOGGER.info("inicia envio de mensaje para regla de negocio [uuid: {}]", uuid);
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode requestRule = objectMapper.createObjectNode();
        requestRule.putPOJO("uid", uuid);
        List<ObjectNode> providerList = new ArrayList<>();
        providersSettings.get("providers").forEach(provider -> {
            ObjectNode pr = objectMapper.createObjectNode();
            pr.putPOJO("name", provider.get("name").asText());
            pr.putPOJO("type", provider.get("type").asText());
            providerList.add(pr);
        });
        requestRule.putPOJO("provider", providerList);
        kafkaSenderService.sendMessage(requestRule, "topic-rules");
        LOGGER.info("finaliza envio de mensaje para regla de negocio [uuid: {}]", uuid);
    }

    private EProcessType defineProcessType(JsonNode message) {
        LOGGER.info("inicia identificación de tipo de proceso a ejecutar");
        String processTypeInput = message.get("Tipo_proceso").asText();
        EProcessType processType = processTypeInput.equals(RESERVE_TYPE) ? EProcessType.RESERVE : EProcessType.CATALOG;
        LOGGER.info("finaliza identificación de tipo de proceso a ejecutar [TIPO:{}]", processType);
        return processType;
    }

    private JsonNode getMessageInFormatJson(String message) throws JsonProcessingException {
        LOGGER.info("inicia transformación de mensaje de String a Json.");
        ObjectMapper mapper = new ObjectMapper();
        JsonNode serializeMessage = mapper.readTree(message);
        LOGGER.info("finaliza transformación de mensaje de String a Json. [MESSAGE:{}]", JsonUtility.getPlainJson(serializeMessage));
        return serializeMessage;
    }

    private String getProviderTypeFromMessage(JsonNode message) {
        LOGGER.info("inicia busqueda de tipo de proveedor por mensaje kafka");
        String providerType = message.get("Tipo_proveedor").asText();
        LOGGER.info("finaliza busqueda de tipo de proveedor por mensaje kafka. [TIPO:{}]", providerType);
        return providerType;
    }

    private JsonNode sendToTransformAndGetCatalog(JsonNode payload) throws ProviderConnectionException {
        LOGGER.info("inicia integración con transformador para obtener catalogos de proveedores [DATOS:{}]", JsonUtility.getPlainJson(payload));
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
