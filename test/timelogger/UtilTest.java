package timelogger;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
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

/**
 *
 * @author Dubi
 */
public class UtilTest {

    public UtilTest() {
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
    public void testRoundToMultipleQuarterHour() throws EmptyTimeFieldException, NotExpectedTimeOrderException {
        LocalTime time = Util.roundToMultipleQuaterHour(LocalTime.of(7, 30), LocalTime.of(7, 50));
        assertTrue(time.equals(LocalTime.of(7, 45)));
    }

    @Test
    public void testFalseIsMultipleQuarterHour() throws EmptyTimeFieldException, NotExpectedTimeOrderException {
        boolean isquarter = Util.isMultipleQuarterHour(LocalTime.of(7, 30), LocalTime.of(7, 50));
        assertFalse(isquarter);
    }

    @Test
    public void testTrueIsMultipleQuarterHour() throws EmptyTimeFieldException, NotExpectedTimeOrderException {
        boolean isquarter = Util.isMultipleQuarterHour(LocalTime.of(7, 30), LocalTime.of(7, 45));
        assertTrue(isquarter);
    }

    @Test(expected = EmptyTimeFieldException.class)
    public void testIsMultipleQuarterHourWithNullStartTime() throws EmptyTimeFieldException, NotExpectedTimeOrderException {
        Util.isMultipleQuarterHour(null, LocalTime.of(7, 45));
    }

    @Test(expected = NotExpectedTimeOrderException.class)
    public void testIsMultipleQuarterHourWithStartTimeAfterEndTime() throws EmptyTimeFieldException, NotExpectedTimeOrderException {
        Util.isMultipleQuarterHour(LocalTime.of(8, 30), LocalTime.of(7, 45));
    }

    @Test
    public void testIsSeparatedTime() throws NotExpectedTimeOrderException, EmptyTimeFieldException, InvalidTaskIdException, NoTaskIdException {
        List<Task> tasks = new ArrayList();
        
        Task task1 = new Task("0001", "-", "6:30", "6:45");
        Task task2 = new Task("0002", "-", "5:30", "6:30");
        tasks = updateTasks(task2, tasks);
        assertTrue(Util.isSeparatedTime(task1, tasks));
        
        Task task3 = new Task("0003", "-", "6:30", "6:45");
        Task task4 = new Task("0004", "-", "6:45", "7:00");
        tasks = updateTasks(task4, tasks);
        assertTrue(Util.isSeparatedTime(task3, tasks));
        
        Task task5 = new Task("0005", "-", "6:30", "6:30");
        Task task6 = new Task("0006", "-", "5:30", "6:30");
        tasks = updateTasks(task6, tasks);
        assertTrue(Util.isSeparatedTime(task5, tasks));
        
        Task task7 = new Task("0007", "-", "6:30", "7:30");
        Task task8 = new Task("0008", "-", "7:30", "7:30");
        tasks = updateTasks(task8, tasks);
        assertTrue(Util.isSeparatedTime(task7, tasks));
        
        Task task9 = new Task("0009", "-", "6:30", "7:00");
        Task task10 = new Task("0010", "-", "6:00", "6:45");
        tasks = updateTasks(task10, tasks);
        assertFalse(Util.isSeparatedTime(task9, tasks));
        
        Task task11 = new Task("0011", "-", "6:30", "7:00");
        Task task12 = new Task("0012", "-", "6:30", "6:45");
        tasks = updateTasks(task12, tasks);
        assertFalse(Util.isSeparatedTime(task11, tasks));
        
        Task task13 = new Task("0013", "-", "6:30", "7:00");
        Task task14 = new Task("0014", "-", "6:45", "7:15");
        tasks = updateTasks(task14, tasks);
        assertFalse(Util.isSeparatedTime(task13, tasks));
        
        Task task15 = new Task("0015", "-", "6:30", "7:00");
        Task task16 = new Task("0016", "-", "6:45", "7:00");
        tasks = updateTasks(task16, tasks);
        assertFalse(Util.isSeparatedTime(task15, tasks));
        
        Task task17 = new Task("0017", "-", "6:30", "6:30");
        Task task18 = new Task("0018", "-", "6:30", "7:00");
        tasks = updateTasks(task18, tasks);
        assertFalse(Util.isSeparatedTime(task17, tasks));
        
        Task task19 = new Task("0019", "-", "6:30", "7:30");
        Task task20 = new Task("0020", "-", "6:30", "6:30");
        tasks = updateTasks(task20, tasks);
        assertFalse(Util.isSeparatedTime(task19, tasks));        
    }

    private List<Task> updateTasks(Task task, List<Task> tasks) {
        if (!tasks.isEmpty()) {
            tasks.remove(0);
        }
        tasks.add(task);
        return tasks;
    }

}