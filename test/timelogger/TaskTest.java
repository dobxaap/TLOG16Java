package timelogger;


import java.time.LocalTime;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import static org.junit.Assert.*;
import org.junit.Test;
import timelogger.exceptions.EmptyTimeFieldException;
import timelogger.exceptions.InvalidTaskIdException;
import timelogger.exceptions.NoTaskIdException;
import timelogger.exceptions.NotExpectedTimeOrderException;

/**
 *
 * @author Dubi
 */
public class TaskTest {
    
    public TaskTest() {
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

    @Test(expected = NotExpectedTimeOrderException.class)
    public void testTaskTimeOrder() throws NotExpectedTimeOrderException, EmptyTimeFieldException, InvalidTaskIdException, NoTaskIdException{
        Task task = new Task("0000", "-", "08:30", "07:30");
    }
    
    @Test(expected = EmptyTimeFieldException.class)
    public void testEndTimeNotEmpty() throws NotExpectedTimeOrderException, EmptyTimeFieldException, InvalidTaskIdException, NoTaskIdException{
        Task task = new Task("0000", "-", "08:30", "");
    }
    
    @Test
    public void testMinutePerTask() throws NotExpectedTimeOrderException, EmptyTimeFieldException, InvalidTaskIdException, NoTaskIdException{
        Task task  = new Task("1234", "-", "7:30", "8:45");
        assertEquals(75L, task.getMinPerTask());
    }
    
    @Test(expected = InvalidTaskIdException.class)
    public void testRedmineTaskIdValidity() throws EmptyTimeFieldException, InvalidTaskIdException, NoTaskIdException{
        Task task = new Task("10101");
        
    }
    
    @Test(expected = InvalidTaskIdException.class)
    public void testLTTaskIdValidity() throws EmptyTimeFieldException, InvalidTaskIdException, NoTaskIdException{
        Task task = new Task("LT-12121");
    }
    
    @Test(expected = NoTaskIdException.class)
    public void testEmptyTaskId() throws EmptyTimeFieldException, NoTaskIdException, InvalidTaskIdException{
        Task task = new Task("");
    }
    
    @Test
    public void testEmptyComment() throws NotExpectedTimeOrderException, EmptyTimeFieldException, InvalidTaskIdException, NoTaskIdException{
            Task task = new Task("1234", "", "1:20", "3:40");
            assertTrue("".equals(task.getComment()));
    }
    
    @Test
    public void testEndTimeRounded() throws NotExpectedTimeOrderException, EmptyTimeFieldException, InvalidTaskIdException, NoTaskIdException{
        Task task = new Task("1111", "-", "7:30", "7:50");
        assertTrue(task.getEndTime().equals(LocalTime.of(7, 45)));
    }
    
    @Test
    public void testStartTimeSetters() throws NotExpectedTimeOrderException, EmptyTimeFieldException, InvalidTaskIdException, NoTaskIdException{
        Task task = new Task("0123", "-", "7:35", "10:00");
        assertTrue(task.getEndTime().equals(LocalTime.of(10, 5)));       
        task.setStartTime(LocalTime.of(7, 55));
        assertTrue(task.getEndTime().equals(LocalTime.of(10, 10)));
        task.setStartTime("8:12");
        assertTrue(task.getEndTime().equals(LocalTime.of(10, 12)));
        task.setStartTime(9, 20);
        assertTrue(task.getEndTime().equals(LocalTime.of(10, 5)));        
    }
    
    @Test
    public void testEndTimeSetters() throws NotExpectedTimeOrderException, EmptyTimeFieldException, InvalidTaskIdException, NoTaskIdException{
        Task task = new Task("3210", "-", "1:30", "8:20");
        assertTrue(task.getEndTime().equals(LocalTime.of(8, 15)));
        task.setEndTime(LocalTime.of(2, 2));
        assertTrue(task.getEndTime().equals(LocalTime.of(2, 0)));
        task.setEndTime(6, 12);
        assertTrue(task.getEndTime().equals(LocalTime.of(6, 15)));
        task.setEndTime("12:21");
        assertTrue(task.getEndTime().equals(LocalTime.of(12, 15)));
    }
    
    @Test(expected = NoTaskIdException.class)
    public void testNullTaskId() throws NotExpectedTimeOrderException, EmptyTimeFieldException, InvalidTaskIdException, NoTaskIdException{
        Task task = new Task(null, "-", "1:00", "2:00");
    }
   
    @Test(expected = InvalidTaskIdException.class)
    public void testInvalidTaskId() throws NotExpectedTimeOrderException, EmptyTimeFieldException, InvalidTaskIdException, NoTaskIdException{
        Task task = new Task("be-invalid", "-", "1:00", "2:00");
    }
    
    @Test(expected = NotExpectedTimeOrderException.class)
    public void testSetStartTimeAfterEndTime() throws NotExpectedTimeOrderException, EmptyTimeFieldException, InvalidTaskIdException, NoTaskIdException{
        Task task = new Task("0123", "-", "4:00", "6:00");
        task.setStartTime("6:15");
    }
    
    @Test(expected = NotExpectedTimeOrderException.class)
    public void testSetEndTimeBeforeStartTime() throws NotExpectedTimeOrderException, EmptyTimeFieldException, InvalidTaskIdException, NoTaskIdException{
        Task task = new Task("0123", "-", "4:00", "6:00");
        task.setEndTime("3:45");
    }
    
    @Test(expected = EmptyTimeFieldException.class)
    public void testGetMinPerTaskWithoutTime() throws NotExpectedTimeOrderException, EmptyTimeFieldException, InvalidTaskIdException, NoTaskIdException{
        Task task = new Task("3210");
        long test = task.getMinPerTask();
    }
    
    @Test
    public void testSetStartTimeBehaviour() throws NotExpectedTimeOrderException, EmptyTimeFieldException, InvalidTaskIdException, NoTaskIdException{
        Task task = new Task("0123", "-", "7:30", "7:45");
        task.setStartTime("7:00");
        assertTrue(task.getStartTime().equals(LocalTime.of(7, 0)));
    }
    
    @Test
    public void testSetEndTimeBehaviour() throws NotExpectedTimeOrderException, EmptyTimeFieldException, InvalidTaskIdException, NoTaskIdException{
        Task task = new Task("3210", "-", "7:30", "7:45");
        task.setEndTime("8:00");
        assertTrue(task.getEndTime().equals(LocalTime.of(8, 0)));
    }
    
    @Test
    public void testTaskBehaviour() throws NotExpectedTimeOrderException, EmptyTimeFieldException, InvalidTaskIdException, NoTaskIdException{
        Task task = new Task("LT-1111", "-", "8:00", "12:00");
        assertTrue("LT-1111".equals(task.getTaskId()));
        assertTrue("-".equals(task.getComment()));
        assertTrue(task.getStartTime().equals(LocalTime.of(8, 0)));
        assertTrue(task.getEndTime().equals(LocalTime.of(12, 0)));
    }
    
}
