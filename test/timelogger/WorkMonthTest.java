package timelogger;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import timelogger.exceptions.EmptyTimeFieldException;
import timelogger.exceptions.InvalidTaskIdException;
import timelogger.exceptions.NegativeMinutesOfWorkException;
import timelogger.exceptions.NoTaskIdException;
import timelogger.exceptions.NotExpectedTimeOrderException;
import timelogger.exceptions.NotNewDateException;
import timelogger.exceptions.NotSeparatedTimesException;
import timelogger.exceptions.NotTheSameMonthException;
import timelogger.exceptions.WeekendNotEnabledException;

/**
 *
 * @author Dubi
 */
public class WorkMonthTest {
    
    public WorkMonthTest() {
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
    public void testGetSumPerMonth() throws NotExpectedTimeOrderException, EmptyTimeFieldException, InvalidTaskIdException, NoTaskIdException, NegativeMinutesOfWorkException, NotSeparatedTimesException, WeekendNotEnabledException, NotNewDateException, NotTheSameMonthException{
        Task task1 = new Task("0404", "task1", "7:30", "8:45");
        WorkDay workday1 = new WorkDay(420, 2016, 9, 9);
        workday1.addTask(task1);
        
        Task task2 = new Task("4040", "task2", "8:45", "9:45");
        WorkDay workday2 = new WorkDay(420, 2016, 9, 1);
        workday2.addTask(task2);
        
        WorkMonth workmonth = new WorkMonth(2016, 9);
        workmonth.addWorkDay(workday1);
        workmonth.addWorkDay(workday2);
        
        assertEquals(135, workmonth.getSumPerMonth());
    }
    
    @Test
    public void testGetSumPerMonthWithEmptyWorkMonth(){
        WorkMonth workmonth = new WorkMonth(2019, 10);
        assertEquals(0, workmonth.getSumPerMonth());
    }
    
    @Test
    public void testGetExtraMinPerMonth() throws NotExpectedTimeOrderException, EmptyTimeFieldException, InvalidTaskIdException, NoTaskIdException, NegativeMinutesOfWorkException, NotSeparatedTimesException, WeekendNotEnabledException, NotNewDateException, NotTheSameMonthException{
        Task task1 = new Task("4435", "task 1", "7:30", "8:45");
        WorkDay workday1 = new WorkDay(420, 2016, 9, 9);
        workday1.addTask(task1);
        
        Task task2 = new Task("5602", "task 1", "8:45", "9:45");
        WorkDay workday2 = new WorkDay(420, 2016, 9, 1);
        workday2.addTask(task2);
        
        WorkMonth workmonth = new WorkMonth(2016, 9);
        workmonth.addWorkDay(workday1);
        workmonth.addWorkDay(workday2);
        
        assertEquals(-705, workmonth.getExtraMinPerMonth());
    }
    
    @Test
    public void testEmptyGetExtraMinPerMonth(){
        WorkMonth workmonth = new WorkMonth(2019, 10);
        assertEquals(0, workmonth.getExtraMinPerMonth());
    }
    
    @Test
    public void testGetRequiredMinPerMonth() throws NegativeMinutesOfWorkException, WeekendNotEnabledException, NotNewDateException, NotTheSameMonthException{
        WorkDay workday1 = new WorkDay(420, 2016, 9, 1);
        WorkDay workday2 = new WorkDay(420, 2016, 9, 9);
        
        WorkMonth workmonth = new WorkMonth(2016, 9);
        workmonth.addWorkDay(workday1);
        workmonth.addWorkDay(workday2);
        assertEquals(840, workmonth.getRequiredMinPerMonth());
    }
    
    @Test
    public void testGetRequiredMinPerMonthEmpty(){
        WorkMonth workmonth = new WorkMonth(2019, 10);
        assertEquals(0, workmonth.getRequiredMinPerMonth());
    }
    
    @Test
    public void testGetSumPerDayEqualsGetSumPerMonth() throws NotExpectedTimeOrderException, EmptyTimeFieldException, InvalidTaskIdException, NoTaskIdException, NotSeparatedTimesException, WeekendNotEnabledException, NotNewDateException, NotTheSameMonthException{
        Task task = new Task("LT-1177", "-", "7:30", "8:45");
        WorkDay workday = new WorkDay(2016, 9, 9);
        workday.addTask(task);
        
        WorkMonth workmonth = new WorkMonth(2016, 9);
        workmonth.addWorkDay(workday);
        
        assertEquals(workday.getSumPerDay(), workmonth.getSumPerMonth());
    }
    
    @Test
    public void testGetSumPerDayEqualsGetSumPerMonthWithIsWeekendEnabled() throws NotExpectedTimeOrderException, EmptyTimeFieldException, InvalidTaskIdException, NoTaskIdException, NotSeparatedTimesException, WeekendNotEnabledException, NotNewDateException, NotTheSameMonthException{
        Task task = new Task("2468", "task", "7:30", "8:45");
        WorkDay workday = new WorkDay(2016, 8, 28);
        workday.addTask(task);
        WorkMonth workmonth = new WorkMonth(2016, 8);
        workmonth.addWorkDay(workday, true);
        
        assertEquals(workday.getSumPerDay(), workmonth.getSumPerMonth());
    }
    
    @Test(expected = WeekendNotEnabledException.class)
    public void testAddWorkDayWithFalseIsWeekendEnabledOnWeekend() throws NotExpectedTimeOrderException, EmptyTimeFieldException, InvalidTaskIdException, NoTaskIdException, NotSeparatedTimesException, WeekendNotEnabledException, NotNewDateException, NotTheSameMonthException{
        Task task = new Task("1357", "task", "7:30", "8:45");
        WorkDay workday = new WorkDay(2016, 8, 28);
        workday.addTask(task);
        WorkMonth workmonth = new WorkMonth(2016, 8);
        workmonth.addWorkDay(workday, false);
    }
    
    @Test(expected = NotNewDateException.class)
    public void testWorkDaysWithSameActualDay() throws WeekendNotEnabledException, NotNewDateException, NoTaskIdException, NotTheSameMonthException{
        WorkDay workday1 = new WorkDay(2016, 9, 1);
        WorkDay workday2 = new WorkDay(2016, 9, 1);
        WorkMonth workmonth = new WorkMonth(2016, 9);
        
        workmonth.addWorkDay(workday1);
        workmonth.addWorkDay(workday2);        
    }
    
    @Test(expected = NotTheSameMonthException.class)
    public void testAddWorkDayWithDifferentMonths() throws WeekendNotEnabledException, NotNewDateException, NotTheSameMonthException{
        WorkDay workday1 = new WorkDay(2016, 9, 1);
        WorkDay workday2 = new WorkDay(2016, 8, 30);
        WorkMonth workmonth = new WorkMonth(2016, 9);
        
        workmonth.addWorkDay(workday1);
        workmonth.addWorkDay(workday2);        
    }
    
    @Test(expected = EmptyTimeFieldException.class)
    public void testGetSumPerMonthWithTaskEmptyTimeFileds() throws EmptyTimeFieldException, NoTaskIdException, InvalidTaskIdException, NotSeparatedTimesException, WeekendNotEnabledException, NotNewDateException, NotTheSameMonthException, NotExpectedTimeOrderException{
        Task task = new Task("4132");
        WorkDay workday = new WorkDay(2016, 9, 1);
        WorkMonth workmonth = new WorkMonth(2016, 9);
        
        workday.addTask(task);
        workmonth.addWorkDay(workday);
        
        workmonth.getSumPerMonth();
    }
    
    @Test(expected = EmptyTimeFieldException.class)
    public void testGetExtraMinPerMonthWithTaskTimeFiledsEmpty() throws EmptyTimeFieldException, NoTaskIdException, InvalidTaskIdException, NotSeparatedTimesException, WeekendNotEnabledException, NotNewDateException, NotTheSameMonthException, NotExpectedTimeOrderException{
        Task task = new Task("LT-0001");
        WorkDay workday = new WorkDay(2016, 9, 1);
        WorkMonth workmonth = new WorkMonth(2016, 9);
        
        workday.addTask(task);
        workmonth.addWorkDay(workday);
        
        workmonth.getExtraMinPerMonth();
    }
  
}
