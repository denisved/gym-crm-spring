package org.gymcrm;

import lombok.extern.slf4j.Slf4j;
import org.gymcrm.config.AppConfig;
import org.gymcrm.facade.GymFacade;
import org.gymcrm.model.Trainee;
import org.gymcrm.model.Trainer;
import org.gymcrm.model.TrainingType;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

@Slf4j
public class GymApplication {
    public static void main(String[] args) {
        log.info("Starting Gym CRM Application...");
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        context.registerShutdownHook();

        GymFacade facade = context.getBean(GymFacade.class);
        Scanner scanner = new Scanner(System.in);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        boolean running = true;

        System.out.println("=========================================");
        System.out.println("        Вітаємо у Gym CRM System         ");
        System.out.println("=========================================");

        while (running) {
            System.out.println("\n--- ГОЛОВНЕ МЕНЮ ---");
            System.out.println("1. Створити Тренера");
            System.out.println("2. Створити Учня");
            System.out.println("3. Оновити спеціалізацію Тренера");
            System.out.println("4. Видалити Учня по ID");
            System.out.println("5. Створити Тренування");
            System.out.println("6. Переглянути всіх Тренерів");
            System.out.println("7. Переглянути всіх Учнів");
            System.out.println("8. Переглянути всі Тренування");
            System.out.println("0. Вийти (Зберегти дані)");
            System.out.print("\nОберіть дію: ");

            String choice = scanner.nextLine();

            try {
                switch (choice) {
                    case "1":
                        System.out.print("Введіть ім'я тренера: ");
                        String trainerFirstName = scanner.nextLine();
                        System.out.print("Введіть прізвище тренера: ");
                        String trainerLastName = scanner.nextLine();
                        System.out.print("Введіть спеціалізацію (наприклад FITNESS, YOGA): ");
                        String specialization = scanner.nextLine();

                        Trainer newTrainer = facade.createTrainer(trainerFirstName, trainerLastName, specialization);
                        System.out.println("-> Тренера успішно створено! Логін: " + newTrainer.getUsername() + ", Пароль: " + newTrainer.getPassword());
                        break;

                    case "2":
                        System.out.print("Введіть ім'я учня: ");
                        String traineeFirstName = scanner.nextLine();
                        System.out.print("Введіть прізвище учня: ");
                        String traineeLastName = scanner.nextLine();
                        System.out.print("Введіть дату народження (РРРР-ММ-ДД, або залиште пустим): ");
                        String dobInput = scanner.nextLine();
                        Date dateOfBirth = dobInput.isEmpty() ? null : dateFormat.parse(dobInput);
                        System.out.print("Введіть адресу (або залиште пустою): ");
                        String address = scanner.nextLine();

                        Trainee newTrainee = facade.createTrainee(traineeFirstName, traineeLastName, dateOfBirth, address);
                        System.out.println("-> Учня успішно створено! Логін: " + newTrainee.getUsername() + ", Пароль: " + newTrainee.getPassword());
                        break;

                    case "3":
                        System.out.print("Введіть ID Тренера для оновлення: ");
                        Long trainerId = Long.parseLong(scanner.nextLine());
                        Trainer trainerToUpdate = facade.getTrainer(trainerId);

                        if (trainerToUpdate != null) {
                            System.out.println("Поточна спеціалізація: " + trainerToUpdate.getSpecialization());
                            System.out.print("Введіть нову спеціалізацію: ");
                            String newSpec = scanner.nextLine();
                            trainerToUpdate.setSpecialization(newSpec);
                            facade.updateTrainer(trainerToUpdate);
                            System.out.println("-> Спеціалізацію тренера успішно оновлено!");
                        } else {
                            System.out.println("-> Помилка: Тренера з таким ID не знайдено.");
                        }
                        break;

                    case "4":
                        System.out.print("Введіть ID Учня для видалення: ");
                        Long traineeId = Long.parseLong(scanner.nextLine());
                        facade.deleteTrainee(traineeId);
                        System.out.println("-> Команда видалення виконана (перевірте логи для підтвердження).");
                        break;

                    case "5":
                        System.out.print("Введіть ID Учня (Trainee ID): ");
                        Long idForTrainee = Long.parseLong(scanner.nextLine());
                        System.out.print("Введіть ID Тренера (Trainer ID): ");
                        Long idForTrainer = Long.parseLong(scanner.nextLine());
                        System.out.print("Введіть назву тренування: ");
                        String trainingName = scanner.nextLine();
                        System.out.print("Введіть тип тренування (FITNESS, YOGA, ZUMBA, STRETCHING, CROSSFIT): ");
                        TrainingType type = TrainingType.valueOf(scanner.nextLine().toUpperCase());
                        System.out.print("Введіть дату тренування (РРРР-ММ-ДД): ");
                        Date trainingDate = dateFormat.parse(scanner.nextLine());
                        System.out.print("Введіть тривалість (у хвилинах): ");
                        Integer duration = Integer.parseInt(scanner.nextLine());

                        facade.createTraining(idForTrainee, idForTrainer, trainingName, type, trainingDate, duration);
                        System.out.println("-> Тренування успішно створено!");
                        break;

                    case "6":
                        System.out.println("\n=== СПИСОК ВСІХ ТРЕНЕРІВ ===");
                        facade.getAllTrainers().forEach(System.out::println);
                        if (facade.getAllTrainers().isEmpty()) System.out.println("Список порожній.");
                        break;

                    case "7":
                        System.out.println("\n=== СПИСОК ВСІХ УЧНІВ ===");
                        facade.getAllTrainees().forEach(System.out::println);
                        if (facade.getAllTrainees().isEmpty()) System.out.println("Список порожній.");
                        break;

                    case "8":
                        System.out.println("\n=== СПИСОК ВСІХ ТРЕНУВАНЬ ===");
                        facade.getAllTrainings().forEach(System.out::println);
                        if (facade.getAllTrainings().isEmpty()) System.out.println("Список порожній.");
                        break;

                    case "0":
                        running = false;
                        System.out.println("-> Завершення роботи програми. Збереження даних у файл...");
                        break;

                    default:
                        System.out.println("-> Невідома команда. Оберіть цифру від 0 до 5.");
                }
            } catch (NumberFormatException e) {
                System.out.println("-> Помилка: Очікувалось число, а ви ввели текст.");
            } catch (ParseException e) {
                System.out.println("-> Помилка: Неправильний формат дати. Використовуйте РРРР-ММ-ДД (наприклад 2000-12-31).");
            } catch (IllegalArgumentException e) {
                System.out.println("-> Помилка: Неправильне значення (можливо ви помилилися у типі тренування).");
            } catch (Exception e) {
                System.out.println("-> Виникла непередбачувана помилка: " + e.getMessage());
            }
        }

        context.close();
    }
}