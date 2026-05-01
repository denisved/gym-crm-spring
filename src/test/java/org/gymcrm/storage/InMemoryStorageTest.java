package org.gymcrm.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryStorageTest {

    private InMemoryStorage storage;
    private Path tempFilePath;
    private Map<String, Map<Long, Object>> storageMap;

    @BeforeEach
    void setUp() throws IOException {
        storage = new InMemoryStorage();
        storageMap = new HashMap<>();
        storageMap.put("TRAINEES", new HashMap<>());
        storageMap.put("TRAINERS", new HashMap<>());
        storageMap.put("TRAININGS", new HashMap<>());
        storage.setStorage(storageMap);
        
        tempFilePath = Files.createTempFile("test_storage", ".json");
        ReflectionTestUtils.setField(storage, "filePath", tempFilePath.toString());
    }

    @Test
    void testInit_EmptyFile() {
        storage.init();

        assertNotNull(storage.getStorage());
        assertTrue(storage.getStorage().containsKey("TRAINEES"));
        assertTrue(storage.getStorage().containsKey("TRAINERS"));
        assertTrue(storage.getStorage().containsKey("TRAININGS"));
    }

    @Test
    void testInit_WithData() throws IOException {
        String json = "{\n" +
                "  \"TRAINERS\": {\n" +
                "    \"1\": { \"id\": 1, \"firstName\": \"John\" }\n" +
                "  },\n" +
                "  \"TRAINEES\": {},\n" +
                "  \"TRAININGS\": {}\n" +
                "}";
        Files.writeString(tempFilePath, json);

        storage.init();

        assertNotNull(storage.getStorage());
        assertFalse(storage.getStorage().get("TRAINERS").isEmpty());
    }

    @Test
    void testInit_InvalidJson() throws IOException {
        Files.writeString(tempFilePath, "invalid json");

        storage.init();

        assertNotNull(storage.getStorage());
        assertTrue(storage.getStorage().get("TRAINERS").isEmpty());
    }

    @Test
    void testDestroy() throws IOException {
        storage.init();
        storage.destroy();

        assertTrue(Files.exists(tempFilePath));
        String content = Files.readString(tempFilePath);
        assertTrue(content.contains("TRAINERS"));
        assertTrue(content.contains("TRAINEES"));
        assertTrue(content.contains("TRAININGS"));
    }
}
