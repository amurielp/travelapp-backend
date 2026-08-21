-- Fix expenses that were created with a null amount (legacy data from migration period)
UPDATE expenses SET amount = 0 WHERE amount IS NULL;

-- Ensure amount is never null going forward
ALTER TABLE expenses ALTER COLUMN amount SET NOT NULL;
ALTER TABLE expenses ALTER COLUMN amount SET DEFAULT 0;
