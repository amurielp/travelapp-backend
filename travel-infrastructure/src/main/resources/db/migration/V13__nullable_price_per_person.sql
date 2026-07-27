-- price_per_person es un flag opcional; eliminar la restricción NOT NULL
ALTER TABLE flights
    ALTER COLUMN price_per_person DROP NOT NULL;
