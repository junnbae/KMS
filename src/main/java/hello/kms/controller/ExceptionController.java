package hello.kms.controller;

import hello.kms.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class ExceptionController {

    @ExceptionHandler(IDExistException.class)
    public ResponseEntity<String> conflict(Exception e){
        log.info("{}", e.getClass());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler({SummonerNameNotExistException.class, UserNotExistException.class, PasswordWrongException.class})
    public ResponseEntity<String> notFound(Exception e){
        log.info("{}", e.getClass());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(RiotApiException.class)
    public ResponseEntity<String> forbidden(Exception e){
        log.info("{}", e.getClass());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
    }
}
