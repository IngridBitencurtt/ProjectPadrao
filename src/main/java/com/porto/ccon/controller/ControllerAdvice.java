package com.porto.ccon.controller;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.porto.ccon.exception.RestException;
import com.porto.ccon.model.dto.response.ErrorResponse;

import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class ControllerAdvice {

    private static final Integer JVM_MAX_STRING_LEN = 2147483647;
    private final MessageSource messageSource;


    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Void> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<List<ErrorResponse>> methodArgumentTypeMismatchException(final MethodArgumentTypeMismatchException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.singletonList(ErrorResponse.builder()
                .code("INS-0002")
                .message(getMessage("INS-0002", e.getName()))
                .build()));
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<Error> mediaTypeNotFoundException(final HttpMediaTypeNotSupportedException e) {
        return new ResponseEntity<>(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<List<ErrorResponse>> assertionException(final HttpMessageNotReadableException e) {
        if (e.getCause()instanceof JsonMappingException cause) {
            String field = cause.getPath().stream()
                    .map(JsonMappingException.Reference::getFieldName)
                    .filter(StringUtils::isNotBlank)
                    .collect(Collectors.joining("."));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonList(ErrorResponse.builder()
                            .code("INS-0002")
                            .message(getMessage("INS-0002", field))
                            .build()));
        }
        return defaultBadRequestError();
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<List<ErrorResponse>> missingServletRequestParameterException(
            final MissingServletRequestParameterException e) {
        return defaultBadRequestError();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<ErrorResponse>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException methodArgumentNotValidException) {
        List<ErrorResponse> messageErrors = Optional.ofNullable(methodArgumentNotValidException)
                .filter(argumentNotValidException -> !ObjectUtils.isEmpty(argumentNotValidException.getBindingResult()))
                .map(MethodArgumentNotValidException::getBindingResult)
                .filter(bindingResult -> !ObjectUtils.isEmpty(bindingResult.getAllErrors()))
                .map(BindingResult::getAllErrors)
                .map(Stream::of)
                .orElseGet(Stream::empty)
                .flatMap(Collection::stream)
                .filter(objectError -> !ObjectUtils.isEmpty(objectError))
                .map(this::createError)
                .collect(Collectors.toList());
        return new ResponseEntity<>(messageErrors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RestException.class)
    public ResponseEntity<Object> handleRestException(RestException restException) {
        log.error(restException.getMessage(), restException);
        if (restException.getResponseBodyCode() != null) {
            return ResponseEntity.status(restException.getStatus())
                    .body(ErrorResponse.builder()
                            .code(restException.getResponseBodyCode())
                            .message(getMessage(restException.getResponseBodyCode()))
                            .build());
        }
        if (restException.getResponseBody() != null) {
            return ResponseEntity.status(restException.getStatus())
                    .body(ErrorResponse.builder()
                            .code(restException.getResponseBody().getCode())
                            .message(getMessage(restException.getResponseBody().getCode()))
                            .build());
        }
        return ResponseEntity.status(restException.getStatus()).build();
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<List<ErrorResponse>> handleConstraintViolationException(
            ConstraintViolationException e) {
        List<ErrorResponse> errors = e.getConstraintViolations().stream()
                .map(constraint -> new ErrorResponse(constraint.getMessageTemplate(),
                        ((PathImpl) constraint.getPropertyPath()).getLeafNode()))
                .collect(Collectors.toList());
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleException(Exception exception) {
        log.error(exception.getMessage(), exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    private ResponseEntity<List<ErrorResponse>> defaultBadRequestError() {
        return new ResponseEntity<>(
                Collections.singletonList(ErrorResponse.builder()
                        .code("INS-0000")
                        .message(getMessage("INS-0000"))
                        .build()),
                HttpStatus.BAD_REQUEST);
    }

    private ErrorResponse createError(ObjectError error) {
        String field = "";
        if (error instanceof FieldError) {
            field = ((FieldError) error).getField();
        }

        if (error.getCode().equals("Size")) {
            Integer min = null;
            Integer max = null;
            if (error.getArguments().length > 2) {
                Integer rawMax = (Integer) error.getArguments()[1];
                max = rawMax == JVM_MAX_STRING_LEN ? null : rawMax;

                Integer rawMin = (Integer) error.getArguments()[2];
                min = rawMin == null ? null : 0;
            }

            if (min != null && max != null) {
                return new ErrorResponse(error.getDefaultMessage(), field, min, max);
            } else if (min != null) {
                return new ErrorResponse(error.getDefaultMessage(), field, min);
            } else if (max != null) {
                return new ErrorResponse(error.getDefaultMessage(), field, max);
            }
        }

        if (error.getCode().equals("DecimalMax")) {
            String max = error.getArguments()[2].toString();
            return new ErrorResponse(error.getDefaultMessage(), field, max);
        }

        if (error.getCode().equals("DecimalMin")) {
            String min = error.getArguments()[2].toString();
            return new ErrorResponse(error.getDefaultMessage(), field, min);
        }

        return ErrorResponse.builder()
                .code(error.getDefaultMessage())
                .message(getMessage(error.getDefaultMessage(), field))
                .build();
    }

    private String getMessage(String code, Object... args) {
        return this.messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
    }
}