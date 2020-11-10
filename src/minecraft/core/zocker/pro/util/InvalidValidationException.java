package minecraft.core.zocker.pro.util;

public class InvalidValidationException extends RuntimeException {

    /**
     * The constant serialVersionUID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Instantiates a new Invalid validation exception.
     *
     * @param t the t
     */
    public InvalidValidationException(Throwable t) {
        super(t);
    }

    /**
     * Instantiates a new Invalid validation exception.
     *
     * @param message the message
     */
    public InvalidValidationException(String message) {
        super(message);
    }

    /**
     * Instantiates a new Invalid validation exception.
     *
     * @param t       the t
     * @param message the message
     */
    public InvalidValidationException(Throwable t, String message) {
        super(message, t);
    }

    /**
     * Instantiates a new Invalid validation exception.
     */
    public InvalidValidationException() {
    }

    /**
     * Gets message.
     *
     * @return the message
     */
    @Override
    public String getMessage() {
        return "Report / " + super.getMessage();
    }
}
