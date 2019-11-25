package timelogger.exceptions;

/**
 *
 * @author Dubi
 */
public class NegativeMinutesOfWorkException extends Exception{

    /**
     *
     * @param message
     */
    public NegativeMinutesOfWorkException(String message) {
        super(message);
    }
    
}
