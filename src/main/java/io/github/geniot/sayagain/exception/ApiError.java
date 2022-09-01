package io.github.geniot.sayagain.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.List;

@Getter
@Setter
public class ApiError extends RuntimeException {

    private HttpStatus status;
    private String message;
    private List<String> errors;

    public ApiError() {
    }

    public ApiError(final HttpStatus status, final String message, final List<String> errors) {
        super();
        this.status = status;
        this.message = message;
        this.errors = errors;
    }

    public ApiError(final HttpStatus status, final String message, final String error) {
        super();
        this.status = status;
        this.message = message;
        errors = List.of(error);
    }

    public ApiError(final HttpStatus status, final String message) {
        super();
        this.status = status;
        this.message = message;
    }


}
