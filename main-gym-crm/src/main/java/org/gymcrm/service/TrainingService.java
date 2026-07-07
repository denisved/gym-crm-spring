package org.gymcrm.service;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.gymcrm.client.WorkloadServiceClient;
import org.gymcrm.dto.WorkloadRequest;
import org.gymcrm.model.Trainee;
import org.gymcrm.model.Trainer;
import org.gymcrm.model.Training;
import org.gymcrm.repository.TraineeRepository;
import org.gymcrm.repository.TrainerRepository;
import org.gymcrm.repository.TrainingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class TrainingService {

    private final TrainingRepository trainingRepository;
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final ValidationService validationService;

    
    private final WorkloadServiceClient workloadServiceClient;
    private final CircuitBreakerFactory<?, ?> circuitBreakerFactory;

    @Autowired
    public TrainingService(TrainingRepository trainingRepository,
                           TraineeRepository traineeRepository,
                           TrainerRepository trainerRepository,
                           ValidationService validationService,
                           WorkloadServiceClient workloadServiceClient,
                           CircuitBreakerFactory<?, ?> circuitBreakerFactory) {
        this.trainingRepository = trainingRepository;
        this.traineeRepository = traineeRepository;
        this.trainerRepository = trainerRepository;
        this.validationService = validationService;
        this.workloadServiceClient = workloadServiceClient;
        this.circuitBreakerFactory = circuitBreakerFactory;
    }

    @Transactional
    public Training createTraining(String traineeUsername, String trainerUsername, String trainingName, Date trainingDate, Number trainingDuration) {
        validationService.validateTraining(trainingName, trainingDate, trainingDuration);

        log.info("Creating training: {} for Trainee: {} and Trainer: {}", trainingName, traineeUsername, trainerUsername);

        Trainee trainee = traineeRepository.findByUsername(traineeUsername)
                .orElseThrow(() -> new IllegalArgumentException("Trainee not found with username: " + traineeUsername));

        Trainer trainer = trainerRepository.findByUsername(trainerUsername)
                .orElseThrow(() -> new IllegalArgumentException("Trainer not found with username: " + trainerUsername));

        Training training = new Training();
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setTrainingName(trainingName.trim());
        training.setTrainingDate(trainingDate);
        training.setTrainingDuration(trainingDuration);
        training.setTrainingType(trainer.getSpecialization());

        Training savedTraining = trainingRepository.save(training);

        
        WorkloadRequest workloadRequest = new WorkloadRequest(
                trainer.getUsername(),
                trainer.getFirstName(),
                trainer.getLastName(),
                trainer.isActive(),
                savedTraining.getTrainingDate(),
                savedTraining.getTrainingDuration().intValue(), 
                "ADD"
        );
        sendWorkloadWithCircuitBreaker(workloadRequest);

        return savedTraining;
    }

    @Transactional
    public void deleteTraining(Long trainingId) {
        log.info("Attempting to delete training with ID: {}", trainingId);

        Training training = trainingRepository.findById(trainingId)
                .orElseThrow(() -> new IllegalArgumentException("Training not found with ID: " + trainingId));

        Trainer trainer = training.getTrainer();

        
        WorkloadRequest workloadRequest = new WorkloadRequest(
                trainer.getUsername(),
                trainer.getFirstName(),
                trainer.getLastName(),
                trainer.isActive(),
                training.getTrainingDate(),
                training.getTrainingDuration().intValue(),
                "DELETE"
        );
        sendWorkloadWithCircuitBreaker(workloadRequest);

        trainingRepository.delete(training);
        log.info("Training with ID: {} successfully deleted", trainingId);
    }

    
    private void sendWorkloadWithCircuitBreaker(WorkloadRequest request) {
        
        String transactionId = MDC.get("transactionId");

        circuitBreakerFactory.create("workloadServiceCircuitBreaker").run(
                () -> {
                    
                    if (transactionId != null) {
                        MDC.put("transactionId", transactionId);
                    }

                    try {
                        log.info("Sending workload update to microservice for trainer: {}", request.getUsername());
                        workloadServiceClient.updateWorkload(request);
                        return null;
                    } finally {
                        
                        MDC.remove("transactionId");
                    }
                },
                throwable -> {
                    
                    if (transactionId != null) {
                        MDC.put("transactionId", transactionId);
                    }
                    try {
                        log.error("Circuit Breaker triggered! Unable to send workload update for trainer {}. Reason: {}",
                                request.getUsername(), throwable.getMessage());
                        return null;
                    } finally {
                        MDC.remove("transactionId");
                    }
                }
        );
    }

    @Transactional(readOnly = true)
    public List<Training> getTraineeTrainings(String traineeUsername, Date fromDate, Date toDate, String trainerUsername, String trainingTypeName) {
        log.debug("Fetching trainings for trainee: {} with filters", traineeUsername);

        Date safeFrom = fromDate != null ? fromDate : java.sql.Date.valueOf("1900-01-01");
        Date safeTo = toDate != null ? toDate : java.sql.Date.valueOf("3000-01-01");

        String safeTrainer = (trainerUsername != null) ? trainerUsername.trim() : "";
        String safeType = (trainingTypeName != null) ? trainingTypeName.trim() : "";
        String cleanTrainee = traineeUsername.trim();

        return trainingRepository.getTraineeTrainingsByCriteria(cleanTrainee, safeFrom, safeTo, safeTrainer, safeType);
    }

    @Transactional(readOnly = true)
    public List<Training> getTrainerTrainings(String trainerUsername, Date fromDate, Date toDate, String traineeUsername) {
        log.debug("Fetching trainings for trainer: {} with criteria", trainerUsername);

        Date safeFromDate = fromDate != null ? fromDate : java.sql.Date.valueOf("1900-01-01");
        Date safeToDate = toDate != null ? toDate : java.sql.Date.valueOf("3000-01-01");

        String safeTraineeUsername = (traineeUsername != null) ? traineeUsername.trim() : "";
        String cleanTrainerUser = trainerUsername.trim();

        return trainingRepository.getTrainerTrainingsByCriteria(cleanTrainerUser, safeFromDate, safeToDate, safeTraineeUsername);
    }

    @Transactional(readOnly = true)
    public List<Training> findAll() {
        return trainingRepository.findAll();
    }
}