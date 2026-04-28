package org.gymcrm.storage;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.gymcrm.model.Trainee;
import org.gymcrm.model.Trainer;
import org.gymcrm.model.Training;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class InMemoryStorage {

    @Value("${storage.file.path}")
    private String filePath;

    @Getter
    private Map<String, Map<Long, Object>> storage;

    @Autowired
    public void setStorage(Map<String, Map<Long, Object>> commonStorageMap) {
        this.storage = commonStorageMap;
    }

    private final ObjectMapper mapper = new ObjectMapper();

    @PostConstruct
    public void init() {
        File file = new File(filePath);
        if (file.exists() && file.length() > 0) {
            try {
                Map<String, Map<Long, Object>> rawStorage = mapper.readValue(file, new TypeReference<Map<String, Map<Long, Object>>>() {});

                storage.put("TRAINERS", convertNamespace(rawStorage.get("TRAINERS"), Trainer.class));
                storage.put("TRAINEES", convertNamespace(rawStorage.get("TRAINEES"), Trainee.class));
                storage.put("TRAININGS", convertNamespace(rawStorage.get("TRAININGS"), Training.class));

                log.info("Storage initialized and mapped successfully from file: {}", filePath);
            } catch (IOException e) {
                log.error("Failed to read storage file, initializing empty storage", e);
                initializeEmptyStorage();
            }
        } else {
            log.info("Storage file not found or empty. Initializing empty storage at: {}", filePath);
            initializeEmptyStorage();
        }
    }

    @PreDestroy
    public void destroy() {
        try {
            File file = new File(filePath);
            file.getParentFile().mkdirs();
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, storage);
            log.info("Data successfully saved to file: {} upon shutdown", filePath);
        } catch (IOException e) {
            log.error("Failed to save data to file: {}", filePath, e);
        }
    }

    private void initializeEmptyStorage() {
        storage.put("TRAINERS", new HashMap<>());
        storage.put("TRAINEES", new HashMap<>());
        storage.put("TRAININGS", new HashMap<>());
    }

    private Map<Long, Object> convertNamespace(Map<Long, Object> rawMap, Class<?> clazz) {
        Map<Long, Object> typedMap = new HashMap<>();
        if (rawMap != null) {
            for (Map.Entry<Long, Object> entry : rawMap.entrySet()) {
                typedMap.put(entry.getKey(), mapper.convertValue(entry.getValue(), clazz));
            }
        }
        return typedMap;
    }
}