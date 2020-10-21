package co.edu.javeriana.eas.pica.toures.balon.controller;

import co.edu.javeriana.eas.pica.toures.balon.exceptions.AbsProviderReaderException;
import co.edu.javeriana.eas.pica.toures.balon.service.IProviderReaderService;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@CrossOrigin("*")
@RestController
@RequestMapping("/V1/Enterprise")
public class ProviderReaderController {

    private static Logger LOGGER = LoggerFactory.getLogger(ProviderReaderController.class);

    private IProviderReaderService providerReaderService;

    @GetMapping("providers")
    public ResponseEntity<JsonNode> getProvidersByType(@RequestHeader("X-Type") String type) {
        try {
            LOGGER.info("INICIA PROCESO DE BUSQUEDA DE PROVEEDORES POR TIPO");
            JsonNode providers = providerReaderService.findProvidersByType(type);
            LOGGER.info("FINALIZA PROCESO DE BUSQUEDA DE PROVEEDORES POR TIPO");
            return new ResponseEntity<>(providers, HttpStatus.OK);
        } catch (AbsProviderReaderException ex) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("providers/{name}")
    public ResponseEntity<JsonNode> getProvidersByName(@PathVariable String name, @RequestHeader("X-Type") String type) {
        try {
            LOGGER.info("INICIA PROCESO DE BUSQUEDA DE PROVEEDORES POR NOMBRE");
            JsonNode providers = providerReaderService.findProviderByName(name, type);
            LOGGER.info("FINALIZA PROCESO DE BUSQUEDA DE PROVEEDORES POR NOMBRE");
            return new ResponseEntity<>(providers, HttpStatus.OK);
        } catch (AbsProviderReaderException ex) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Autowired
    public void setProviderReaderService(IProviderReaderService providerReaderService) {
        this.providerReaderService = providerReaderService;
    }
}
