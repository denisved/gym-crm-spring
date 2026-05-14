package org.gymcrm;

import lombok.extern.slf4j.Slf4j;
import org.gymcrm.config.AppConfig;
import org.gymcrm.facade.GymFacade;
import org.gymcrm.model.Trainee;
import org.gymcrm.model.Trainer;
import org.gymcrm.model.Training;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

@Slf4j
public class GymApplication {
    private static final Scanner scanner = new Scanner(System.in);
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public static void main(String[] args) {
        log.info("Starting Gym CRM Application...");

        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        context.registerShutdownHook();

        GymFacade facade = context.getBean(GymFacade.class);
        boolean running = true;

        System.out.println("=========================================");
        System.out.println(" Gym CRM System ");
        System.out.println("=========================================");

        while (running) {
            printMainMenu();
            String choice = scanner.nextLine();

            try {
                switch (choice) {
                    case "1": createTrainer(facade); break;
                    case "2": createTrainee(facade); break;
                    case "3": handleTraineeSection(facade); break;
                    case "4": handleTrainerSection(facade); break;
                    case "5": addTraining(facade); break;

                    case "6":
                        System.out.println("\n=== СПИСОК ВСІХ ТРЕНЕРІВ ===");
                        List<Trainer> trainers = facade.getAllTrainers();
                        if (trainers.isEmpty()) System.out.println("Список порожній.");
                        else trainers.forEach(System.out::println);
                        break;

                    case "7":
                        System.out.println("\n=== СПИСОК ВСІХ УЧНІВ ===");
                        List<Trainee> trainees = facade.getAllTrainees();
                        if (trainees.isEmpty()) System.out.println("Список порожній.");
                        else trainees.forEach(System.out::println);
                        break;

                    case "8":
                        System.out.println("\n=== СПИСОК ВСІХ ТРЕНУВАНЬ ===");
                        List<Training> allTrainings = facade.getAllTrainings();
                        if (allTrainings.isEmpty()) System.out.println("Тренувань ще не заплановано.");
                        else allTrainings.forEach(System.out::println);
                        break;

                    case "0":
                        running = false;
                        System.out.println("-> Завершення роботи. Дані збережені в БД.");
                        break;
                    default:
                        System.out.println("-> Невідома команда.");
                }
            } catch (Exception e) {
                System.out.println("-> [ПОМИЛКА ГОЛОВНОГО МЕНЮ]: " + e.getMessage());
            }
        }
        context.close();
    }

    private static void printMainMenu() {
        System.out.println("\n--- ГОЛОВНЕ МЕНЮ ---");
        System.out.println("1. Реєстрація: Створити профіль Тренера");
        System.out.println("2. Реєстрація: Створити профіль Учня");
        System.out.println("3. Увійти в Кабінет Учня");
        System.out.println("4. Увійти в Кабінет Тренера");
        System.out.println("5. Адмін: Додати нове Тренування");
        System.out.println("6. Адмін: Подивитись всіх тренерів");
        System.out.println("7. Адмін: Подивитись всіх учнів");
        System.out.println("8. Адмін: Подивитись всі тренування");
        System.out.println("0. Вийти з програми");
        System.out.print("\nОберіть дію: ");
    }
    private static void createTrainer(GymFacade facade) {
        System.out.print("Ім'я: "); String fn = scanner.nextLine();
        System.out.print("Прізвище: "); String ln = scanner.nextLine();
        System.out.print("Спеціалізація (YOGA, FITNESS, ZUMBA, STRETCHING, CROSSFIT): ");
        String spec = scanner.nextLine().trim().toUpperCase();
        Trainer t = facade.createTrainer(fn, ln, spec);
        System.out.println("-> Створено! Username: " + t.getUsername() + " | Password: " + t.getPassword());
    }

    private static void createTrainee(GymFacade facade) throws ParseException {
        System.out.print("Ім'я: "); String fn = scanner.nextLine();
        System.out.print("Прізвище: "); String ln = scanner.nextLine();
        System.out.print("Дата народження (yyyy-MM-dd або пусто): "); String dobStr = scanner.nextLine();
        Date dob = dobStr.isEmpty() ? null : dateFormat.parse(dobStr);
        System.out.print("Адреса (або пусто): "); String addr = scanner.nextLine();
        Trainee t = facade.createTrainee(fn, ln, dob, addr);
        System.out.println("-> Створено! Username: " + t.getUsername() + " | Password: " + t.getPassword());
    }

