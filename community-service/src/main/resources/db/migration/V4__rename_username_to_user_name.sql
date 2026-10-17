-- =========================================
-- V4: Rename users.username to user_name
-- Social Activity Planner
-- =========================================

ALTER TABLE users
    RENAME COLUMN username TO user_name;
