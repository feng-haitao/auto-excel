package net.fenghaitao.exception;

public class AutoExcelException extends RuntimeException {
    public AutoExcelException(String message) { super(message); }
    public AutoExcelException(Throwable cause) { super(cause); }
    public AutoExcelException(String message, Throwable cause) {
        super(message, cause);
    }
}
