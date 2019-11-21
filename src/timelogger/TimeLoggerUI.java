package timelogger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import timelogger.exceptions.EmptyTimeFieldException;
import timelogger.exceptions.InvalidTaskIdException;
import timelogger.exceptions.NegativeMinutesOfWorkException;
import timelogger.exceptions.NoTaskIdException;
import timelogger.exceptions.NotExpectedTimeOrderException;
import timelogger.exceptions.NotNewDateException;
import timelogger.exceptions.NotNewMonthException;
import timelogger.exceptions.NotSeparatedTimesException;
import timelogger.exceptions.NotTheSameMonthException;
import timelogger.exceptions.WeekendNotEnabledException;

/**
 * 
 * @author Dubi
 */
public class TimeLoggerUI {

    private final String instructionFile
            = (new File("").getAbsolutePath() + "\\instructions.txt");
    private final TimeLogger timeLogger = new TimeLogger();
    private final int listingToIndexOffset = -1;

    /**
     * Fetches the instruction file,prints its content to the console,
     * then waits for user input.
     */
    public void startUI() {
        getInstructions();
        while (true) {
            try {
                processUserInput(getStringInputFromUser("Please select a command(0-10)"));
            } catch (NotExpectedTimeOrderException | EmptyTimeFieldException | InvalidTaskIdException | NoTaskIdException | WeekendNotEnabledException | NotNewMonthException | NotNewDateException | NotTheSameMonthException | NegativeMinutesOfWorkException | NotSeparatedTimesException ex) {
                System.out.println(ex.getMessage());
            } 
        }
    }

    /**
     * Tries to read and write the given file to console.
     */
    private void getInstructions() {
        try (Stream<String> stream = Files.lines(Paths.get(instructionFile))) {
            stream.forEach(System.out::println);

        } catch (IOException e) {
            System.out.println(e);
        } catch (Exception e) {
            System.out.println("Unexpected exception:" + e);
        }
    }

    private void processUserInput(String inputFromUser) throws NotExpectedTimeOrderException, EmptyTimeFieldException, InvalidTaskIdException, NoTaskIdException, NegativeMinutesOfWorkException, NotSeparatedTimesException, WeekendNotEnabledException, NotNewDateException, NotTheSameMonthException, NotNewMonthException {

        switch (inputFromUser) {
            case "0":
                System.exit(0);
                break;
            case "1":
                listMonths();
                break;
            case "2":
                listDays();
                break;
            case "3":
                listTasks();
                break;
            case "4":
                addMonth();
                break;
            case "5":
                addDayToMonth();
                break;
            case "6":
                startTask();
                break;
            case "7":
                finishTask();
                break;
            case "8":
                deleteTask();
                break;
            case "9":
                modifyTask();
                break;
            case "10":
                getStatistics();
                break;
            default:
                getInstructions();      // FIXME: refactor to get and show
                System.out.println("Invalid input.\nExpected 0 to 10.");
                break;
        }
    }

