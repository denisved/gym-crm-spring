package org.gymcrm.dao;

import lombok.extern.slf4j.Slf4j;
import org.gymcrm.model.Training;
import org.gymcrm.storage.InMemoryStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
public class TrainingDao {
    private static final String NAMESPACE = "TRAININGS";
    private InMemoryStorage storage;

    @Autowired
    public void setStorage(InMemoryStorage storage) {
        this.storage = storage;
    }

    public Training save(Training training) {
        Map<Long, Object> trainingsMap = storage.getStorage().get(NAMESPACE);

        if (training.getId() == null) {
            training.setId(generateNextId(trainingsMap));
            log.debug("Assigned new ID {} for Training", training.getId());
        }

        trainingsMap.put(training.getId(), training);
        return training;
    }

    public Training findById(Long id) {
        return (Training) storage.getStorage().get(NAMESPACE).get(id);
    }

    public List<Training> findAll() {
        return storage.getStorage().get(NAMESPACE).values().stream()
                .map(obj -> (Training) obj)
                .toList();
    }

    private Long generateNextId(Map<Long, Object> entityMap) {
        if (entityMap == null || entityMap.isEmpty()) {
            return 1L;
        }
        return Collections.max(entityMap.keySet()) + 1;
    }
}