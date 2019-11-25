package timelogger;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import timelogger.exceptions.NotNewDateException;
import timelogger.exceptions.NotTheSameMonthException;
import timelogger.exceptions.WeekendNotEnabledException;

/**
 * 
 * @author Dubi
 */
public class WorkMonth{

    private final List<WorkDay> days = new ArrayList<>();
    @Getter private YearMonth date;
    private long sumPerMonth;
    @Getter private long requiredMinPerMonth;

    public WorkMonth() {
    }
    
    public WorkMonth(int year, int month) {
        this.date = YearMonth.of(year, month);
    }

    /**
     * Getter for the list of days.
     * @throws IllegalStateException If given month has no days in it.
     * @return List of WorkDays
     */
    public List<WorkDay> getDays() {
        if (days.isEmpty()) {
            throw new IllegalStateException("The list of days is empty.");
        }
        return days;
    }

    /**
     * Returns with the sum of working hours in a month.
     * @return sumPerMonth
     */
    public long getSumPerMonth() {
        sumPerMonth = days.stream()
                //.filter(day -> !day.getTasks().isEmpty())
                .collect(Collectors.summingLong(day -> day.getSumPerDay()));
        return sumPerMonth;
    }

    /**
     * Returns with the total overtime of a month.
     * @return getSumPerMonth() - requiredMinPerMonth
     */
    public long getExtraMinPerMonth() {
        return getSumPerMonth() - requiredMinPerMonth;
    }

    /**
     * Checks if given workDay already exists in the list of workDays.
     * @param workDay
     * @return Boolean
     */
    public boolean isNewDate(WorkDay workDay) {
        return days.stream().noneMatch(day
                -> day.getActualDay()
                        .equals(workDay.getActualDay()));
    }

    /**
     * Checks if a workday is in the current month.
     * @param workDay
     * @return Boolean
     */
    public boolean isSameMonth(WorkDay workDay) {
        return workDay.getActualDay().getMonth().equals(date.getMonth());
    }

    /**
     * Adds the given workDay to the list of days.
     * Checks if workDay's date is on a weekend, already exists or in another month.
     * @param workDay
     * @param isWeekendEnabled
     * @throws WeekendNotEnabledException
     * @throws NotNewDateException
     * @throws NotTheSameMonthException 
     */
    public void addWorkDay(WorkDay workDay, boolean isWeekendEnabled) throws WeekendNotEnabledException, NotNewDateException, NotTheSameMonthException {

        if (isWeekendEnabled == false && 
                Util.isWeekday(workDay.getActualDay()) == false) {
            throw new WeekendNotEnabledException("Adding days to weekends are disabled.");
            
        } else if (isNewDate(workDay) == false) {
            throw new NotNewDateException("This workday already exists.");
            
        } else if (isSameMonth(workDay) == false) {
            throw new NotTheSameMonthException("Not all the workdays are in the same month.");
        }
        else
        if ((isWeekendEnabled || Util.isWeekday(workDay.getActualDay())) && 
                isNewDate(workDay) && isSameMonth(workDay)) {
            requiredMinPerMonth += workDay.getRequiredMinPerday();
            days.add(workDay);
        }   
    }

    /**
     * Same as the other addWorkDay(), but isWeekendEnabled false by default.
     * @param workday
     * @throws WeekendNotEnabledException
     * @throws NotNewDateException
     * @throws NotTheSameMonthException 
     */
    public void addWorkDay(WorkDay workday) throws WeekendNotEnabledException, NotNewDateException, NotTheSameMonthException {
        boolean isWeekendEnabled = false;
        addWorkDay(workday, isWeekendEnabled);
    }
}
