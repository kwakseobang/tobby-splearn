package org.kwakmunsu.splearn.adapter;

import java.time.LocalDateTime;
import org.kwakmunsu.splearn.domain.member.DuplicateEmailException;
import org.kwakmunsu.splearn.domain.member.DuplicateProfileException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ApiControllerAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleException(Exception ex) {
        return getProblemDetail(HttpStatus.INTERNAL_SERVER_ERROR, ex);
    }

    @ExceptionHandler({DuplicateEmailException.class, DuplicateProfileException.class})
    public ProblemDetail handleDuplicateEmailException(DuplicateEmailException ex) {
        return getProblemDetail(HttpStatus.CONFLICT, ex);
    }

    private ProblemDetail getProblemDetail(HttpStatus httpStatus, Exception ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(httpStatus, ex.getMessage());

        problemDetail.setProperty("timestamp", LocalDateTime.now());
        problemDetail.setProperty("exception", ex.getClass().getSimpleName());

        return problemDetail;
    }

}