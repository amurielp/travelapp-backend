[//]: # ( [MermaidChart: 990b2a37-5538-4a77-83e8-d42eb5bae226]
# Crear Evento (validación + geocodificación + auto-gasto)
[//]: # ( [MermaidChart: 990b2a37-5538-4a77-83e8-d42eb5bae226]
[//]: # ( [MermaidChart: 990b2a37-5538-4a77-83e8-d42eb5bae226]
[//]: # ( [MermaidChart: 990b2a37-5538-4a77-83e8-d42eb5bae226]
[//]: # ( [MermaidChart: 990b2a37-5538-4a77-83e8-d42eb5bae226]
```mermaid
[//]: # ( [MermaidChart: 990b2a37-5538-4a77-83e8-d42eb5bae226]
[//]: # ( [MermaidChart: 990b2a37-5538-4a77-83e8-d42eb5bae226]
[//]: # ( [MermaidChart: 990b2a37-5538-4a77-83e8-d42eb5bae226]
[//]: # ( [MermaidChart: 990b2a37-5538-4a77-83e8-d42eb5bae226]
[//]: # ( [MermaidChart: 990b2a37-5538-4a77-83e8-d42eb5bae226]
[//]: # ( [MermaidChart: 990b2a37-5538-4a77-83e8-d42eb5bae226]
sequenceDiagram
    participant C as Cliente
    participant CTRL as EventController
    participant CUC as CreateEventUseCase
[//]: # ( [MermaidChart: 990b2a37-5538-4a77-83e8-d42eb5bae226]
    participant GEO as EventGeocodingHelper
    participant PORT as GeocodingPort
    participant IATA as IATA static Map
    participant REDIS as Redis (TTL 30d)
    participant NOM as nominatim.openstreetmap.org
    participant REPO as EventRepository
    participant EXP as ExpenseRepository
    participant DB as PostgreSQL

    C->>CTRL: POST /v1/trips/{tripId}/events {type:FLIGHT, flight:{destIata:SIN,...}, ...}
    CTRL->>CUC: execute(CreateEventCommand)

    alt type == ACCOMMODATION
        CUC->>CUC: valida accommodation.name no vacío → DomainValidationException si falla
    end

    CUC->>REPO: findByTripIdAndDateRange(tripId, startDate, endDate)
    REPO->>DB: SELECT * FROM events WHERE trip_id=? AND dates overlap
    DB-->>REPO: List<TravelEvent>
    CUC->>CUC: filter overlapping → EventOverlapException si hay colisión

    CUC->>GEO: resolve(FLIGHT, null, null, ..., flight{destIata:SIN})
    GEO->>PORT: geocodeIata("SIN")
    PORT->>IATA: Map.get("SIN")
    IATA-->>PORT: [1.3644, 103.9915]
    PORT-->>GEO: Optional([1.3644, 103.9915])
    GEO-->>CUC: [1.3644, 103.9915]

    Note over GEO,NOM: Para ACCOMMODATION/ACTIVITY/DESTINATION via Nominatim
    GEO->>PORT: geocode("Hilton Singapore")
    PORT->>REDIS: @Cacheable check "geocoding::Hilton Singapore"
    alt Cache miss
        REDIS-->>PORT: null
        PORT->>NOM: GET /search?q=Hilton+Singapore&format=json&limit=1 (timeout 5s)
        NOM-->>PORT: [{lat, lon, display_name}]
        PORT->>REDIS: put result (TTL 30 dias)
    else Cache hit
        REDIS-->>PORT: [lat, lon]
    end
    PORT-->>GEO: Optional([lat, lon])

    Note over CUC,GEO: Si Nominatim falla → Optional.empty() → evento se guarda sin coordenadas

    CUC->>REPO: save(TravelEvent{lat, lon, status=CONFIRMED, source=MANUAL, ...})
    REPO->>DB: INSERT INTO events
    DB-->>REPO: TravelEvent

    alt extractPrice(event) != null AND != 0
        CUC->>EXP: save(Expense{isPaid:true, paidAt:now(), amount:price, category:TRANSPORT})
        EXP->>DB: INSERT INTO expenses
    end

    CTRL-->>C: 201 EventResponse
```
