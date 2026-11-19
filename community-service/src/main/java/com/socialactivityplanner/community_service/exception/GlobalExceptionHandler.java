package com.socialactivityplanner.community_service.exception;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  // --- 400 Bad Request: validation errors (@Valid) ---

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex,
      HttpHeaders headers,
      HttpStatusCode status,
      WebRequest request) {
    Map<String, String> fieldErrors = new HashMap<>();
    ex.getBindingResult()
        .getFieldErrors()
        .forEach(error -> fieldErrors.put(error.getField(), error.getDefaultMessage()));

    ProblemDetail problem =
        ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Validation failed");
    problem.setTitle("Invalid Request Content");
    problem.setProperty("errors", fieldErrors);
    problem.setProperty("timestamp", Instant.now());
    return ResponseEntity.badRequest().body(problem);
  }

  // --- 403 Forbidden: access denied / not a member ---

  @ExceptionHandler(GroupAccessDeniedException.class)
  public ProblemDetail handleAccessDenied(GroupAccessDeniedException ex) {
    log.warn("Access denied: {}", ex.getMessage());

    ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage());
    problem.setTitle("Access Denied");
    problem.setProperty("timestamp", Instant.now());
    return problem;
  }

  // --- 404 Not Found: resource does not exist ---

  @ExceptionHandler(ResourceNotFoundException.class)
  public ProblemDetail handleNotFound(ResourceNotFoundException ex) {
    log.warn("Resource not found: {}", ex.getMessage());

    ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    problem.setTitle("Resource Not Found");
    problem.setProperty("timestamp", Instant.now());
    return problem;
  }

  // --- 409 Conflict: duplicate resource / already a member ---

  @ExceptionHandler(DuplicateResourceException.class)
  public ProblemDetail handleDuplicate(DuplicateResourceException ex) {
    log.warn("Duplicate resource: {}", ex.getMessage());

    ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
    problem.setTitle("Duplicate Resource");
    problem.setProperty("timestamp", Instant.now());
    return problem;
  }

  @ExceptionHandler(AlreadyMemberException.class)
  public ProblemDetail handleAlreadyMember(AlreadyMemberException ex) {
    log.warn("Already a member: {}", ex.getMessage());

    ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
    problem.setTitle("Already a Group Member");
    problem.setProperty("timestamp", Instant.now());
    return problem;
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ProblemDetail handleIllegalArgument(IllegalArgumentException ex) {
    ProblemDetail problem =
        ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    problem.setTitle("Invalid Argument");
    problem.setProperty("timestamp", Instant.now());
    return problem;
  }

  // --- 500 Internal Server Error: catch-all for unexpected errors ---

  @ExceptionHandler(Exception.class)
  public ProblemDetail handleGeneralError(Exception ex) {
    log.error("Unhandled internal server error", ex);

    ProblemDetail problem =
        ProblemDetail.forStatusAndDetail(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "An unexpected error occurred. Please contact support.");
    problem.setTitle("Internal Server Error");
    problem.setProperty("timestamp", Instant.now());
    return problem;
  }
}
