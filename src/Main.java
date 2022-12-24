import task.*;
import task.SingleTask;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collection;
import java.util.Date;
import java.util.Scanner;

public class Main {
    private static final Service SCHEDULE = new Service();
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    public static void main(String[] args) {
        SCHEDULE.addTask(new SingleTask("SingleTest","test", LocalDateTime.now(),TaskType.PERSONAL));
        SCHEDULE.addTask(new DailyTask("DailyTest","test", LocalDateTime.now(),TaskType.WORK));
        SCHEDULE.addTask(new WeeklyTask("WeeklyTest","test", LocalDateTime.now(),TaskType.PERSONAL));
        SCHEDULE.addTask(new MonthlyTask("MonthlyTest","test", LocalDateTime.now(),TaskType.WORK));
        SCHEDULE.addTask(new YearlyTask("YearlyTest","test", LocalDateTime.now(),TaskType.PERSONAL));
        try (Scanner scanner = new Scanner(System.in)) {
            label:
            while (true) {
                printMenu();
                System.out.print("Выберите пункт меню: ");
                if (scanner.hasNextInt()) {
                    int menu = scanner.nextInt();
                    scanner.reset();
                    switch (menu) {
                        case 1:
                            addTask(scanner);
                            break;
                        case 2:
                            removeTasks(scanner);
                            break;
                        case 3:
                            printTaskForDate(scanner);
                            break;
                        case 4:
                            printAllTasks(scanner);
                            break;
                        case 0:
                            break label;
                    }
                } else {
                    scanner.next();
                    System.out.println("Выберите пункт меню из списка!");
                }
            }
        }
        printMenu();
    }
    private static void printMenu() {
        System.out.println(
                """
                        1. Добавить задачу
                        2. Удалить задачу
                        3. Получить задачу на указанный день
                        4. Получить список всех задач
                        0. Выход
                        """
        );

    }

    private static void addTask(Scanner scanner) {
        String title = readString("Введите название задачи: ",scanner);
        String description = readString("Введите описание задачи: ",scanner);
        LocalDateTime taskDate = readDateTime(scanner);
        TaskType taskType = readType(scanner);
        Repeatability repeatability = readRepeatability(scanner);
        Task task = switch (repeatability) {
            case SINGLE -> new SingleTask(title,description,taskDate, taskType);
            case DAILY -> new DailyTask(title,description,taskDate, taskType);
            case WEEKLY -> new WeeklyTask(title,description,taskDate, taskType);
            case MONTHLY -> new MonthlyTask(title,description,taskDate, taskType);
            case YEARLY -> new YearlyTask(title,description,taskDate, taskType);
        };
        SCHEDULE.addTask(task);
    }

    private static TaskType readType(Scanner scanner) {
        while (true) {
            try {
                System.out.println("Необходимо выбрать тип задачи:");
                for (TaskType taskType : TaskType.values()) {
                    System.out.println(taskType.ordinal() +". " + defineType(taskType));
                }
                System.out.print("Введите тип: ");
                String ordinalLine = scanner.nextLine();
                int ordinal = Integer.parseInt(ordinalLine);
                return TaskType.values()[ordinal];
            } catch (NumberFormatException e) {
                System.out.println("Введен неверный номер типа задачи");
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println("Тип задачи не найден");
            }
        }
    }

    private static Repeatability readRepeatability(Scanner scanner) {
        while (true) {
            try {
                System.out.println("Необходимо выбрать тип повторяемости задачи:");
                for (Repeatability repeatability : Repeatability.values()) {
                    System.out.println(repeatability.ordinal() +". " + defineRepeatability(repeatability));
            }
            System.out.print("Введите тип повторяемости : ");
                String ordinalLine = scanner.nextLine();
                int ordinal = Integer.parseInt(ordinalLine);
                return Repeatability.values()[ordinal];
            } catch (NumberFormatException e) {
                System.out.println("Введен неверный номер типа задачи");
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println("Тип задачи не найден");
            }
        }
    }

