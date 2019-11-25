package timelogger;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import timelogger.exceptions.EmptyTimeFieldException;
import timelogger.exceptions.NotExpectedTimeOrderException;

/**
 * 
 * @author Dubi
 */
public class Util {

    private static final int QUARTER_OF_HOUR_IN_MIN = 15;
    private static final int HALF_OF_QUARTER_OF_HOUR_IN_MIN = 
            (QUARTER_OF_HOUR_IN_MIN % 2 == 0)
            ?QUARTER_OF_HOUR_IN_MIN/2 : QUARTER_OF_HOUR_IN_MIN/2+1;

    /**
     * Returns endTime, which value depends on startTime. 
     * If endTime not multiples of quarter hour apart from startTime, 
     * then it will be rounded.
     * @param startTime
     * @param endTime
     * @return LocalTime endTime
     * @throws EmptyTimeFieldException
     * @throws NotExpectedTimeOrderException 
     */
    public static LocalTime roundToMultipleQuaterHour(LocalTime startTime, LocalTime endTime) throws EmptyTimeFieldException, NotExpectedTimeOrderException {
        long duration = startTime.until(endTime, ChronoUnit.MINUTES);

        if (isMultipleQuarterHour(startTime, endTime)) {
            return endTime;
        } else {
            return (duration % QUARTER_OF_HOUR_IN_MIN > HALF_OF_QUARTER_OF_HOUR_IN_MIN)
                    ? endTime.plus((QUARTER_OF_HOUR_IN_MIN - duration % QUARTER_OF_HOUR_IN_MIN), ChronoUnit.MINUTES)
                    : endTime.minusMinutes((duration % QUARTER_OF_HOUR_IN_MIN));
        }
    }

    /**
     * Checks if given task is separated from any given list of tasks.
     * @param task
     * @param tasks
     * @return Boolean
     */
    public static boolean isSeparatedTime(Task task, List<Task> tasks) {
        return tasks.stream()
                .noneMatch(taskElement
                        -> ((taskElement.getStartTime().isBefore(task.getEndTime()))
                && (taskElement.getEndTime().isAfter(task.getStartTime())))
                || (task.getStartTime().equals(task.getEndTime()) && 
                        taskElement.getStartTime().equals(task.getEndTime()))
                || (taskElement.getStartTime().equals(taskElement.getEndTime()) 
                        && task.getStartTime().equals(taskElement.getEndTime())));
    }

    /**
     * Checks if given date is on a weekday.
     * @param date
     * @return Boolean
     */
    public static boolean isWeekday(LocalDate date) {
        return date.getDayOfWeek() != DayOfWeek.SATURDAY
                && date.getDayOfWeek() != DayOfWeek.SUNDAY;
    }

    /**
     * Checks if the time between startTime and endTime is 
     * multiples of a quarter of hour.
     * @param startTime
     * @param endTime
     * @return Boolean
     * @throws EmptyTimeFieldException
     * @throws NotExpectedTimeOrderException 
     */
    public static boolean isMultipleQuarterHour(LocalTime startTime, LocalTime endTime) throws EmptyTimeFieldException, NotExpectedTimeOrderException {
        if (startTime == null || endTime == null) {
            throw new EmptyTimeFieldException("Start time or end time is missing.");
        } else if (startTime.isAfter(endTime)) {
            throw new NotExpectedTimeOrderException("Start time is after end time.");
        }
        long duration = startTime.until(endTime, ChronoUnit.MINUTES);
        return (duration % QUARTER_OF_HOUR_IN_MIN == 0);
    }
}