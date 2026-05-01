package org.gymcrm.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CredentialsGeneratorTest {

    private CredentialsGenerator generator;

    @BeforeEach
    void setUp() {
        generator = new CredentialsGenerator();
    }

    @Test
    void testGeneratePassword() {
        String password = generator.generatePassword();
        assertNotNull(password);
        assertEquals(10, password.length());
    }

    @Test
    void testGenerateUsername_FirstTime() {
        String username = generator.generateUsername("John", "Doe", List.of());
        assertEquals("John.Doe", username);
    }

    @Test
    void testGenerateUsername_WithExistingExactMatch() {
        String username = generator.generateUsername("John", "Doe", List.of("John.Doe"));
        assertEquals("John.Doe1", username);
    }

    @Test
    void testGenerateUsername_WithMultipleExisting() {
        String username = generator.generateUsername("John", "Doe", List.of("John.Doe", "John.Doe1", "John.Doe2"));
        assertEquals("John.Doe3", username);
    }

    @Test
    void testGenerateUsername_WithGapInExisting() {
        String username = generator.generateUsername("John", "Doe", List.of("John.Doe", "John.Doe3"));
        assertEquals("John.Doe4", username);
    }
}