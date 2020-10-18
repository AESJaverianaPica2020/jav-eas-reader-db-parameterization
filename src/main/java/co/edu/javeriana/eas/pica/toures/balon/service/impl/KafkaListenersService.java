package co.edu.javeriana.eas.pica.toures.balon.service.impl;

import co.edu.javeriana.eas.pica.toures.balon.service.IProviderHandlerService;
import co.edu.javeriana.eas.pica.toures.balon.utilities.JsonUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaListenersService {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaListenersService.class);

    private IProviderHandlerService providerHandlerService;

    @KafkaListener(topics = "topic-info-reader", groupId = "toures-group")
    public void listenerCatalog(String data) {
        LOGGER.info("INICIA PROCESO DE INTEGRACIÓN CON PROVEEDORES POR MENSAJE RECIBIDO [{}]", JsonUtility.getPlainJson(data));
        providerHandlerService.runProcessGetCatalog(data);
        LOGGER.info("FINALIZA PROCESO DE INTEGRACIÓN CON PROVEEDORES POR MENSAJE RECIBIDO");
    }

    @Autowired
    public void setProviderHandlerService(IProviderHandlerService providerHandlerService) {
        this.providerHandlerService = providerHandlerService;
    }
}
