package hello.kms.Controller;

import hello.kms.exception.IdExistException;
import hello.kms.exception.IdNotExistException;
import hello.kms.exception.PasswordNotMatchException;
import hello.kms.exception.RegisterInputException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionController {
    @ExceptionHandler({IdExistException.class, IdNotExistException.class, PasswordNotMatchException.class, RegisterInputException.class})
    public ResponseEntity<String> handServiceException(Exception e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
