# Diagramas de Secuencia — TravelApp Backend

Cada fichero cubre una funcionalidad del backend (`travel-api`). Los diagramas están en formato Mermaid.

| Fichero | Funcionalidad |
|---|---|
| [01-auth.md](01-auth.md) | Autenticación OAuth2 con Keycloak |
| [02-trips.md](02-trips.md) | CRUD de viajes |
| [03-create-event.md](03-create-event.md) | Crear evento (validación solapamiento + geocodificación + auto-gasto) |
| [04-update-event.md](04-update-event.md) | Actualizar evento (geocodificación condicional + sync de gasto) |
| [05-expenses.md](05-expenses.md) | Gestión de gastos (CRUD + resumen + timeline + categoría) |
| [06-payment-reminders.md](06-payment-reminders.md) | Scheduler de recordatorios de pago (FCM) |
| [07-esim-insurance-alerts.md](07-esim-insurance-alerts.md) | Alertas de caducidad eSIM/seguro y deadline de cancelación |
| [08-device-tokens.md](08-device-tokens.md) | Registro y revocación de tokens FCM |
| [09-gaps.md](09-gaps.md) | Detección y resolución de gaps en el itinerario |
| [10-ai-suggestions.md](10-ai-suggestions.md) | Sugerencias AI (solicitar, aceptar, descartar) |
| [11-user-profile.md](11-user-profile.md) | Perfil, preferencias, consentimiento GDPR y feature flags |
| [12-payment-methods.md](12-payment-methods.md) | Métodos de pago e informe de uso |
| [13-wishlist.md](13-wishlist.md) | Wishlist por viaje |
| [14-documents.md](14-documents.md) | Document Vault (subida, descarga presigned URL, eliminación) |
| [15-export-pdf.md](15-export-pdf.md) | Export PDF asíncrono (job + polling) |
| [16-subscriptions.md](16-subscriptions.md) | Suscripciones RevenueCat (verificación + webhook renovación) |
| [17-share-invites.md](17-share-invites.md) | Compartir viaje e invitaciones de colaboradores |
| [18-sync-delta.md](18-sync-delta.md) | Sync delta incremental para clientes móviles |
| [19-geocoding.md](19-geocoding.md) | Geocodificación interna (IATA lookup + Nominatim + caché Redis) |
