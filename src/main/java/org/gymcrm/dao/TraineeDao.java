package org.gymcrm.dao;

import lombok.extern.slf4j.Slf4j;
import org.gymcrm.model.Trainee;
import org.gymcrm.storage.InMemoryStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;

@Slf4j
@Repository
public class TraineeDao {
    private static final String NAMESPACE = "TRAINEES";
    private InMemoryStorage storage;

    @Autowired
    public void setStorage(InMemoryStorage storage) {
        this.storage = storage;
    }

    public Trainee save(Trainee trainee) {
        Map<Long, Object> traineesMap = Optional.ofNullable(storage.getStorage().get(NAMESPACE))
                .orElseGet(HashMap::new);

        Long traineeId = Optional.ofNullable(trainee.getId())
                .orElseGet(() -> {
                    Long newId = generateNextId(traineesMap);
                    log.debug("Assigned new ID {} for Trainee", newId);
                    return newId;
                });

        trainee.setId(traineeId);
        traineesMap.put(traineeId, trainee);

        return trainee;
    }

    public Trainee findById(Long id) {
        return (Trainee) storage.getStorage().get(NAMESPACE).get(id);
    }

    public void delete(Long id) {
        Object removed = storage.getStorage().get(NAMESPACE).remove(id);
        if (removed != null) {
            log.debug("Trainee with ID {} was successfully removed from storage", id);
        }
    }

    public List<Trainee> findAll() {
        return storage.getStorage().get(NAMESPACE).values().stream()
                .map(obj -> (Trainee) obj)
                .toList();
    }

    private Long generateNextId(Map<Long, Object> entityMap) {
        if (entityMap == null || entityMap.isEmpty()) {
            return 1L;
        }
        return Collections.max(entityMap.keySet()) + 1;
    }
}