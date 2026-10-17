-- =========================================
-- V3: Add first and last name to users
-- Social Activity Planner
-- =========================================

ALTER TABLE users
    ADD COLUMN first_name VARCHAR(50) NOT NULL DEFAULT '',
    ADD COLUMN last_name  VARCHAR(50) NOT NULL DEFAULT '';
