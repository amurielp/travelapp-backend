package com.travelapp.web.controllers;

import com.travelapp.shared.exceptions.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.http.*;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    record ErrorResponse(int status, String error, Object details, Instant timestamp) {}

    @ExceptionHandler(TripNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTripNotFound(TripNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse(404, ex.getMessage(), null, Instant.now()));
    }

    @ExceptionHandler(EventNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEventNotFound(EventNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse(404, ex.getMessage(), null, Instant.now()));
    }

    @ExceptionHandler(EventOverlapException.class)
    public ResponseEntity<ErrorResponse> handleOverlap(EventOverlapException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(new ErrorResponse(409, ex.getMessage(), null, Instant.now()));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse(404, ex.getMessage(), null, Instant.now()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleForbidden(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(new ErrorResponse(403, "Access denied", null, Instant.now()));
    }

    @ExceptionHandler(DomainValidationException.class)
    public ResponseEntity<ErrorResponse> handleDomainValidation(DomainValidationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse(400, ex.getMessage(), null, Instant.now()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("Illegal argument: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse(400, "Valor no válido: " + ex.getMessage(), null, Instant.now()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
            .collect(Collectors.toMap(
                FieldError::getField,
                f -> f.getDefaultMessage() != null ? f.getDefaultMessage() : "Invalid",
                (existing, duplicate) -> existing
            ));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse(400, "Validation failed", errors, Instant.now()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrity(DataIntegrityViolationException ex) {
        log.warn("Data integrity violation: {}", ex.getMostSpecificCause().getMessage());
        var msg = resolveConstraintMessage(ex.getMostSpecificCause().getMessage());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
            .body(new ErrorResponse(422, msg, null, Instant.now()));
    }

    @ExceptionHandler(TransactionSystemException.class)
    public ResponseEntity<ErrorResponse> handleTransaction(TransactionSystemException ex) {
        var cause = ex.getRootCause();
        var raw   = cause != null ? cause.getMessage() : ex.getMessage();
        log.warn("Transaction system exception: {}", raw);
        var msg = resolveConstraintMessage(raw);
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
            .body(new ErrorResponse(422, msg, null, Instant.now()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        log.error("Unhandled exception: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ErrorResponse(500, "Internal server error", ex.getMessage(), Instant.now()));
    }

    private String resolveConstraintMessage(String raw) {
        if (raw == null) return "Los datos proporcionados no son válidos.";

        // ── CHECK constraints ──────────────────────────────────────────────
        if (raw.contains("valid_dates"))
            return "La fecha de fin debe ser igual o posterior a la fecha de inicio.";
        if (raw.contains("payment_method_type_check"))
            return "Tipo de medio de pago no válido. Permitidos: card, transfer, cash, crypto, other.";
        if (raw.contains("gap_type_check"))
            return "Tipo de hueco en itinerario no válido.";
        if (raw.contains("severity_check"))
            return "Nivel de severidad no válido. Permitidos: ERROR, WARNING, INFO.";
        if (raw.contains("status_check"))
            return "Estado no válido. Permitidos: OPEN, RESOLVED, IGNORED, SNOOZED.";
        if (raw.contains("parse_status_check"))
            return "Estado de procesamiento de documento no válido.";

        // ── UNIQUE constraints ─────────────────────────────────────────────
        if (raw.contains("users_email_key") || raw.contains("users_email_unique")
                || (raw.contains("unique") && raw.contains("email")))
            return "El email ya está registrado.";
        if (raw.contains("users_keycloak_id"))
            return "El usuario ya existe en el sistema.";
        if (raw.contains("public_slug"))
            return "El slug público ya está en uso. Elige otro.";
        if (raw.contains("cached_places_google_place_id"))
            return "El lugar ya existe en caché.";
        if (raw.contains("duplicate key") || raw.contains("violates unique constraint"))
            return "Ya existe un registro con estos datos.";

        // ── FOREIGN KEY constraints ────────────────────────────────────────
        if (raw.contains("trips_user_id_fkey") || (raw.contains("foreign key") && raw.contains("user")))
            return "El usuario no existe.";
        if (raw.contains("events_trip_id_fkey") || (raw.contains("foreign key") && raw.contains("trip")))
            return "El viaje no existe.";
        if (raw.contains("budget_items_budget_id_fkey"))
            return "El presupuesto no existe.";
        if (raw.contains("violates foreign key constraint"))
            return "El recurso referenciado no existe.";

        // ── NOT NULL ───────────────────────────────────────────────────────
        if (raw.contains("not-null constraint") || raw.contains("violates not-null")
                || raw.contains("null value in column"))
            return "Faltan campos obligatorios.";

        // ── Genérico ───────────────────────────────────────────────────────
        if (raw.contains("violates check constraint"))
            return "Los datos no cumplen las reglas de validación.";

        return "Los datos proporcionados no son válidos.";
    }
}
