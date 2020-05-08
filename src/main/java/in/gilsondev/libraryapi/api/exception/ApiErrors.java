package in.gilsondev.libraryapi.api.exception;

import in.gilsondev.libraryapi.exception.BusinessException;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ApiErrors {
    private final List<String> errors;

    public ApiErrors(BindingResult bindingResult) {
        this.errors = new ArrayList<>();
        bindingResult.getAllErrors().forEach(error -> this.errors.add(error.getDefaultMessage()));
    }

    public ApiErrors(BusinessException businessException) {
        this.errors = Collections.singletonList(businessException.getMessage());
    }

    public List<String> getErrors() {
        return errors;
    }
}
