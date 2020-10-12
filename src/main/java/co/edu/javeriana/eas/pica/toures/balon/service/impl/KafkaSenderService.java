package co.edu.javeriana.eas.pica.toures.balon.service.impl;

import co.edu.javeriana.eas.pica.toures.balon.utilities.JsonUtility;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaSenderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaSenderService.class);

    private String topicProducer;

    private KafkaTemplate<String, Object> kafkaTemplate;

    public void sendMessage(JsonNode message) {
        LOGGER.info("INICIA PROCESO DE ENVIO DE MENSAJE A TOPICO [{}] CON MENSAJE [{}]", topicProducer, JsonUtility.getPlainJson(message));
        kafkaTemplate.send(topicProducer, message);
        LOGGER.info("INICIA PROCESO DE ENVIO DE MENSAJE A TOPICO [{}]", topicProducer);
    }

    @Autowired
    public void setKafkaTemplate(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Value("${toures.balon.kafka.producer.topic}")
    public void setTopicProducer(String topicProducer) {
        this.topicProducer = topicProducer;
    }
}
