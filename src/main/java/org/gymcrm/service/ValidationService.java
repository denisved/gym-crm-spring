package org.gymcrm.service;

import org.springframework.stereotype.Service;
import java.util.Date;

@Service
public class ValidationService {

    public void validateTraining(String trainingName, Date trainingDate, Number trainingDuration) {
        if (trainingName == null || trainingName.trim().length() < 3) {
            throw new IllegalArgumentException("Назва тренування має містити мінімум 3 символи.");
        }
        if (trainingDate == null) {
            throw new IllegalArgumentException("Дата тренування є обов'язковою.");
        }
        if (trainingDuration == null || trainingDuration.intValue() <= 0) {
            throw new IllegalArgumentException("Тривалість тренування має бути більшою за 0 хвилин.");
        }
    }

    public void validateName(String name, String fieldName) {
        if (name == null || name.trim().length() < 3) {
            throw new IllegalArgumentException(fieldName + " має містити мінімум 3 символи.");
        }
    }

    public void validateDateOfBirth(Date dateOfBirth) {
        if (dateOfBirth != null && dateOfBirth.after(new Date())) {
            throw new IllegalArgumentException("Помилка: Дата народження не може бути в майбутньому часі.");
        }
    }

}