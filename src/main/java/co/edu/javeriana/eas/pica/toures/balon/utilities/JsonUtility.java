package co.edu.javeriana.eas.pica.toures.balon.utilities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonUtility {

    private JsonUtility() {

    }

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonUtility.class);

    public static String getPlainJson(Object object) {
        ObjectMapper objectMapper = new ObjectMapper();
        String json = null;
        try {
            json = objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException var4) {
            LOGGER.error("JsonUtility", var4);
        }
        return json;
    }

}
