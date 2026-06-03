package org.gymcrm.service;

import org.junit.jupiter.api.Test;
import java.util.Date;
import java.util.Calendar;
import static org.junit.jupiter.api.Assertions.*;

class ValidationServiceTest {

    private final ValidationService validationService = new ValidationService();

    @Test
    void validateTraining_ValidInputs_NoException() {
        assertDoesNotThrow(() -> validationService.validateTraining("Yoga", new Date(), 60));
    }

    @Test
    void validateTraining_NullName_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> validationService.validateTraining(null, new Date(), 60));
    }

    @Test
    void validateTraining_ShortName_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> validationService.validateTraining("Yo", new Date(), 60));
    }

    @Test
    void validateTraining_NullDate_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> validationService.validateTraining("Yoga", null, 60));
    }

    @Test
    void validateTraining_NullDuration_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> validationService.validateTraining("Yoga", new Date(), null));
    }

    @Test
    void validateTraining_ZeroDuration_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> validationService.validateTraining("Yoga", new Date(), 0));
    }

    @Test
    void validateName_ValidName_NoException() {
        assertDoesNotThrow(() -> validationService.validateName("John", "First Name"));
    }

    @Test
    void validateName_NullName_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> validationService.validateName(null, "First Name"));
    }

    @Test
    void validateName_ShortName_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> validationService.validateName("Jo", "First Name"));
    }

    @Test
    void validateDateOfBirth_ValidDate_NoException() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, -20);
        assertDoesNotThrow(() -> validationService.validateDateOfBirth(cal.getTime()));
    }

    @Test
    void validateDateOfBirth_NullDate_NoException() {
        assertDoesNotThrow(() -> validationService.validateDateOfBirth(null));
    }

    @Test
    void validateDateOfBirth_FutureDate_ThrowsException() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, 1);
        assertThrows(IllegalArgumentException.class, () -> validationService.validateDateOfBirth(cal.getTime()));
    }
}