    private static void handleTraineeSection(GymFacade facade) {
        System.out.print("Введіть Username: "); String user = scanner.nextLine();
        System.out.print("Введіть Password: "); String pass = scanner.nextLine();

        if (!facade.authenticateTrainee(user, pass)) {
            System.out.println("-> Відмовлено в доступі: невірні дані.");
            return;
        }

        boolean inCabinet = true;
        while (inCabinet) {
            System.out.println("\n=== КАБІНЕТ УЧНЯ [" + user + "] ===");
            System.out.println("1. Переглянути профіль | 2. Оновити профіль | 3. Змінити пароль | 4. Змінити статус (Актив/Деактив)");
            System.out.println("5. Видалити профіль    | 6. Список тренувань| 7. Вільні тренери | 8. Призначити тренерів");
            System.out.println("0. Вийти з кабінету (Logout)");
            System.out.print("Оберіть дію: ");
            String act = scanner.nextLine();

            try {
                switch (act) {
                    case "1":
                        System.out.println(facade.getTrainee(user));
                        break;
                    case "2":
                        Trainee trainee = facade.getTrainee(user);
                        System.out.println("Вводьте нові дані або натисніть Enter, щоб залишити без змін:");

                        String currentDob = trainee.getDateOfBirth() != null ? dateFormat.format(trainee.getDateOfBirth()) : "не вказана";
                        System.out.print("Нова дата народження yyyy-MM-dd (" + currentDob + "): ");
                        String dobStr = scanner.nextLine();
                        if (!dobStr.isEmpty()) trainee.setDateOfBirth(dateFormat.parse(dobStr));

                        System.out.print("Нова адреса (" + trainee.getAddress() + "): ");
                        String addr = scanner.nextLine();
                        if (!addr.isEmpty()) trainee.setAddress(addr);

                        facade.updateTrainee(trainee);
                        System.out.println("-> Профіль (Дата народження та Адреса) успішно оновлено!");
                        break;
                    case "3":
                        System.out.print("Новий пароль: ");
                        String newPass = scanner.nextLine();
                        facade.changeTraineePassword(user, pass, newPass);
                        System.out.println("-> Пароль успішно змінено! Вас автоматично виведено з системи для безпеки.");
                        inCabinet = false;
                        break;
                    case "4":
                        System.out.print("Активувати профіль? (true/false): ");
                        facade.toggleTraineeActivation(user, Boolean.parseBoolean(scanner.nextLine()));
                        System.out.println("-> Статус оновлено!");
                        break;
                    case "5":
                        facade.deleteTrainee(user);
                        System.out.println("-> Профіль видалено. Всі пов'язані тренування каскадно видалені з БД.");
                        inCabinet = false;
                        break;
                    case "6":
                        System.out.println("--- ФІЛЬТР ТРЕНУВАНЬ ---");
                        System.out.print("З дати (yyyy-MM-dd, або Enter щоб пропустити): ");
                        String from = scanner.nextLine();
                        System.out.print("До дати (yyyy-MM-dd, або Enter щоб пропустити): ");
                        String to = scanner.nextLine();
                        System.out.print("Юзернейм тренера (або Enter щоб пропустити): ");
                        String trainerName = scanner.nextLine();
                        System.out.print("Тип тренування (або Enter щоб пропустити): ");
                        String typeName = scanner.nextLine();

                        List<Training> list = facade.getTraineeTrainings(
                                user,
                                from.isEmpty() ? null : dateFormat.parse(from),
                                to.isEmpty() ? null : dateFormat.parse(to),
                                trainerName.isEmpty() ? null : trainerName,
                                typeName.isEmpty() ? null : typeName
                        );

                        if (list.isEmpty()) {
                            System.out.println("-> Тренувань за вказаними критеріями не знайдено.");
                        } else {
                            System.out.println("\n=== ВАШІ ТРЕНУВАННЯ ===");
                            list.forEach(t -> {
                                System.out.printf("Назва: %s | Тренер: %s | Дата: %s | Тривалість: %d хв. | Тип: %s%n",
                                        t.getTrainingName(),
                                        t.getTrainer().getUsername(),
                                        dateFormat.format(t.getTrainingDate()),
                                        t.getTrainingDuration().intValue(),
                                        t.getTrainingType().getTrainingTypeName()
                                );
                            });
                        }
                        break;
                    case "7":
                        List<Trainer> freeTrainers = facade.getUnassignedTrainers(user);
                        if (freeTrainers.isEmpty()) System.out.println("-> Немає вільних тренерів.");
                        else freeTrainers.forEach(System.out::println);
                        break;
                    case "8":
                        System.out.println("--- ОНОВЛЕННЯ СПИСКУ ТРЕНЕРІВ ---");
                        System.out.print("Введіть юзернейми тренерів через кому (наприклад: Ivan.Ivanov, Petro.Petrov): ");
                        String rawInput = scanner.nextLine().trim();

                        List<String> trainerUsernames;
                        if (rawInput.isEmpty()) {
                            trainerUsernames = java.util.Collections.emptyList();
                        } else {
                            trainerUsernames = Arrays.stream(rawInput.split(","))
                                    .map(String::trim)
                                    .filter(s -> !s.isEmpty())
                                    .toList();
                        }

                        facade.updateTraineeTrainersList(user, trainerUsernames);
                        System.out.println("-> Список тренерів успішно оновлено!");
                        break;
                    case "0":
                        inCabinet = false;
                        System.out.println("-> Вихід з кабінету учня...");
                        break;
                    default:
                        System.out.println("-> Невідома дія. Спробуйте ще раз.");
                }
            } catch (Exception e) {
                System.out.println("-> [ПОМИЛКА У КАБІНЕТІ]: Некоректне введення або збій: " + e.getMessage());
            }
        }
    }