    private static LocalDateTime readDateTime(Scanner scanner) {
        LocalDate localDate = readDate(scanner);
        LocalTime localTime = readTime(scanner);
        return localDate.atTime(localTime);

    }

    private static String readString(String message, Scanner scanner) {
        while (true) {
            System.out.print(message);
            String readString = scanner.nextLine();
            if (readString == null || readString.isBlank()) {
                System.out.println("Введено пустое значение");
            } else {
                return readString;
            }
        }
    }

    private static void removeTasks(Scanner scanner) {
        System.out.println("Все задачи");
        for (Task task:SCHEDULE.getAllTask()){
            System.out.printf("%d. %s [%s] (%s)%n",
                    task.getId(),
                    task.getTitle(),
                    defineType(task.getTaskType()),
                    defineRepeatability(task.getRepeatabilityType()));
        }
        while (true) {
            try {
                System.out.print("Выберите задачу для удаления:");
                String idLine = scanner.nextLine();
                int id = Integer.parseInt(idLine);
                SCHEDULE.removeTask(id);
                break;
            } catch (NumberFormatException e) {
                System.out.println("Введен неверный id задачи");
            } catch (TaskNotFoundException e) {
                System.out.println("Задача для удаления не найдена");
            }
        }
    }

    private static void printTaskForDate(Scanner scanner) {
        LocalDate localDate = readDate(scanner);
        Collection<Task> taskForDate = SCHEDULE.getTasksForDate(localDate);
        System.out.println("Задачи на " + localDate.format(DATE_FORMAT) + ":");
        for (Task task : taskForDate) {
            System.out.printf("[%s]%s: %s (%s)%n",
                    defineType(task.getTaskType()),
                    task.getTitle(),
                    task.getTaskDateTime().format(TIME_FORMAT),
                    task.getDescription());
        }
    }

    private static LocalDate readDate(Scanner scanner) {
        while (true) {
            try {
                System.out.print("Необходимо ввести дату задачи (dd.MM.yyyy): ");
                String dateLine = scanner.nextLine();
                return LocalDate.parse(dateLine, DATE_FORMAT);
            } catch (DateTimeParseException e) {
                System.out.println("Необходимо ввести дату в корректном формате");
            }
        }
    }
    private static void printAllTasks (Scanner scanner) {
        System.out.println("Все задачи");
        for (Task task : SCHEDULE.getAllTask()) {
            System.out.printf("%d. %s [%s] (%s)%n",
                    task.getId(),
                    task.getTitle(),
                    defineType(task.getTaskType()),
                    defineRepeatability(task.getRepeatabilityType()));
        }
    }
    private static LocalTime readTime(Scanner scanner) {
        while (true) {
            try {
                System.out.print("Необходимо ввести время задачи (hh:mm): ");
                String dateLine = scanner.nextLine();
                return LocalTime.parse(dateLine, TIME_FORMAT);
            } catch (DateTimeParseException e) {
                System.out.println("Необходимо ввести дату в корректном формате");
            }
        }
    }

    private static String defineType(TaskType taskType) {
        return switch (taskType) {
            case WORK -> "Рабочая задача";
            case PERSONAL -> "Персональная задача";
//            case WORK:
//                System.out.print("Рабочая задача");
//                break;
//            case PERSONAL:
//                System.out.print("Персональная задача");
//                break;
//            default:
//                System.out.print("Введен неправильный тип задачи");
//                break;
        };
    }
    private static String defineRepeatability(Repeatability repeatability) {
        return switch (repeatability) {

            case SINGLE -> "Разовая";
            case DAILY -> "Ежедневная";
            case WEEKLY -> "Еженедельная";
            case MONTHLY -> "Ежемесячная";
            case YEARLY -> "Ежегодная";
        };
    }
}
