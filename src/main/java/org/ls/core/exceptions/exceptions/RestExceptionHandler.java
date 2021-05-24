package org.ls.core.exceptions.exceptions;


import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.constraints.NotNull;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
@Slf4j
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    public static ResponseEntity<Object> buildResponseEntity(ApiError apiError) {
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    /**
     * Handle MissingServletRequestParameterException. Triggered when a 'required' request parameter is missing.
     *
     * @param ex      MissingServletRequestParameterException
     * @param headers HttpHeaders
     * @param status  HttpStatus
     * @param request WebRequest
     * @return the ApiError object
     */
    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            @NotNull MissingServletRequestParameterException ex, @NotNull HttpHeaders headers,
            @NotNull HttpStatus status, @NotNull WebRequest request) {
        String error = ex.getParameterName() + " parameter is missing";
        return buildResponseEntity(new ApiError(BAD_REQUEST, error, ex));
    }

    /**
     * Handle HttpMediaTypeNotSupportedException. This one triggers when JSON is invalid as well.
     *
     * @param ex      HttpMediaTypeNotSupportedException
     * @param headers HttpHeaders
     * @param status  HttpStatus
     * @param request WebRequest
     * @return the ApiError object
     */
    @NotNull
    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(
            HttpMediaTypeNotSupportedException ex,
            @NotNull HttpHeaders headers,
            @NotNull HttpStatus status,
            @NotNull WebRequest request) {
        StringBuilder builder = new StringBuilder();
        builder.append(ex.getContentType());
        builder.append(" media type is not supported. Supported media types are ");
        ex.getSupportedMediaTypes().forEach(t -> builder.append(t).append(", "));
        return buildResponseEntity(new ApiError(HttpStatus.UNSUPPORTED_MEDIA_TYPE, builder.substring(0, builder.length() - 2), ex));
    }

    /**
     * Handle MethodArgumentNotValidException. Triggered when an object fails @Valid validation.
     *
     * @param ex      the MethodArgumentNotValidException that is thrown when @Valid validation fails
     * @param headers HttpHeaders
     * @param status  HttpStatus
     * @param request WebRequest
     * @return the ApiError object
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {
        ApiError apiError = new ApiError(BAD_REQUEST);
        apiError.setMessage("Объект не прошел валидацию");
        apiError.addValidationErrors(ex.getBindingResult().getFieldErrors());
        apiError.addValidationError(ex.getBindingResult().getGlobalErrors());
        return buildResponseEntity(apiError);
    }

    /**
     * Падает когда не проходит валидацию. javax.validation.constraints например @Positive
     *
     * @param ex RuntimeException
     * @return ResponseEntity<ApiError>
     */
    @ExceptionHandler(TransactionSystemException.class)
    protected ResponseEntity<Object> transactionErr(RuntimeException ex) {

        ApiError apiError;

        if (((TransactionSystemException) ex).getRootCause() == null) {
            log.error(ex.getMessage());
            apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR);
            apiError.setMessage("Внутренняя ошибка сервиса.");
        } else {
            if (((TransactionSystemException) ex).getMostSpecificCause() instanceof javax.validation.ConstraintViolationException) {
                log.error(Objects.requireNonNull(((TransactionSystemException) ex).getRootCause()).getMessage());
            }
            apiError = new ApiError(BAD_REQUEST);
            apiError.setMessage("Объект не прошел валидацию");
        }

        return buildResponseEntity(apiError);
    }

    /**
     * Handle DataIntegrityViolationException, inspects the cause for different DB causes.
     *
     * @param ex the DataIntegrityViolationException
     * @return the ApiError object
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    protected ResponseEntity<Object> handleDataIntegrityViolation(DataIntegrityViolationException ex,
                                                                  WebRequest request) {
        if (ex.getCause() instanceof ConstraintViolationException) {
            final String regex = "^ERROR: duplicate key value violates unique constraint\\s\\S+\\s+Detail: Key.+ already exists.$";

            final Pattern pattern = Pattern.compile(regex);

            String readableMessage = ((ConstraintViolationException) ex.getCause()).getSQLException().getMessage(); //юзеру показывмем только понятное сообщение. Сейчас вырезается до первого ':', возможно в других базах нужно по-другому...
            readableMessage = readableMessage.replaceAll("[\r\n]+", "");
            final Matcher matcher = pattern.matcher(readableMessage);
            boolean matches = matcher.matches();
            if (matches) {
                //ERROR: duplicate key value violates unique constraint "uklrfk6tnem13kmbggxnfeymftl"
                //Detail: Key (project_id, value)=(1, коробка) already exists.

                readableMessage = readableMessage.replaceAll("^ERROR: duplicate key value violates unique constraint\\s\\S+\\s", "Найдены дублирующиеся значения.");
                readableMessage = readableMessage.replaceAll("Detail: Key", "\n");
                readableMessage = readableMessage.replaceAll(" already exists.$", " уже существует.");
                readableMessage = readableMessage.replaceAll("(\\(.*\\)=)", "");

                ApiError error = new ApiError(HttpStatus.CONFLICT);
                error.setMessage("Ошибка базы данных:");
                error.setDebugMessage(readableMessage);
                return buildResponseEntity(error);
            } else
                return buildResponseEntity(new ApiError(HttpStatus.CONFLICT, "Ошибка базы данных: " + readableMessage, ex.getCause()));
        }
        return buildResponseEntity(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, ex));
    }

    /**
     * Handles EntityNotFoundException. Created to encapsulate errors with more detail than javax.persistence.EntityNotFoundException.
     *
     * @param ex the EntityNotFoundException
     * @return the ApiError object
     */

    @ExceptionHandler(EntityNotFoundException.class)
    protected ResponseEntity<Object> handleEntitySubErrorsException(EntityNotFoundException ex) {
        ApiError apiError = new ApiError(NOT_FOUND);
        apiError.setMessage(ex.getMessage());
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(EntitySubErrorsException.class)
    protected ResponseEntity<Object> handleEntitySubErrorsException(EntitySubErrorsException ex) {
        ApiError apiError = new ApiError(BAD_REQUEST);
        apiError.setMessage(ex.getMessage());
        apiError.addValidationErrors(ex.getErrors());
        return buildResponseEntity(apiError);
    }


    @ExceptionHandler(RuntimeException.class)
    protected ResponseEntity<Object> enternallErr(RuntimeException ex) {
        ApiError apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR);
        apiError.setMessage("Внутренняя ошибка сервиса.");
        apiError.setDebugMessage(ex.getMessage());
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    protected ResponseEntity<Object> onOptimisticLockingException(ObjectOptimisticLockingFailureException ex) {
        ApiError apiError = new ApiError(BAD_REQUEST);
        apiError.setMessage("Объект был изменен.");
        apiError.setDebugMessage("Обновите страницу и повторите действие.");
        return buildResponseEntity(apiError);
    }


    @ExceptionHandler(StatusChangeException.class)
    protected ResponseEntity<Object> onStatusChangeException(StatusChangeException ex) {
        ApiError apiError = new ApiError(BAD_REQUEST);
        apiError.setMessage("Ошибка при переводе статуса.");
        apiError.setDebugMessage(ex.getMessage());
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(CheckException.class)
    protected ResponseEntity<Object> onOperationCheckException(CheckException ex) {
        ApiError apiError = new ApiError(BAD_REQUEST);
        apiError.setMessage("Ошибка при попытке совершить действие.");
        apiError.setDebugMessage(ex.getMessage());
        return buildResponseEntity(apiError);
    }

//    @ExceptionHandler(FeignException.class)
//    public ResponseEntity<Object> handleFeignStatusException(FeignException ex, HttpServletResponse response) {
//        log.info(String.valueOf(response.getStatus()));
//        return buildResponseEntity(new ApiError(HttpStatus.CONFLICT));
//    }


    /**
     * Handle HttpMessageNotReadableException. Happens when request JSON is malformed.
     *
     * @param ex      HttpMessageNotReadableException
     * @param headers HttpHeaders
     * @param status  HttpStatus
     * @param request WebRequest
     * @return the ApiError object
     */
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ServletWebRequest servletWebRequest = (ServletWebRequest) request;
        log.info("{} to {}", servletWebRequest.getHttpMethod(), servletWebRequest.getRequest().getServletPath());
        String error = "Malformed JSON request";
        return buildResponseEntity(new ApiError(BAD_REQUEST, error, ex));
    }

    /**
     * Handle HttpMessageNotWritableException.
     *
     * @param ex      HttpMessageNotWritableException
     * @param headers HttpHeaders
     * @param status  HttpStatus
     * @param request WebRequest
     * @return the ApiError object
     */
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotWritable(HttpMessageNotWritableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        String error = "Error writing JSON output";
        return buildResponseEntity(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, error, ex));
    }

    /**
     * Handle NoHandlerFoundException.
     *
     * @param ex
     * @param headers
     * @param status
     * @param request
     * @return
     */
    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(
            NoHandlerFoundException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ApiError apiError = new ApiError(BAD_REQUEST);
        apiError.setMessage(String.format("Could not find the %s method for URL %s", ex.getHttpMethod(), ex.getRequestURL()));
        apiError.setDebugMessage(ex.getMessage());
        return buildResponseEntity(apiError);
    }

    /**
     * Handle Exception, handle generic Exception.class
     *
     * @param ex the Exception
     * @return the ApiError object
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<Object> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex,
                                                                      WebRequest request) {
        ApiError apiError = new ApiError(BAD_REQUEST);
        apiError.setMessage(String.format("The parameter '%s' of value '%s' could not be converted to type '%s'", ex.getName(), ex.getValue(), Objects.requireNonNull(ex.getRequiredType()).getSimpleName()));
        apiError.setDebugMessage(ex.getMessage());
        return buildResponseEntity(apiError);
    }

}