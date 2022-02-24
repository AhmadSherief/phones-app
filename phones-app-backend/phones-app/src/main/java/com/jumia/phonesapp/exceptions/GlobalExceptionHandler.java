package com.jumia.phonesapp.exceptions;

import java.sql.SQLException;

import javax.persistence.EntityNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import javax.validation.ConstraintViolation;
//import javax.validation.ConstraintViolationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

//import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {

	Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	// Global Exception handler
	@ExceptionHandler(Exception.class)
	public ResponseEntity<?> handleGlobalException(Exception exception, WebRequest request) {
		log.error("An exception has occured: [" + exception.getMessage() + "]", exception);
		GeneralException generalException = new GeneralException(exception.getMessage());
		return new ResponseEntity<>(generalException, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	// Exception handler for incorrect method arguments
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<?> handleValidationError(MethodArgumentNotValidException exception, WebRequest request) {
		String detailedError = new String();
		for (int i = 0; i < exception.getAllErrors().size(); i++) {
			if (i == 0) {
				detailedError += exception.getAllErrors().get(i).getDefaultMessage();

			} else {
				detailedError += ", " + exception.getAllErrors().get(i).getDefaultMessage();
			}
		}
		log.error("MethodArgumentNotValidException: " + detailedError);
		GeneralException generalException = new GeneralException(detailedError);
		return new ResponseEntity<>(generalException, HttpStatus.BAD_REQUEST);
	}

	// Exception handler for incorrect method arguments
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<?> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException exception) {
		String detailedError = "Value of query parameter '" + exception.getParameter().getParameterName() + "' is incorrect";

		log.error("MethodArgumentNotValidException: " + detailedError);
		GeneralException generalException = new GeneralException(detailedError);
		return new ResponseEntity<>(generalException, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(EntityNotFoundException.class)
	@ResponseStatus(code = HttpStatus.NOT_FOUND)
	public GeneralException handleNotFoundException(EntityNotFoundException entityNotFoundException) {
		log.error("EntityNotFoundException: " + entityNotFoundException.getMessage());
		return new GeneralException(entityNotFoundException.getMessage());

	}

	@ExceptionHandler(ApiException.class)
	@ResponseStatus(code = HttpStatus.BAD_REQUEST)
	public GeneralException handleApiException(ApiException apiException) {
		log.error("[ApiException: " + apiException.getMessage());
		log.error("[ApiException: " + apiException.getLocalizedMessage());
		return new GeneralException(apiException.getMessage());
	}

	@ExceptionHandler(MissingServletRequestParameterException.class)
	@ResponseStatus(code = HttpStatus.BAD_REQUEST)
	public GeneralException handleMissingServletRequestParameterException(
			MissingServletRequestParameterException exception) {
		log.error("[Missing Query Parameter Exception] " + exception.getMessage());
		return new GeneralException("Missing required query parameter: " + exception.getParameterName());
	}

	@ExceptionHandler(SQLException.class)
	@ResponseStatus(code = HttpStatus.BAD_REQUEST)
	public GeneralException handleSQLException(SQLException exception) {
		log.error("[SQL Exception] " + exception.getMessage());
		return new GeneralException("Missing required query parameter: " + exception.getMessage());
	}

}
