-- Flyway Schema History Repair Script
-- This script repairs the checksum mismatches in the flyway_schema_history table

USE uba_account_db_dev;

-- Show current schema history before repair
SELECT version, description, checksum FROM flyway_schema_history ORDER BY version;

-- Repair the checksums by updating them with the new values
-- V1.0 migration
UPDATE flyway_schema_history
SET checksum = 421709965
WHERE version = '1.0' AND description = 'Initialize Account Schema';

-- V1.1 migration
UPDATE flyway_schema_history
SET checksum = -202539443
WHERE version = '1.1' AND description = 'Add Sample Data';

-- Show updated schema history after repair
SELECT version, description, checksum FROM flyway_schema_history ORDER BY version;

