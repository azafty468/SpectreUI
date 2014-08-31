package spectre.exceptions;

/**
 * Created by andrewzafft on 6/3/14.
 */
public class SpectreViewException extends Exception {
    public SpectreViewException(String message) {
        super(message);
    }
    public SpectreViewException(String message, Exception source) {
        super(message, source);
    }
}
