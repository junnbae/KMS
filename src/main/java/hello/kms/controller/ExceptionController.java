package hello.kms.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

@ControllerAdvice
@Slf4j
public class ExceptionController {
    
//    @ExceptionHandler(ResponseStatusException.class)
//    public void responseStatusException(Exception e){
//        log.info("{}", ResponseStatusException.class);
//    }
}
