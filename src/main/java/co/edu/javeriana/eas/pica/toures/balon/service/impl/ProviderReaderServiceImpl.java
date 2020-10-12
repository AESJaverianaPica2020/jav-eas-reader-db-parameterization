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

import java.util.List;

@Service
public class ProviderReaderServiceImpl implements IProviderReaderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProviderReaderServiceImpl.class);

    private IProviderRepository providerRepository;

    @Override
    public JsonNode findProvidersByType(String type) throws AbsProviderReaderException {
        LOGGER.info("INICIA CONSULTA DE PROVEEDORES POR TIPO DE SERVICIO.");
        try {
            List<Provider> providers = providerRepository.findProviderByType(type).orElseThrow(
                    () -> new ProviderNotFoundException(ProviderReaderExceptionCode.EMPTY_PROVIDER_RESULT));
            LOGGER.info("FINALIZA CONSULTA DE PROVEEDORES POR TIPO DE SERVICIO. [RESPUESTA:{}]", JsonUtility.getPlainJson(providers));
            return new ObjectMapper().convertValue(providers, ArrayNode.class);
        } catch (ProviderNotFoundException ex) {
            LOGGER.info("NO SE ENCONTRO NINGUN RESULTADO DE PROVEEDORES POR TIPO DE SERVICIO.");
            throw ex;
        } catch (Exception ex) {
            LOGGER.error("ERROR EN CONSULTA DE PROVEEDORES.", ex);
            throw new ProviderConnectionException(ProviderReaderExceptionCode.TPC_ESTABLISH_CONNECTION);
        }
    }

    @Override
    public JsonNode findProviderByName(String name) throws AbsProviderReaderException {
        LOGGER.info("INICIA CONSULTA DE PROVEEDOR POR NOMBRE.");
        try {
            Provider provider = providerRepository.findProviderByName(name).orElseThrow(
                    () -> new ProviderNotFoundException(ProviderReaderExceptionCode.EMPTY_PROVIDER_RESULT));
            LOGGER.info("FINALIZA CONSULTA DE PROVEEDOR POR NOMBRE. [RESPUESTA:{}]", JsonUtility.getPlainJson(provider));
            return new ObjectMapper().convertValue(provider, JsonNode.class);
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
