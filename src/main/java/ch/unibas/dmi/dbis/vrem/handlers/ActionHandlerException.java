package ch.unibas.dmi.dbis.vrem.handlers;

public class ActionHandlerException extends Exception {
    private static final long serialVersionUID = -3925174394057916202L;

    ActionHandlerException(String message) {
        super(message);
    }

    ActionHandlerException(String message, Throwable cause) {
        super(message, cause);
    }
}
