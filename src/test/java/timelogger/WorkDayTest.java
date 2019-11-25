package timelogger;

import java.time.LocalDate;
import java.time.LocalTime;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import timelogger.exceptions.EmptyTimeFieldException;
import timelogger.exceptions.FutureWorkException;
import timelogger.exceptions.InvalidTaskIdException;
import timelogger.exceptions.NegativeMinutesOfWorkException;
import timelogger.exceptions.NoTaskIdException;
import timelogger.exceptions.NotExpectedTimeOrderException;
import timelogger.exceptions.NotSeparatedTimesException;

/**
 *
 * @author Dubi
 */
public class WorkDayTest {
    
    public WorkDayTest() {
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
    public void testGetExtraMinutesPerDayWithDefaultRequiredMinPerDay() throws NotExpectedTimeOrderException, EmptyTimeFieldException, InvalidTaskIdException, NoTaskIdException, NotSeparatedTimesException{
        Task task = new Task("1234", "", "7:30", "8:45");
        WorkDay workday = new WorkDay();
        workday.addTask(task);
        assertEquals(-375, workday.getExtraMinPerDay());  
    }
    
    @Test
    public void testGetExtraMinutesPerDayWithoutTasks(){
        WorkDay workday = new WorkDay();
        assertEquals(-450, workday.getExtraMinPerDay());
    }
    
    @Test(expected = NegativeMinutesOfWorkException.class)
    public void testNegativeSetRequiredMinPerDay() throws NegativeMinutesOfWorkException{
        WorkDay workday = new WorkDay();
        workday.setRequiredMinPerday(-1);
    }
    
    @Test(expected = NegativeMinutesOfWorkException.class)
    public void testNegativeConstructorRequiredMinPerDay() throws NegativeMinutesOfWorkException{
        WorkDay workday = new WorkDay(-1);
    }
    
    @Test(expected = FutureWorkException.class)
    public void testFutureDateInSetActualDay() throws FutureWorkException{
        WorkDay workday = new WorkDay();
        workday.setActualDay(2020, 4, 1);
    }
    
    @Test
    public void testGetSumPerDayWithMultipleTasks() throws NotExpectedTimeOrderException, EmptyTimeFieldException, InvalidTaskIdException, NoTaskIdException, NotSeparatedTimesException{
        Task task1 = new Task("1234", "1st task", "7:30", "8:45");
        Task task2 = new Task("4321", "2nd task", "8:45", "9:45");
        WorkDay workday = new WorkDay();
        workday.addTask(task1);
        workday.addTask(task2);
        assertEquals(135, workday.getSumPerDay());        
    }
    
    @Test
    public void testGetSumPerDayEmpty(){
        WorkDay workday = new WorkDay();
        assertEquals(0, workday.getSumPerDay());
    }
    
    @Test
    public void testEndTimeOfTheLastTaskWithMultipleTasks()throws NotExpectedTimeOrderException, EmptyTimeFieldException, InvalidTaskIdException, NoTaskIdException, NotSeparatedTimesException{
        Task task1 = new Task("1111", "1st task", "7:30", "8:45");
        Task task2 = new Task("2222", "2nd task", "9:30", "11:45");
        WorkDay workday = new WorkDay();
        workday.addTask(task1);
        workday.addTask(task2);
        assertTrue(LocalTime.of(11, 45).equals(workday.getLatestTaskEndTime()));
    }
    
    @Test
    public void testEndTimeOfTheLastTaskWithoutTask(){
        WorkDay workday = new WorkDay();
        assertNull(workday.getLatestTaskEndTime());
    }
    
    @Test(expected = NotSeparatedTimesException.class)
    public void testTasksTimeOverlap()throws NotExpectedTimeOrderException, EmptyTimeFieldException, InvalidTaskIdException, NoTaskIdException, NotSeparatedTimesException{
        Task task1 = new Task("1111", "1st task", "7:30", "8:45");
        Task task2 = new Task("2222", "2nd task", "8:30", "9:45");
        WorkDay workday = new WorkDay();
        workday.addTask(task1);
        workday.addTask(task2);
    }
    
    @Test
    public void testWorkDayWithGivenDateAndRequiredMinutesPerDay() throws NegativeMinutesOfWorkException{
        WorkDay workday = new WorkDay(200, 2000, 2, 2);
        assertEquals(200, workday.getRequiredMinPerday());
        assertEquals(-200, workday.getExtraMinPerDay());
        assertTrue(workday.getActualDay().equals(LocalDate.of(2000, 2, 2)));
    }
    
    @Test
    public void testWorkDayWithGivenDateAndDefaultRequiredMinutesPerDay(){
        WorkDay workday = new WorkDay(2002, 2, 4);
        assertEquals(450, workday.getRequiredMinPerday());
        assertEquals(-450, workday.getExtraMinPerDay());
        assertTrue(workday.getActualDay().equals(LocalDate.of(2002, 2, 4)));
    }
    
    @Test
    public void testWorkDayWithDefaultDateAndGivenRequiredMinutesPerDay() throws NegativeMinutesOfWorkException{
        WorkDay workday = new WorkDay(300);
        assertEquals(300, workday.getRequiredMinPerday());
        assertEquals(-300, workday.getExtraMinPerDay());
        assertTrue(workday.getActualDay().equals(LocalDate.now()));
    }
    
    @Test
    public void testWorkDayWithDefaultValues(){
        WorkDay workday = new WorkDay();
        assertEquals(450, workday.getRequiredMinPerday());
        assertEquals(-450, workday.getExtraMinPerDay());
        assertTrue(workday.getActualDay().equals(LocalDate.now()));
    }
    
    @Test
    public void testWorkDayArbitaryDateEqualsActualDate(){
        WorkDay workday = new WorkDay(2016, 9, 1);
        assertTrue(workday.getActualDay().equals(LocalDate.of(2016, 9, 1)));
    }
    
    @Test
    public void testWorkDayWithGivenRequiredMinutesPerDay() throws NegativeMinutesOfWorkException{
        WorkDay workday = new WorkDay(2077, 10, 10);
        workday.setRequiredMinPerday(300);
        assertEquals(300, workday.getRequiredMinPerday());
    }
    
    @Test(expected = EmptyTimeFieldException.class)
    public void testTaskWithoutTimeFields() throws EmptyTimeFieldException, NoTaskIdException, InvalidTaskIdException, NegativeMinutesOfWorkException, NotSeparatedTimesException, NotExpectedTimeOrderException{
        Task task = new Task("LT-6789");
        WorkDay workday = new WorkDay(222, 2022, 1, 1);
        workday.addTask(task);
        workday.getSumPerDay();
    }
    
    @Test(expected = NotSeparatedTimesException.class)
    public void testTasksWithRoundedEndtimeOverlap() throws NotExpectedTimeOrderException, EmptyTimeFieldException, InvalidTaskIdException, NoTaskIdException, NotSeparatedTimesException{
        Task task1 = new Task("LT-0001", "task 1", "8:45", "9:50");
        Task task2 = new Task("0001", "task 2", "8:20", "8:45");
        WorkDay workday = new WorkDay();
        workday.addTask(task1);
        workday.addTask(task2);
    }
    
}
