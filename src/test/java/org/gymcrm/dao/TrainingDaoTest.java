package org.gymcrm.dao;

import org.gymcrm.model.Training;
import org.gymcrm.storage.InMemoryStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingDaoTest {

    @Mock
    private InMemoryStorage storage;

    @InjectMocks
    private TrainingDao trainingDao;

    private Map<Long, Object> trainingsMap;
    private Map<String, Map<Long, Object>> storageMap;

    @BeforeEach
    void setUp() {
        trainingDao.setStorage(storage);
        trainingsMap = new HashMap<>();
        storageMap = new HashMap<>();
        storageMap.put("TRAININGS", trainingsMap);
        when(storage.getStorage()).thenReturn(storageMap);
    }

    @Test
    void testSave_NewTraining() {
        Training training = new Training();
        training.setTrainingName("Session1");

        Training saved = trainingDao.save(training);

        assertEquals(1L, saved.getId());
        assertEquals(1, trainingsMap.size());
        assertEquals(saved, trainingsMap.get(1L));
    }

    @Test
    void testSave_ExistingTraining() {
        Training existing = new Training();
        existing.setId(4L);
        trainingsMap.put(4L, existing);

        Training trainingToSave = new Training();
        trainingToSave.setId(4L);
        trainingToSave.setTrainingName("Updated");

        Training saved = trainingDao.save(trainingToSave);

        assertEquals(4L, saved.getId());
        assertEquals(1, trainingsMap.size());
        assertEquals("Updated", ((Training) trainingsMap.get(4L)).getTrainingName());
    }

    @Test
    void testFindById() {
        Training training = new Training();
        training.setId(2L);
        trainingsMap.put(2L, training);

        Training found = trainingDao.findById(2L);

        assertEquals(training, found);
    }

    @Test
    void testFindById_NotFound() {
        Training found = trainingDao.findById(99L);
        assertNull(found);
    }

    @Test
    void testFindAll() {
        Training t1 = new Training();
        t1.setId(1L);
        Training t2 = new Training();
        t2.setId(2L);

        trainingsMap.put(1L, t1);
        trainingsMap.put(2L, t2);

        List<Training> all = trainingDao.findAll();

        assertEquals(2, all.size());
        assertTrue(all.contains(t1));
        assertTrue(all.contains(t2));
    }
}