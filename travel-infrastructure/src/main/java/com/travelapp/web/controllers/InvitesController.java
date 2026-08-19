package com.travelapp.web.controllers;

import com.travelapp.web.dto.response.TripInviteResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// PENDIENTE: funcionalidad de viajes compartidos no confirmada en scope del MVP móvil.
// Requiere decisión sobre multi-viajero y migración V23 (tabla trip_members).
// GET /me/invites devuelve lista vacía; POST /invites/{token}/accept devuelve 404.
@RestController
@RequestMapping("/api/v1")
public class InvitesController {

    @GetMapping("/me/invites")
    public ResponseEntity<List<TripInviteResponse>> listMyInvites() {
        return ResponseEntity.ok(List.of());
    }

    @PostMapping("/invites/{token}/accept")
    public ResponseEntity<Void> acceptInvite(@PathVariable String token) {
        return ResponseEntity.notFound().build();
    }
}
