package org.gymcrm.dao;

import lombok.extern.slf4j.Slf4j;
import org.gymcrm.model.Trainer;
import org.gymcrm.storage.InMemoryStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
public class TrainerDao {
    private static final String NAMESPACE = "TRAINERS";
    private InMemoryStorage storage;

    @Autowired
    public void setStorage(InMemoryStorage storage) {
        this.storage = storage;
    }

    public Trainer save(Trainer trainer) {
        Map<Long, Object> trainersMap = storage.getStorage().get(NAMESPACE);

        if (trainer.getId() == null) {
            trainer.setId(generateNextId(trainersMap));
            log.debug("Assigned new ID {} for Trainer", trainer.getId());
        }

        trainersMap.put(trainer.getId(), trainer);
        return trainer;
    }

    public Trainer findById(Long id) {
        return (Trainer) storage.getStorage().get(NAMESPACE).get(id);
    }

    public List<Trainer> findAll() {
        return storage.getStorage().get(NAMESPACE).values().stream()
                .map(obj -> (Trainer) obj)
                .toList();
    }

    private Long generateNextId(Map<Long, Object> entityMap) {
        if (entityMap == null || entityMap.isEmpty()) {
            return 1L;
        }
        return Collections.max(entityMap.keySet()) + 1;
    }
}