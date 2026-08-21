-- Origin coordinates for FLIGHT and TRANSPORT events (to draw the route arc on the map)
ALTER TABLE events ADD COLUMN IF NOT EXISTS origin_latitude  DOUBLE PRECISION;
ALTER TABLE events ADD COLUMN IF NOT EXISTS origin_longitude DOUBLE PRECISION;