    /**
     * Lists all the stored months.
     */
    private void listMonths() {
        System.out.println("Months:");
        try {
            timeLogger.getMonths().stream()
                    .forEach(month -> {
                        System.out.println(
                                timeLogger.getMonths().indexOf(month) + 1 + ". "
                                + month.getDate()
                                .format(DateTimeFormatter.ofPattern("yyyy-MM"))
                        );
                    });
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Lists the days of a given month.
     */
    private void listDays() {
        try {
            listMonths();
            int month = validateListSelection(getMonthsSize(), "The month");
            listDaysInMonth(month);
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Lists the tasks of a given date.
     */
    private void listTasks() {
        try {
            listMonths();
            int month = validateListSelection(getMonthsSize(), "The month");
            listDaysInMonth(month);
            int day = validateListSelection(getDaysSize(month), "The day");
            listTasksInDay(month, day);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    // TODO: vars for date range
    /**
     * Creates a new month in a given year.
     * @throws NotNewMonthException 
     */
    private void addMonth()  throws NotNewMonthException{
        int year = getIntegerInputFromUser("The year (ex.2019)");
        year = validatedIntegerValue(year, LocalDate.now().getYear() - 20, 
                LocalDate.now().getYear() + 1, "Enter a valid year");

        int month = getIntegerInputFromUser("The month (1-12)");
        month = validatedIntegerValue(month, 1, 12,"Enter a valid month(1-12)");
        timeLogger.addMonth(new WorkMonth(year, month)); 
    }

    /**
     * Creates a new day within given month.
     * @throws NegativeMinutesOfWorkException
     * @throws WeekendNotEnabledException
     * @throws NotNewDateException
     * @throws NotTheSameMonthException 
     */
    private void addDayToMonth() throws NegativeMinutesOfWorkException, WeekendNotEnabledException, NotNewDateException, NotTheSameMonthException {
        listMonths();
        int month = validateListSelection(getMonthsSize(), "The month");
        int monthLength = getMonthByIndex(month).getDate().getMonth().maxLength();
        int day = getIntegerInputFromUser("New day");
        day = validatedIntegerValue(day, 1, monthLength, "Enter a valid day");

        Long requiredMinPerDay; // TODO: reft. new method
        while (true) {
            try {
                String requiredWorkingHours = getStringInputFromUser("Enter the required hours(Optional, def.7.5)");
                if (requiredWorkingHours.matches("\\d{1,2}\\.?\\d{0,2}")) {
                    double tmpDouble = Double.parseDouble(requiredWorkingHours);
                    if (tmpDouble > 24) {
                        System.out.println("Enter valid hours(0-24)!");
                        continue;
                    }
                    requiredMinPerDay = (long) tmpDouble * 60;
                    break;
                } else if ("".equals(requiredWorkingHours)) {
                    requiredMinPerDay = 450L;
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Unexpected format.");
            }
        }
        getMonthByIndex(month).addWorkDay(new WorkDay((long) requiredMinPerDay,
                getMonthByIndex(month).getDate().getYear(),
                getMonthByIndex(month).getDate().getMonthValue(),
                day), true);
    }

    /**
     * Sets the startTime of given task.
     * @throws NotExpectedTimeOrderException
     * @throws EmptyTimeFieldException
     * @throws InvalidTaskIdException
     * @throws NoTaskIdException
     * @throws NotSeparatedTimesException 
     */
    private void startTask() throws NotExpectedTimeOrderException, EmptyTimeFieldException, InvalidTaskIdException, NoTaskIdException, NotSeparatedTimesException {
        listMonths();
        int month = validateListSelection(getMonthsSize(), "The month");
        listDaysInMonth(month);
        int day = validateListSelection(getDaysSize(month), "The day");

        String taskId = getStringInputFromUser("The task id");
        Task newTask = new Task(taskId);
        while (!newTask.isValidTaskId(taskId)) {
            taskId = getStringInputFromUser("Enter a valid task id(1234/LT-1234)");
        }
        newTask.setTaskId(taskId);

        String comment = getStringInputFromUser("The commnet");
        newTask.setComment(comment);
        String startTime = validateTimeInput(
                getStringInputFromUser("The start time(ex.10:20)"));

        if (startTime.isBlank()) {      // TODO: set to [endTime] branch
            startTime = getDayWithGivenMonthAndDay(month, day)
                    .getLatestTaskEndTime()
                    .format(DateTimeFormatter.ofPattern("H:m"));
            getDayWithGivenMonthAndDay(month, day).startTask(newTask, startTime);

        } else {
            getDayWithGivenMonthAndDay(month, day)
                    .startTask(newTask, startTime);
        }
    }

    /**
     * Sets the endTime of given task.
     * @throws NotExpectedTimeOrderException
     * @throws EmptyTimeFieldException 
     */
    private void finishTask() throws NotExpectedTimeOrderException, EmptyTimeFieldException {
        try {           
            listMonths();
            int month = validateListSelection(getMonthsSize(), "The month");
            listDaysInMonth(month);
            int day = validateListSelection(getDaysSize(month), "The day");

            List<Task> startedTasks = getDayWithGivenMonthAndDay(month, day)
                    .getTasks().stream()
                    .filter(task -> task.getEndTime() == null)
                    .collect(Collectors.toList());
            if (startedTasks.isEmpty()) {
                System.out.println("The given day has no task you can end.");
            } else {
                startedTasks.forEach(task -> {
                    System.out.println(startedTasks
                            .indexOf(task) + 1 + ". " + task.toString());
                });
                int taskNum = validateListSelection(startedTasks.size(), 
                        "Select a task from the list");
                startedTasks.get(taskNum)
                        .setEndTime(LocalTime
                                .parse(validateTimeInput(
                                        getStringInputFromUser("End time")),
                                        DateTimeFormatter.ofPattern("H:m")));
            }
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        }  
    }

    /**
     * Deletes the given task.
     */
    private void deleteTask() {
        listMonths();
        try {
            int month = validateListSelection(getMonthsSize(), "The month");
            listDaysInMonth(month);
            int day = validateListSelection(getDaysSize(month), "The day");
            listTasksInDay(month, day);
            int taskNum = validateListSelection( 
                    getDayWithGivenMonthAndDay(month, day).getTasks().size(), 
                    "Select the task you want to delete");

            Task task = getDayWithGivenMonthAndDay(month, day)
                    .getTasks().get(taskNum);

            if (getStringInputFromUser("Are you sure? (y/)")
                    .equalsIgnoreCase("y")) {
                getDayWithGivenMonthAndDay(month, day)
                        .deleteTask(task);
                System.out.println("Task was deleted.");
            } else {
                System.out.println("Task was not deleted.");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Modifies the given task's values, if given value is not empty.
     * @throws NotExpectedTimeOrderException
     * @throws InvalidTaskIdException
     * @throws NoTaskIdException
     * @throws EmptyTimeFieldException 
     */
    private void modifyTask() throws NotExpectedTimeOrderException, InvalidTaskIdException, NoTaskIdException, EmptyTimeFieldException {
        listMonths();
        int month = validateListSelection(getMonthsSize(), "The month");
        listDaysInMonth(month);
        int day = validateListSelection(getDaysSize(month), "The day");
        listTasksInDay(month, day);
        Task task = getDayWithGivenMonthAndDay(month, day)
                .getTasks().get(validateListSelection(
                        getDayWithGivenMonthAndDay(month, day).getTasks().size()
                        ,"Select the task you want to modify"));

        System.out.println("To keep the [original value],let the input empty.");
        String taskId = getStringInputFromUser("New taskId"
                + "[" + task.getTaskId() + "]");
        String comment = getStringInputFromUser("New comment"
                + "[" + task.getComment() + "]");
        String startTime = optionalTimeInput(getStringInputFromUser(
                "New start hour" + "[" + task.getStartTime() + "]"));
        String endTime = optionalTimeInput(getStringInputFromUser(
                "New end hour" + "[" + task.getEndTime() + "]"));

        List<String> modifyedTask;
        modifyedTask = List.of(taskId, comment, startTime, endTime);
        getDayWithGivenMonthAndDay(month, day)
                .modifyTask(task, modifyedTask);
    }

    /**
     * Prints the given months statistics to the console.
     * @throws EmptyTimeFieldException 
     */
    private void getStatistics() throws EmptyTimeFieldException{
        try {
        listMonths();
        int month = validateListSelection(getMonthsSize(),
                "Select a month from the list above");
        WorkMonth workMonth = getMonthByIndex(month);
        
            System.out.println("Date: " + workMonth.getDate());
            System.out.println("Required Minutes Per Month:"
                    + workMonth.getRequiredMinPerMonth());
            System.out.println("Extra Min Per Month: "
                    + workMonth.getExtraMinPerMonth());
            System.out.println("Sum of month: "
                    + workMonth.getSumPerMonth());

            workMonth.getDays().stream().forEachOrdered(day -> {
                System.out.println("\nDate of day: " + day.getActualDay());
                System.out.println("Required Minutes Per Day: "
                        + day.getRequiredMinPerday());
                System.out.println("Extra minutes per day: "
                        + day.getExtraMinPerDay());
                System.out.println("Sum of day: " + day.getSumPerDay());
            }); 
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        }
    }

    // OTHER METHODS
    /**
     * Asks for user input with given message.
     * @param message
     * @return String input
     */
    private String getStringInputFromUser(String message) {
        Scanner scanner = new Scanner(System.in);
        System.out.print(message + ": ");
        return scanner.nextLine();
    }

    /**
     * Prompts the user with given message and keep asking for input until it is
     * a valid integer value.
     * @param message
     * @return int number
     */
    private int getIntegerInputFromUser(String message) {
        int number;
        while (true) {
            try {
                number = Integer.parseInt(getStringInputFromUser(message));
                break;
            } catch (NumberFormatException e) {
                System.out.println("Only integer numbers are allowed.");
            }
        }
        return number;
    }

    /**
     * Returns with WorkMonth with the given list index.
     * @param monthNumber
     * @return WorkMonth month
     */
    private WorkMonth getMonthByIndex(int monthNumber) {
        return timeLogger.getMonths().get(monthNumber);
    }

    /**
     * Returns with an integer value, which must be in a given range.
     * If value is not in range, then user is asked for another value.
     * @param value
     * @param min
     * @param max
     * @param msg
     * @return int value
     */
    private int validatedIntegerValue(int value, int min, int max, String msg) {
        while (value > max || value < min) {
            try {
                value = getIntegerInputFromUser(msg);
            } catch (NumberFormatException e) {
                System.out.println("Only integer numbers are allowed.");
            }
        }
        return value;
    }

    /**
     * User is asked for a value, then with the given offset it checks if 
     * the value is within a valid list index range.
     * On a success returns with the given value + offset.
     * @param max
     * @param message
     * @return int value
     */
    private int validateListSelection(int max, String message) {
        int value = getIntegerInputFromUser(message) + listingToIndexOffset;
        while (value < 0 || value >= max) {
            try {
                value = getIntegerInputFromUser(message) + listingToIndexOffset;
            } catch (NumberFormatException e) {
                System.out.println("Only integer numbers are allowed.");
            }
        }
        return value;
    }

    /**
     * Tries to parse the given timeString input as a LocalTime object.
     * If it fails, then ask the user for another input.
     * On a success returns the given timeString value.
     * @param timeString
     * @return String timeString
     */
    private String validateTimeInput(String timeString) {
        while (true) {
            try {
                LocalTime time = LocalTime
                        .parse(timeString, DateTimeFormatter.ofPattern("H:M"));
                break;
            } catch (DateTimeParseException e) {
                timeString = getStringInputFromUser("Allowed "
                        + "time format is hh:mm");
            }
        }
        return timeString;
    }

    /**
     * Returns with WorkDay with given month and day index values.
     * @param month
     * @param day
     * @return WorkDay day
     */
    private WorkDay getDayWithGivenMonthAndDay(int month, int day) {
        return timeLogger.getMonths().get(month).getDays().get(day);
    }

    /**
     * Lists the days in given month.
     * @param month 
     */
    private void listDaysInMonth(int month) {
        timeLogger.getMonths().get(month).getDays().forEach(day -> {
            System.out.println(timeLogger.getMonths().get(month).getDays()
                    .indexOf(day) + 1 + ". " + day.getActualDay());
        });
    }

    /**
     * Lists the tasks in a given day.
     * @param month
     * @param day 
     */
    private void listTasksInDay(int month, int day) {
        if (getDayWithGivenMonthAndDay(month, day).getTasks().isEmpty()) {
            System.out.println("The given day has no tasks.");
        } else {
            getDayWithGivenMonthAndDay(month, day).getTasks().forEach(task -> {
                System.out.println(getDayWithGivenMonthAndDay(month, day)
                        .getTasks().indexOf(task) + 1 + ". " + task.toString());
            });
        }
    }

    private int getMonthsSize() {
        return timeLogger.getMonths().size();
    }

    private int getDaysSize(int month) {
        return timeLogger.getMonths().get(month).getDays().size();
    }

    /**
     * If input is empty, then returns with an empty String. Otherwise validates
     * the input as a time input.
     * @param input
     * @return String input
     */
    private String optionalTimeInput(String input) {
        if (input.isBlank()) {
            return "";
        }
        return validateTimeInput(input);
    }
}