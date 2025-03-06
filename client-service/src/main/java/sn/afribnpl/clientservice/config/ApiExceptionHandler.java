package sn.afribnpl.clientservice.config;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import sn.afribnpl.clientservice.exceptions.DuplicateEmailException;

import java.util.Date;
import java.util.stream.Collectors;

@ControllerAdvice
public class ApiExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

    private ApiResponseException createApiResponse(String message, HttpStatus status) {
        ApiResponseException apiResponseException = new ApiResponseException();
        apiResponseException.setMessage(message);
        apiResponseException.setTimestamp(new Date());
        apiResponseException.setStatus(status);
        return apiResponseException;
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseException> handleException(Exception ex) {
        log.error("Erreur serveur : {}", ex.getMessage(), ex);
        return new ResponseEntity<>(createApiResponse(
                " Une erreur s'est produite côté serveur, veuillez contacter votre administrateur.",
                HttpStatus.INTERNAL_SERVER_ERROR),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponseException> handleNotFoundException(NoResourceFoundException ex) {
        log.error("Ressource non trouvée : {}", ex.getMessage(), ex);
        return new ResponseEntity<>(createApiResponse(ex.getMessage(), HttpStatus.NOT_FOUND), HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponseException> handleNotFoundException(EntityNotFoundException ex) {
        log.error("Entity non trouvée : {}", ex.getMessage(), ex);
        return new ResponseEntity<>(createApiResponse(ex.getMessage(), HttpStatus.NOT_FOUND), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<ApiResponseException> handleNotFoundException(DuplicateKeyException ex) {
        log.error("Entity non trouvée : {}", ex.getMessage(), ex);
        return new ResponseEntity<>(createApiResponse(ex.getMessage(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponseException> handleViolationException(DataIntegrityViolationException ex) {
        log.error("Violation d'intégrité : {}", ex.getMessage(), ex);
        String message = "Contrainte de base de données violée. Veuillez vérifier vos données.";
        if (ex.getCause() != null) {
            message += " Cause: " + ex.getCause().getMessage();
        }
        return new ResponseEntity<>(createApiResponse(message, HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseException> handleValidationException(MethodArgumentNotValidException ex) {
        log.error("Validation échouée : {}", ex.getMessage(), ex);
        String errors = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(err -> err.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return new ResponseEntity<>(createApiResponse("Validation échouée: " + errors, HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<ApiResponseException> handleSecurityException(SecurityException ex) {
        log.error("Erreur de sécurité : {}", ex.getMessage(), ex);
        return new ResponseEntity<>(createApiResponse(ex.getMessage(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ApiResponseException> handleDuplicateEmailException(SecurityException ex) {
        log.error("Duplication d'email : {}", ex.getMessage(), ex);
        return new ResponseEntity<>(createApiResponse(ex.getMessage(), HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
    }


}
