package com.travelapp.web.controllers;

import com.travelapp.adapters.storage.S3StorageAdapter;
import com.travelapp.documents.domain.*;
import com.travelapp.documents.ports.DocumentRepository;
import com.travelapp.trips.usecases.ValidateTripAccessUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.*;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/trips/{tripId}/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentRepository        docRepo;
    private final S3StorageAdapter          storage;
    private final ValidateTripAccessUseCase validateAccess;
    private final WebClient                 aiServiceWebClient;  // llama al ai-service para parse

    @GetMapping
    public ResponseEntity<List<TravelDocument>> list(
            @PathVariable UUID tripId,
            @RequestParam(required = false) String type,
            @AuthenticationPrincipal Jwt jwt) {
        validateAccess.execute(tripId, userId(jwt));
        var docs = type != null
            ? docRepo.findByTripIdAndType(tripId, type)
            : docRepo.findByTripId(tripId);
        return ResponseEntity.ok(docs);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<TravelDocument> upload(
            @PathVariable UUID tripId,
            @RequestParam("file")                        MultipartFile file,
            @RequestParam("documentTypeId")              String documentTypeId,
            @RequestParam(value = "displayName",  required = false) String displayName,
            @RequestParam(value = "validFrom",    required = false) String validFrom,
            @RequestParam(value = "validUntil",   required = false) String validUntil,
            @RequestParam(value = "notes",        required = false) String notes,
            @AuthenticationPrincipal Jwt jwt) {

        var uid = userId(jwt);
        validateAccess.execute(tripId, uid);

        // 1. Subir archivo a S3
        var fileUrl = storage.upload(file, "trips/" + tripId + "/documents");

        // 2. Persistir metadatos
        var doc = TravelDocument.builder()
            .id(UUID.randomUUID())
            .tripId(tripId)
            .uploadedBy(uid)
            .documentTypeId(documentTypeId)
            .displayName(displayName != null ? displayName
                : file.getOriginalFilename().replaceAll("\\.[^.]+$", ""))
            .fileName(file.getOriginalFilename())
            .fileUrl(fileUrl)
            .fileSizeBytes(file.getSize())
            .fileType(file.getContentType())
            .parseStatus(requiresParsing(documentTypeId) ? ParseStatus.PENDING : ParseStatus.NOT_REQUIRED)
            .validFrom(validFrom  != null ? java.time.LocalDate.parse(validFrom)  : null)
            .validUntil(validUntil != null ? java.time.LocalDate.parse(validUntil) : null)
            .notes(notes)
            .uploadedAt(OffsetDateTime.now())
            .build();

        var saved = docRepo.save(doc);

        // 3. Si necesita parsing IA, encolar (llamada async al ai-service)
        if (saved.requiresAiParse()) {
            enqueueAiParse(saved);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @DeleteMapping("/{docId}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID tripId,
            @PathVariable UUID docId,
            @AuthenticationPrincipal Jwt jwt) {
        validateAccess.execute(tripId, userId(jwt));
        docRepo.deleteById(docId);
        return ResponseEntity.noContent().build();
    }

    // ── Helpers ───────────────────────────────────────────────

    private boolean requiresParsing(String typeId) {
        return Set.of("flight_ticket", "hotel_voucher", "car_rental",
                      "train_ticket", "bus_ticket").contains(typeId);
    }

    /**
     * Envía el documento al ai-service para extracción de datos.
     * Es async — la UI hace polling sobre parseStatus.
     */
    private void enqueueAiParse(TravelDocument doc) {
        try {
            aiServiceWebClient.post()
                .uri("/parse/enqueue")
                .bodyValue(Map.of(
                    "document_id", doc.getId().toString(),
                    "file_url",    doc.getFileUrl(),
                    "file_type",   doc.getFileType(),
                    "document_type", doc.getDocumentTypeId()
                ))
                .retrieve()
                .bodyToMono(String.class)
                .subscribe(
                    resp -> log.info("parse.enqueued docId={}", doc.getId()),
                    err  -> log.error("parse.enqueue.failed docId={} error={}", doc.getId(), err.getMessage())
                );
        } catch (Exception e) {
            log.error("parse.enqueue.error docId={}", doc.getId(), e);
            // No propagar — el documento ya está guardado, el parse fallará y se reintentará
        }
    }

    private UUID userId(Jwt jwt) { return UUID.fromString(jwt.getSubject()); }
}
