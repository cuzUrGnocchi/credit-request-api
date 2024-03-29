package me.dio.credit.request.system.exception

import jakarta.persistence.EntityNotFoundException
import org.springframework.dao.DataAccessException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.time.LocalDateTime

@RestControllerAdvice
class RestExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValidException(exception: MethodArgumentNotValidException): ResponseEntity<ExceptionDetails> {
        val errors: HashMap<String, String?> = HashMap()

        exception.bindingResult.allErrors.forEach { error ->
            val title: String = (error as FieldError).field
            val message: String? = error.defaultMessage
            errors[title] = message
        }

        return ResponseEntity(
            ExceptionDetails(
                title = exception.javaClass.toString(),
                timestamp = LocalDateTime.now(),
                status = HttpStatus.BAD_REQUEST.value(),
                exception = exception.javaClass.toString(),
                details = errors
            ), HttpStatus.BAD_REQUEST
        )
    }

    @ExceptionHandler(DataAccessException::class)
    fun handleDataAccessException(exception: DataAccessException): ResponseEntity<ExceptionDetails> {
        return ResponseEntity(
            ExceptionDetails(
                title = exception.javaClass.toString(),
                timestamp = LocalDateTime.now(),
                status = HttpStatus.CONFLICT.value(),
                exception = exception.javaClass.toString(),
                details = mapOf(exception.cause.toString() to exception.message)
            ), HttpStatus.CONFLICT
        )
    }

    @ExceptionHandler(EntityNotFoundException::class)
    fun handleEntityNotFoundException(exception: EntityNotFoundException): ResponseEntity<ExceptionDetails> {
        return ResponseEntity(
            ExceptionDetails(
                title = exception.javaClass.toString(),
                timestamp = LocalDateTime.now(),
                status = HttpStatus.BAD_REQUEST.value(),
                exception = exception.javaClass.toString(),
                details = mapOf(exception.cause.toString() to exception.message)
            ), HttpStatus.BAD_REQUEST
        )
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(exception: IllegalArgumentException): ResponseEntity<ExceptionDetails> {
        return ResponseEntity(
            ExceptionDetails(
                title = exception.javaClass.toString(),
                timestamp = LocalDateTime.now(),
                status = HttpStatus.BAD_REQUEST.value(),
                exception = exception.javaClass.toString(),
                details = mapOf(exception.cause.toString() to exception.message)
            ), HttpStatus.BAD_REQUEST
        )
    }
}