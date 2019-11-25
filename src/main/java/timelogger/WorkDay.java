package timelogger;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import timelogger.exceptions.EmptyTimeFieldException;
import timelogger.exceptions.FutureWorkException;
import timelogger.exceptions.NegativeMinutesOfWorkException;
import timelogger.exceptions.NotExpectedTimeOrderException;
import timelogger.exceptions.NotSeparatedTimesException;

/**
 * 
 * @author Dubi
 */
public class WorkDay {
    private final List<Task> tasks = new ArrayList<>();
    @Getter private long requiredMinPerday;
    @Getter private LocalDate actualDay;
    private long sumPerDay;

    public WorkDay() {
        this.requiredMinPerday = 450;
        this.actualDay = LocalDate.now();
    }

    public WorkDay(long requiredMinPerday) throws NegativeMinutesOfWorkException {
        this.requiredMinPerday = validatedRequiredMinPerDay(requiredMinPerday);
        this.actualDay = LocalDate.now();
    }

    public WorkDay(int year, int month, int day) {
        this.requiredMinPerday = 450;
        this.actualDay = LocalDate.of(year, month, day);
    }

    public WorkDay(long requiredMinPerday, int year, int month, int day) throws NegativeMinutesOfWorkException {
        this.requiredMinPerday = validatedRequiredMinPerDay(requiredMinPerday); 
        this.actualDay = LocalDate.of(year, month, day);
    }

    /**
     * Setter for requiredMinPerDay.
     * @param requiredMinPerday
     * @throws NegativeMinutesOfWorkException
     */
    public void setRequiredMinPerday(long requiredMinPerday) throws NegativeMinutesOfWorkException {
        this.requiredMinPerday = validatedRequiredMinPerDay(requiredMinPerday);
    }

    /**
     * Setter for actualDay.
     * @param year
     * @param month
     * @param day
     * @throws FutureWorkException
     */
    public void setActualDay(int year, int month, int day) throws FutureWorkException {
        this.actualDay = validatedActualDay(LocalDate.of(year, month, day));
    }
    
    /**
     * Getter, which returns with the list of tasks.
     * @throws IllegalStateException If the list of tasks is empty.
     * @return List of tasks
     */
    public List<Task> getTasks(){
        if (tasks.isEmpty()) {
            throw new IllegalStateException("The list of tasks is emtpy.");
        }
        return tasks;
    }

    /**
     * Returns with the sum of minutes of finished tasks.
     * @return long
     */
    public long getSumPerDay() {
        return tasks.stream()
                .collect(Collectors.summingLong(task -> {
                    try {
                        return task.getMinPerTask();
                    } catch (EmptyTimeFieldException ex) {}
                    return 0;
        }));
    }
    
    /**
     * Returns with the amount of overtime in the current day.
     * @return int sumPerDay - requiredMinPerday
     */
    public long getExtraMinPerDay(){
        sumPerDay = getSumPerDay();
        return sumPerDay - requiredMinPerday;
    }
    
    /**
     * Adds a task to the list of tasks.
     * Checks if given task has any missing time fields or overlap with other task in tasks list.
     * @param task
     * @throws NotSeparatedTimesException
     * @throws EmptyTimeFieldException
     * @throws NotExpectedTimeOrderException 
     */
    public void addTask(Task task) throws NotSeparatedTimesException, EmptyTimeFieldException, NotExpectedTimeOrderException{
        
        if(task.getStartTime() == null || task.getEndTime() == null){
            throw new EmptyTimeFieldException("Cannot add task with empty time fields.");
        }else if(!Util.isSeparatedTime(task, tasks)){
            throw new NotSeparatedTimesException("There is an overlap between tasks start and end time.");
        }else if (Util.isMultipleQuarterHour(task.getStartTime(), task.getEndTime()) && 
                Util.isSeparatedTime(task, tasks)) {
            tasks.add(task);
        }
    }
    
    /**
     * Returns with the latest task's end time, if the list is not empty.
     * @return LocalTime endTime or null
     */
    public LocalTime getLatestTaskEndTime(){
        if (tasks.isEmpty()) {
            return null;
        }
        return tasks.get(tasks.size() -1).getEndTime();
    }
    
    /**
     * Deletes the given task from the tasks list.
     * @param task
     * @return List of tasks
     */
    public List<Task> deleteTask(Task task){
        tasks.remove(task);
        return tasks;
    }
    
    /**
     * Modifies the given task with given list of parameters.
     * @param task
     * @param modTask
     * @throws NotExpectedTimeOrderException
     * @throws EmptyTimeFieldException 
     */
    public void modifyTask(Task task, List<String> modTask) throws NotExpectedTimeOrderException, EmptyTimeFieldException{
        if (!"".equals(modTask.get(0))) {
            getTask(task).setTaskId(modTask.get(0));
        }
        if (!"".equals(modTask.get(1))) {
            getTask(task).setComment(modTask.get(1));
        }
        if (!"".equals(modTask.get(2))) {
            getTask(task).setStartTime(modTask.get(2));
        }
        if ("".equals(modTask.get(3))) {
            getTask(task).setEndTime(modTask.get(3));
        }
    }

    private Task getTask(Task task){
        return tasks.get(tasks.indexOf(task));
    }
    
    @Override
    public String toString() {
        return "WorkDay{" + "tasks=" + tasks + ", requiredMinPerday=" + requiredMinPerday + ", actualDay=" + actualDay + ", sumPerDay=" + sumPerDay + '}';
    }

    /**
     * Returns with requiredMinPerDay, if it is valid.
     * @param requiredMinPerday
     * @return long requiredMinPerDay
     * @throws NegativeMinutesOfWorkException 
     */
    private long validatedRequiredMinPerDay(long requiredMinPerday) throws NegativeMinutesOfWorkException {
        if (requiredMinPerday < 0) {
            throw new NegativeMinutesOfWorkException("The required time cannot be negative.");
        } 
        return requiredMinPerday;
    }

    /**
     * Returns with given date, if it is valid.
     * @param date
     * @return LocalDate date
     * @throws FutureWorkException 
     */
    private LocalDate validatedActualDay(LocalDate date) throws FutureWorkException {
        if (date.isAfter(LocalDate.now())) {
            throw new FutureWorkException("Date cannot be in the future.");
        }
        return date;
    }
    
    /**
     * Sets the given task startTime with the given parameter.
     * @param task
     * @param startTime
     * @throws NotExpectedTimeOrderException
     * @throws EmptyTimeFieldException 
     */
    public void startTask(Task task, String startTime) throws NotExpectedTimeOrderException, EmptyTimeFieldException{
        task.setStartTime(startTime);
        tasks.add(task);
    }
    
}
