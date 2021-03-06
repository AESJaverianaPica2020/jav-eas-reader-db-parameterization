package co.edu.javeriana.eas.pica.toures.balon.service.impl;

import co.edu.javeriana.eas.pica.toures.balon.enums.ProviderReaderExceptionCode;
import co.edu.javeriana.eas.pica.toures.balon.exceptions.AbsProviderReaderException;
import co.edu.javeriana.eas.pica.toures.balon.exceptions.impl.ProviderConnectionException;
import co.edu.javeriana.eas.pica.toures.balon.exceptions.impl.ProviderNotFoundException;
import co.edu.javeriana.eas.pica.toures.balon.models.documents.Provider;
import co.edu.javeriana.eas.pica.toures.balon.models.repositories.IProviderRepository;
import co.edu.javeriana.eas.pica.toures.balon.service.IProviderReaderService;
import co.edu.javeriana.eas.pica.toures.balon.utilities.JsonUtility;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProviderReaderServiceImpl implements IProviderReaderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProviderReaderServiceImpl.class);

    private IProviderRepository providerRepository;

    @Override
    public JsonNode findProvidersByType(String type) throws AbsProviderReaderException {
        LOGGER.info("INICIA CONSULTA DE PROVEEDORES POR TIPO DE SERVICIO.");
        try {
            Map<String, JsonNode> providersSettings = new HashMap<>();
            List<Provider> providers = providerRepository.findProviderByType(type).orElseThrow(
                    () -> new ProviderNotFoundException(ProviderReaderExceptionCode.EMPTY_PROVIDER_RESULT));
            LOGGER.info("FINALIZA CONSULTA DE PROVEEDORES POR TIPO DE SERVICIO. [RESPUESTA:{}]", JsonUtility.getPlainJson(providers));
            JsonNode settings = new ObjectMapper().convertValue(providers, ArrayNode.class);
            providersSettings.put("providers", settings);
            return new ObjectMapper().convertValue(providersSettings, JsonNode.class);
        } catch (ProviderNotFoundException ex) {
            LOGGER.info("NO SE ENCONTRO NINGUN RESULTADO DE PROVEEDORES POR TIPO DE SERVICIO.");
            throw ex;
        } catch (Exception ex) {
            LOGGER.error("ERROR EN CONSULTA DE PROVEEDORES.", ex);
            throw new ProviderConnectionException(ProviderReaderExceptionCode.TPC_ESTABLISH_CONNECTION);
        }
    }

    @Override
    public JsonNode findProviderByNameAndType(String name, String type) throws AbsProviderReaderException {
        LOGGER.info("INICIA CONSULTA DE PROVEEDOR POR NOMBRE.");
        try {
            Map<String, JsonNode> providersSettings = new HashMap<>();
            List<Provider> providers = providerRepository.findProviderByNameAndAndType(name, type).orElseThrow(
                    () -> new ProviderNotFoundException(ProviderReaderExceptionCode.EMPTY_PROVIDER_RESULT));
            LOGGER.info("FINALIZA CONSULTA DE PROVEEDOR POR NOMBRE. [RESPUESTA:{}]", JsonUtility.getPlainJson(providers));
            JsonNode settings = new ObjectMapper().convertValue(providers, ArrayNode.class);
            providersSettings.put("providers", settings);
            return new ObjectMapper().convertValue(providersSettings, JsonNode.class);
        } catch (ProviderNotFoundException ex) {
            LOGGER.info("NO SE ENCONTROPROVEEDOR POR NOMBRE.");
            throw ex;
        } catch (Exception ex) {
            LOGGER.error("ERROR EN CONSULTA DE PROVEEDORES.", ex);
            throw new ProviderConnectionException(ProviderReaderExceptionCode.TPC_ESTABLISH_CONNECTION);
        }
    }

    @Autowired
    public void setProviderRepository(IProviderRepository providerRepository) {
        this.providerRepository = providerRepository;
    }
}
