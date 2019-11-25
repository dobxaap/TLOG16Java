package timelogger;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import lombok.Getter;
import lombok.Setter;
import timelogger.exceptions.EmptyTimeFieldException;
import timelogger.exceptions.InvalidTaskIdException;
import timelogger.exceptions.NoTaskIdException;
import timelogger.exceptions.NotExpectedTimeOrderException;

/**
 * 
 * @author Dubi
 */
@Getter
public class Task {

    @Setter private String taskId;
    @Setter private String comment;
    private LocalTime startTime;
    private LocalTime endTime;

    public Task(String taskId) throws EmptyTimeFieldException, NoTaskIdException, InvalidTaskIdException {
        this.taskId = evalueateTaskId(taskId);
    }

    public Task(String taskId, String comment,
            int startHour, int startMin, int endHour, int endMin) throws NotExpectedTimeOrderException, InvalidTaskIdException, NoTaskIdException, EmptyTimeFieldException{

        this.taskId = evalueateTaskId(taskId);
        this.comment = comment;
        this.startTime = LocalTime.of(startHour, startMin);
        this.endTime = LocalTime.of(endHour, endMin);
        
        checkTimeOrder(this.startTime, this.endTime);
        endTime = Util.roundToMultipleQuaterHour(startTime, endTime);
    }

    public Task(String taskId, String comment,
            String startTime, String endTime) throws NotExpectedTimeOrderException, EmptyTimeFieldException, InvalidTaskIdException, NoTaskIdException {
        this.taskId = evalueateTaskId(taskId);
        this.comment = comment;
        this.startTime = checkStringTime(startTime);
        this.endTime = checkStringTime(endTime);

        checkTimeOrder(this.startTime, this.endTime);
        this.endTime = Util.roundToMultipleQuaterHour(
                this.startTime, this.endTime);
    }

    /**
     * Setter for startTime with LocalTime parameter.
     * @param startTime
     * @throws NotExpectedTimeOrderException
     * @throws EmptyTimeFieldException
     */
    public void setStartTime(LocalTime startTime) throws NotExpectedTimeOrderException, EmptyTimeFieldException {
        this.startTime = startTime;
        validateSetStartTime();        
    }
    
    /**
     * Setter for startTime with int parameters.
     * @param startHour
     * @param startMin
     * @throws NotExpectedTimeOrderException
     * @throws EmptyTimeFieldException
     */
    public void setStartTime(int startHour, int startMin) throws NotExpectedTimeOrderException, EmptyTimeFieldException{
        this.startTime = LocalTime.of(startHour, startMin);
        validateSetStartTime();        
    }
    
    /**
     * Setter for startTime with String parameter.
     * @param startTime
     * @throws NotExpectedTimeOrderException
     * @throws EmptyTimeFieldException
     */
    public void setStartTime(String startTime) throws NotExpectedTimeOrderException, EmptyTimeFieldException{
        this.startTime = LocalTime
                .parse(startTime, DateTimeFormatter.ofPattern("H:m"));
        validateSetStartTime(); 
    }
    
    /**
     * Rounds the endTime of a task, if it is not multiples of quarter hour away from startTime.
     * @param startTime
     * @throws EmptyTimeFieldException
     * @throws NotExpectedTimeOrderException 
     */
    private void validateSetStartTime() throws EmptyTimeFieldException, NotExpectedTimeOrderException {
        if (this.endTime != null) {
            this.endTime = Util.roundToMultipleQuaterHour(
                    this.startTime,this.endTime);
            checkTimeOrder(this.startTime, endTime);
        }
    }

    /**
     * Setter for endTime with LocalTime parameter.
     * @param endTime
     * @throws NotExpectedTimeOrderException
     * @throws EmptyTimeFieldException
     */
    public void setEndTime(LocalTime endTime) throws NotExpectedTimeOrderException, EmptyTimeFieldException {
        this.endTime = Util.roundToMultipleQuaterHour(startTime, endTime);
        checkTimeOrder(startTime, this.endTime);
    }
    
    /**
     * Setter for endTime with int parameters.
     * @param hour
     * @param minute
     * @throws NotExpectedTimeOrderException
     * @throws EmptyTimeFieldException
     */
    public void setEndTime(int hour, int minute) throws NotExpectedTimeOrderException, EmptyTimeFieldException{
        this.endTime = Util.roundToMultipleQuaterHour(startTime, 
                LocalTime.of(hour, minute));
        checkTimeOrder(startTime, this.endTime);
    }
    
    /**
     * Setter for endTime with String parameter.
     * @param endTime
     * @throws NotExpectedTimeOrderException
     * @throws EmptyTimeFieldException
     */
    public void setEndTime(String endTime) throws NotExpectedTimeOrderException, EmptyTimeFieldException{
        this.endTime = Util.roundToMultipleQuaterHour(startTime, 
                LocalTime.parse(endTime, DateTimeFormatter.ofPattern("H:m")));
        checkTimeOrder(startTime, this.endTime);
    }

    /**
     * Returns the amount of minutes of current task.
     * @return long duration
     * @throws EmptyTimeFieldException 
     */
    public long getMinPerTask() throws EmptyTimeFieldException  {
        if (startTime == null || endTime == null) {
            throw new EmptyTimeFieldException("Missing start or end time.");
        }
        return startTime.until(endTime, ChronoUnit.MINUTES);
    }

    /**
     * Checks if given id is valid.
     * @param id
     * @return Boolean
     */
    public boolean isValidTaskId(String id) {
        return id.matches("(LT-\\d{4}|\\d{4})");
    }

    @Override
    public String toString() {
        return "taskId=" + taskId + ", comment=" + comment + ", startTime=" 
                + startTime + ", endTime=" + endTime;
    }

    /**
     * Checks if given times are in the expected order.
     * @param startTime
     * @param endTime
     * @throws NotExpectedTimeOrderException 
     */
    private void checkTimeOrder(LocalTime startTime, LocalTime endTime) throws NotExpectedTimeOrderException {
        if (startTime.isAfter(endTime)) {
            throw new NotExpectedTimeOrderException("End time is not after start time.");
        }
    }

    /**
     * Returns LocalTime value of given String time.
     * @param time
     * @return LocalTime time
     * @throws EmptyTimeFieldException 
     */
    private LocalTime checkStringTime(String time) throws EmptyTimeFieldException {
        if (time.isBlank()) {
            throw new EmptyTimeFieldException("Time field is blank.");
        }
        return LocalTime.parse(time, DateTimeFormatter.ofPattern("H:m"));
    }

    /**
     * Returns id, if any was given and it is in a valid format.
     * @param id
     * @return String id
     * @throws NoTaskIdException
     * @throws InvalidTaskIdException 
     */
    private String evalueateTaskId(String id) throws NoTaskIdException, InvalidTaskIdException {       
        if ("".equals(id) || id == null)
            throw new NoTaskIdException("Task has no id.");
        else if(isValidTaskId(id) == false)
            throw new InvalidTaskIdException("Not a valid task id.");        
        return id;      
    }
}
