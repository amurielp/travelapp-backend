-- V18: Soft delete + updated_at en entidades core para soporte de offline sync móvil
-- El cliente Flutter necesita detectar tanto borrados (deleted_at) como ediciones (updated_at)
-- desde su última sincronización. trips y events ya tienen updated_at; budget_items y
-- wishlist_items solo tenían created_at, así que se les añade updated_at aquí también.

ALTER TABLE trips          ADD COLUMN deleted_at TIMESTAMPTZ;
ALTER TABLE events         ADD COLUMN deleted_at TIMESTAMPTZ;
ALTER TABLE budget_items   ADD COLUMN deleted_at TIMESTAMPTZ;
ALTER TABLE budget_items   ADD COLUMN updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW();
ALTER TABLE wishlist_items ADD COLUMN deleted_at TIMESTAMPTZ;
ALTER TABLE wishlist_items ADD COLUMN updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW();

-- Índices para consultas de delta sync (?since=timestamp)
CREATE INDEX idx_trips_deleted_at          ON trips(deleted_at)          WHERE deleted_at IS NOT NULL;
CREATE INDEX idx_events_deleted_at         ON events(deleted_at)         WHERE deleted_at IS NOT NULL;
CREATE INDEX idx_budget_items_deleted_at   ON budget_items(deleted_at)   WHERE deleted_at IS NOT NULL;
CREATE INDEX idx_budget_items_updated_at   ON budget_items(updated_at);
CREATE INDEX idx_wishlist_items_deleted_at ON wishlist_items(deleted_at) WHERE deleted_at IS NOT NULL;
CREATE INDEX idx_wishlist_items_updated_at ON wishlist_items(updated_at);
