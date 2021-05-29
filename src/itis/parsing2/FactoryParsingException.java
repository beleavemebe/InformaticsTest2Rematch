package itis.parsing2;

import java.util.List;

public class FactoryParsingException extends RuntimeException {


    private List<FactoryValidationError> validationErrors;

    public FactoryParsingException(String message, List<FactoryValidationError> validationErrors) {
        super(message);
        this.validationErrors = validationErrors;
    }

    public List<FactoryValidationError> getValidationErrors() {
        return validationErrors;
    }

    public void setValidationErrors(List<FactoryValidationError> validationErrors) {
        this.validationErrors = validationErrors;
    }

    public static class FactoryValidationError {

        @Override
        public String toString() {
            return "ParkValidationError{" +
                    "fieldName='" + fieldName + '\'' +
                    ", validationError='" + validationError + '\'' +
                    '}';
        }

        public String fieldName;
        public String validationError;

        public FactoryValidationError(String fieldName, String validationError) {
            this.fieldName = fieldName;
            this.validationError = validationError;
        }
    }

    @Override
    public String toString() {
        return "ParkParsingException{" +
                "validationErrors=" + validationErrors +
                '}';
    }
}


