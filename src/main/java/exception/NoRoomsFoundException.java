package exception;

public class NoRoomsFoundException extends RuntimeException {
    public NoRoomsFoundException(String message) {
        super(message);
    }
}
