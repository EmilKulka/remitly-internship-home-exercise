package pl.emilkulka.remitly_internship.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class SwiftCodeAlreadyExistsException extends RuntimeException {
    public SwiftCodeAlreadyExistsException(String message) {
        super(message);
    }
}