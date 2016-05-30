package fi.hel.allu.ui.exception;

public class NotFoundException extends RuntimeException {
    private static final long serialVersionUID = 15424L;

    public NotFoundException(String message) {
        super(message);
    }

}
