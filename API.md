# TravelApp REST API

Base URL: `/api/v1`
Auth: Bearer JWT (Keycloak)

## Endpoints

### Auth (Keycloak maneja el flujo — estos son informativos)
POST   /realms/travelapp/protocol/openid-connect/token   → obtener token
POST   /realms/travelapp/protocol/openid-connect/logout  → cerrar sesión

### Users
GET    /users/me                   → perfil del usuario autenticado
PUT    /users/me                   → actualizar perfil
PATCH  /users/me/preferences       → actualizar preferencias
DELETE /users/me                   → eliminar cuenta

### Trips
GET    /trips                      → mis viajes (propios + compartidos)
POST   /trips                      → crear viaje
GET    /trips/{id}                 → detalle del viaje
PATCH  /trips/{id}                 → actualizar viaje
DELETE /trips/{id}                 → eliminar viaje
POST   /trips/{id}/publish         → publicar itinerario (URL pública)
GET    /trips/public/{slug}        → itinerario público (sin auth)

### Trip Members
GET    /trips/{id}/members         → miembros del viaje
POST   /trips/{id}/members/invite  → invitar colaborador
DELETE /trips/{id}/members/{userId}→ eliminar miembro

### Events (calendario)
GET    /trips/{id}/events          → todos los eventos del viaje
POST   /trips/{id}/events          → crear evento (manual)
GET    /trips/{id}/events/{eid}    → detalle del evento
PATCH  /trips/{id}/events/{eid}    → actualizar evento
DELETE /trips/{id}/events/{eid}    → eliminar evento
GET    /trips/{id}/events/day/{date} → eventos de un día (YYYY-MM-DD)
GET    /trips/{id}/days            → resumen de días (ocupancy, free_slots)

### Documents (PDFs)
POST   /trips/{id}/documents       → subir PDF (multipart/form-data)
GET    /trips/{id}/documents       → listar documentos
GET    /trips/{id}/documents/{did} → estado del parsing
POST   /trips/{id}/documents/{did}/accept → confirmar eventos parseados
DELETE /trips/{id}/documents/{did} → eliminar documento

### Budget
GET    /trips/{id}/budget          → presupuesto completo con resumen
POST   /trips/{id}/budget/items    → añadir gasto
PATCH  /trips/{id}/budget/items/{iid} → actualizar gasto
DELETE /trips/{id}/budget/items/{iid} → eliminar gasto
GET    /trips/{id}/budget/summary  → resumen por categoría

### Wishlist
GET    /trips/{id}/wishlist        → lista de deseos del viaje
POST   /trips/{id}/wishlist        → añadir ítem
PATCH  /trips/{id}/wishlist/{wid}  → actualizar ítem
DELETE /trips/{id}/wishlist/{wid}  → eliminar ítem
POST   /trips/{id}/wishlist/{wid}/schedule → convertir a evento

### Suggestions (IA / Places API según feature flag)
GET    /trips/{id}/suggestions?date=YYYY-MM-DD → sugerencias para un día
POST   /trips/{id}/suggestions/{sid}/accept    → aceptar sugerencia
POST   /trips/{id}/suggestions/{sid}/dismiss   → descartar sugerencia

### Places (catálogo cacheado)
GET    /places/search?city=&category=&q=       → buscar lugares
GET    /places/{placeId}                       → detalle de un lugar

### Feature Flags (admin)
GET    /admin/flags                → listar flags
PATCH  /admin/flags/{key}          → activar/desactivar flag

## Códigos de respuesta
200 OK           → operación exitosa
201 Created      → recurso creado
204 No Content   → eliminación exitosa
400 Bad Request  → validación fallida (body con errores por campo)
401 Unauthorized → token ausente o inválido
403 Forbidden    → sin permisos sobre el recurso
404 Not Found    → recurso no existe
409 Conflict     → conflicto (slug duplicado, etc.)
500 Server Error → error interno

## Paginación
GET /trips?page=0&size=20&sort=startDate,desc
Respuesta: { content: [...], totalElements, totalPages, number }

## Filtros de eventos
GET /trips/{id}/events?type=FLIGHT&from=2025-07-01&to=2025-07-31&status=CONFIRMED