    private static void handleTrainerSection(GymFacade facade) {
        System.out.print("Введіть Username: "); String user = scanner.nextLine();
        System.out.print("Введіть Password: "); String pass = scanner.nextLine();

        if (!facade.authenticateTrainer(user, pass)) {
            System.out.println("-> Відмовлено в доступі.");
            return;
        }

        boolean inCabinet = true;
        while (inCabinet) {
            System.out.println("\n=== КАБІНЕТ ТРЕНЕРА [" + user + "] ===");
            System.out.println("1. Переглянути профіль | 2. Оновити профіль | 3. Змінити пароль | 4. Змінити статус (Актив/Деактив) | 5. Мої тренування");
            System.out.println("0. Вийти з кабінету (Logout)");
            System.out.print("Оберіть дію: ");
            String act = scanner.nextLine();

            try {
                switch (act) {
                    case "1":
                        System.out.println(facade.getTrainer(user));
                        break;
                    case "2":
                        Trainer trainer = facade.getTrainer(user);
                        System.out.println("Поточна спеціалізація: " + trainer.getSpecialization().getTrainingTypeName());
                        System.out.print("Нова спеціалізація (YOGA, FITNESS, ZUMBA, STRETCHING, CROSSFIT) або Enter, щоб залишити: ");
                        String spec = scanner.nextLine().trim().toUpperCase();

                        if (!spec.isEmpty()) {
                            facade.updateTrainerSpecialization(user, spec);
                            System.out.println("-> Спеціалізацію успішно оновлено!");
                        } else {
                            System.out.println("-> Оновлення скасовано (змін не внесено).");
                        }
                        break;
                    case "3":
                        System.out.print("Новий пароль: ");
                        String newPass = scanner.nextLine();
                        facade.changeTrainerPassword(user, pass, newPass);
                        System.out.println("-> Пароль успішно змінено! Вас автоматично виведено з системи для безпеки.");
                        inCabinet = false;
                        break;
                    case "4":
                        System.out.print("Активувати профіль? (true/false): ");
                        facade.toggleTrainerActivation(user, Boolean.parseBoolean(scanner.nextLine()));
                        System.out.println("-> Статус оновлено!");
                        break;
                    case "5":
                        System.out.println("--- ФІЛЬТР ТРЕНУВАНЬ ---");
                        System.out.print("З дати (yyyy-MM-dd, або Enter щоб пропустити): ");
                        String from = scanner.nextLine();
                        System.out.print("До дати (yyyy-MM-dd, або Enter щоб пропустити): ");
                        String to = scanner.nextLine();
                        System.out.print("Юзернейм учня (або Enter щоб пропустити): ");
                        String traineeName = scanner.nextLine();

                        List<Training> list = facade.getTrainerTrainings(
                                user,
                                from.isEmpty() ? null : dateFormat.parse(from),
                                to.isEmpty() ? null : dateFormat.parse(to),
                                traineeName.isEmpty() ? null : traineeName
                        );

                        if (list.isEmpty()) {
                            System.out.println("-> Тренувань не знайдено.");
                        } else {
                            System.out.println("\n=== ВАШІ ТРЕНУВАННЯ ===");
                            list.forEach(t -> {
                                System.out.printf("Назва: %s | Учень: %s | Дата: %s | Тривалість: %d хв.%n",
                                        t.getTrainingName(),
                                        t.getTrainee().getUsername(),
                                        dateFormat.format(t.getTrainingDate()),
                                        t.getTrainingDuration().intValue()
                                );
                            });
                        }
                        break;
                    case "0":
                        inCabinet = false;
                        System.out.println("-> Вихід з кабінету тренера...");
                        break;
                    default:
                        System.out.println("-> Невідома дія. Спробуйте ще раз.");
                }
            } catch (Exception e) {
                System.out.println("-> [ПОМИЛКА У КАБІНЕТІ]: Некоректне введення або збій: " + e.getMessage());
            }
        }
    }

    private static void addTraining(GymFacade facade) throws ParseException {
        System.out.print("Username Учня: "); String trainee = scanner.nextLine();
        System.out.print("Username Тренера: "); String trainer = scanner.nextLine();
        System.out.print("Назва тренування: "); String name = scanner.nextLine();
        System.out.print("Дата (yyyy-MM-dd): "); Date d = dateFormat.parse(scanner.nextLine());
        System.out.print("Тривалість (хв): "); Integer dur = Integer.parseInt(scanner.nextLine());

        facade.createTraining(trainee, trainer, name, d, dur);
        System.out.println("-> Тренування додано!");
    }
}