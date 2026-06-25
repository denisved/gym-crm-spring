package org.gymcrm.dao;

import org.gymcrm.model.Trainer;
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
class TrainerDaoTest {

    @Mock
    private InMemoryStorage storage;

    @InjectMocks
    private TrainerDao trainerDao;

    private Map<Long, Object> trainersMap;
    private Map<String, Map<Long, Object>> storageMap;

    @BeforeEach
    void setUp() {
        trainerDao.setStorage(storage);
        trainersMap = new HashMap<>();
        storageMap = new HashMap<>();
        storageMap.put("TRAINERS", trainersMap);
        when(storage.getStorage()).thenReturn(storageMap);
    }

    @Test
    void testSave_NewTrainer() {
        Trainer trainer = new Trainer();
        trainer.setFirstName("Jane");

        Trainer saved = trainerDao.save(trainer);

        assertEquals(1L, saved.getId());
        assertEquals(1, trainersMap.size());
        assertEquals(saved, trainersMap.get(1L));
    }

    @Test
    void testSave_ExistingTrainer() {
        Trainer existing = new Trainer();
        existing.setId(3L);
        trainersMap.put(3L, existing);

        Trainer trainerToSave = new Trainer();
        trainerToSave.setId(3L);
        trainerToSave.setFirstName("Updated");

        Trainer saved = trainerDao.save(trainerToSave);

        assertEquals(3L, saved.getId());
        assertEquals(1, trainersMap.size());
        assertEquals("Updated", ((Trainer) trainersMap.get(3L)).getFirstName());
    }

    @Test
    void testFindById() {
        Trainer trainer = new Trainer();
        trainer.setId(2L);
        trainersMap.put(2L, trainer);

        Trainer found = trainerDao.findById(2L);

        assertEquals(trainer, found);
    }

    @Test
    void testFindById_NotFound() {
        Trainer found = trainerDao.findById(99L);
        assertNull(found);
    }

    @Test
    void testFindAll() {
        Trainer t1 = new Trainer();
        t1.setId(1L);
        Trainer t2 = new Trainer();
        t2.setId(2L);

        trainersMap.put(1L, t1);
        trainersMap.put(2L, t2);

        List<Trainer> all = trainerDao.findAll();

        assertEquals(2, all.size());
        assertTrue(all.contains(t1));
        assertTrue(all.contains(t2));
    }
}