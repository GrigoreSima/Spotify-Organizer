package organizer.backend.repositories;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.HashMap;

@Repository
public class LocalStorageRepository {
    Logger logger = LoggerFactory.getLogger(LocalStorageRepository.class);

    private final HashMap<String, String> states = new HashMap<>();

    public String getState(String key) {
        String state = this.states.get(key);
        logger.info("Got state = {} with key = {}", state, key);
        return state;
    }

    public void addState(String key, String value) {
        logger.info("Added state = {} with key = {}", value, key);
        this.states.put(key, value);
    }

    public void removeState(String key) {
        logger.info("Removed state with key = {}", key);
        this.states.remove(key);
    }
}
