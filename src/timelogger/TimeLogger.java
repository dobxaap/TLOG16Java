package timelogger;

import java.util.ArrayList;
import java.util.List;
import timelogger.exceptions.NotNewMonthException;

/**
 * 
 * @author Dubi
 */
public class TimeLogger {
    private final List<WorkMonth> months = new ArrayList<>();

    /**
     * Getter for months
     * @throws IllegalStateException If the list of months is empty.
     * @return List of months
     */
    public List<WorkMonth> getMonths() {
        if (months.isEmpty()) {
            throw new IllegalStateException("The list of months is empty.");
        }
        return months;
    }
    
    /**
     * Checks if given workMonth is already in the list of months.
     * @param workMonth
     * @return Boolean
     */
    public boolean isNewMonth(WorkMonth workMonth){
        return !months.stream()
                .anyMatch(month -> workMonth.getDate()
                        .equals(month.getDate()));
    }
    
    /**
     * Adds the given workMonth to the list of months.
     * @param workMonth
     * @throws NotNewMonthException 
     */
    public void addMonth(WorkMonth workMonth) throws NotNewMonthException{
        if (isNewMonth(workMonth)) {
            months.add(workMonth);
        }else{
            throw new NotNewMonthException("This month already exists.");
        }
    }   
}