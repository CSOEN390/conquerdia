package ca.concordia.encs.conquerdia.exception;

import java.util.ArrayList;
import java.util.List;

public class ValidationException extends Exception {
    private final List<String> validationErrors = new ArrayList<>();


    public ValidationException(String message) {
        super(message);
        validationErrors.add("Validation Error! " + message);
    }

    public void addValidationError(String error) {
        validationErrors.add("Validation Error! " + error);
    }

    public List<String> getValidationErrors() {
        return validationErrors;
    }
}