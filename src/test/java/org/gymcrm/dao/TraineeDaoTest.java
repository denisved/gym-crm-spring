package org.gymcrm.dao;

import org.gymcrm.model.Trainee;
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
class TraineeDaoTest {

    @Mock
    private InMemoryStorage storage;

    @InjectMocks
    private TraineeDao traineeDao;

    private Map<Long, Object> traineesMap;
    private Map<String, Map<Long, Object>> storageMap;

    @BeforeEach
    void setUp() {
        traineeDao.setStorage(storage);
        traineesMap = new HashMap<>();
        storageMap = new HashMap<>();
        storageMap.put("TRAINEES", traineesMap);
        when(storage.getStorage()).thenReturn(storageMap);
    }

    @Test
    void testSave_NewTrainee() {
        Trainee trainee = new Trainee();
        trainee.setFirstName("John");
        
        Trainee saved = traineeDao.save(trainee);
        
        assertEquals(1L, saved.getId());
        assertEquals(1, traineesMap.size());
        assertEquals(saved, traineesMap.get(1L));
    }

    @Test
    void testSave_ExistingTrainee() {
        Trainee existing = new Trainee();
        existing.setId(5L);
        traineesMap.put(5L, existing);

        Trainee traineeToSave = new Trainee();
        traineeToSave.setId(5L);
        traineeToSave.setFirstName("Updated");

        Trainee saved = traineeDao.save(traineeToSave);

        assertEquals(5L, saved.getId());
        assertEquals(1, traineesMap.size());
        assertEquals("Updated", ((Trainee) traineesMap.get(5L)).getFirstName());
    }

    @Test
    void testFindById() {
        Trainee trainee = new Trainee();
        trainee.setId(2L);
        traineesMap.put(2L, trainee);

        Trainee found = traineeDao.findById(2L);

        assertEquals(trainee, found);
    }

    @Test
    void testFindById_NotFound() {
        Trainee found = traineeDao.findById(99L);
        assertNull(found);
    }

    @Test
    void testDelete() {
        Trainee trainee = new Trainee();
        trainee.setId(3L);
        traineesMap.put(3L, trainee);

        traineeDao.delete(3L);

        assertEquals(0, traineesMap.size());
    }

    @Test
    void testFindAll() {
        Trainee t1 = new Trainee();
        t1.setId(1L);
        Trainee t2 = new Trainee();
        t2.setId(2L);
        
        traineesMap.put(1L, t1);
        traineesMap.put(2L, t2);

        List<Trainee> all = traineeDao.findAll();
        
        assertEquals(2, all.size());
        assertTrue(all.contains(t1));
        assertTrue(all.contains(t2));
    }
}