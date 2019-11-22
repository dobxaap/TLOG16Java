package timelogger;

import java.util.stream.Collectors;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import timelogger.exceptions.EmptyTimeFieldException;
import timelogger.exceptions.InvalidTaskIdException;
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
public class TimeLoggerTest {
    
    public TimeLoggerTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void testGetMinPerTaskEqualsGetSumPerMonthInTimeLogger() throws NotExpectedTimeOrderException, EmptyTimeFieldException, InvalidTaskIdException, NoTaskIdException, NotSeparatedTimesException, WeekendNotEnabledException, NotNewDateException, NotTheSameMonthException, NotNewMonthException{
        WorkDay workday = new WorkDay(2016, 4, 14);
        WorkMonth workmonth = new WorkMonth(2016, 4);
        Task task = new Task("1248", "task", "7:30", "10:30");
        workday.addTask(task);
        workmonth.addWorkDay(workday);
        
        TimeLogger timelogger = new TimeLogger();
        timelogger.addMonth(workmonth);
        
        long sumMonth = timelogger.getMonths().stream()
                .collect(Collectors.summingLong(WorkMonth::getSumPerMonth));
        
        assertEquals(task.getMinPerTask(),sumMonth);
    }
    
    @Test(expected = NotNewMonthException.class)
    public void test() throws NotNewMonthException{
        WorkMonth workmonth1 = new WorkMonth(2016, 4);
        WorkMonth workmonth2 = new WorkMonth(2016, 4);
        TimeLogger timelogger = new TimeLogger();
        
        timelogger.addMonth(workmonth1);
        timelogger.addMonth(workmonth2);
    }
    
}
