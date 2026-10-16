-- =========================================
-- V2: Create member and message tables
-- Social Activity Planner
-- =========================================

-- 3. GROUP_MEMBERS
CREATE TABLE group_members (
    group_id UUID NOT NULL,
    user_id UUID NOT NULL,

    role VARCHAR(20) NOT NULL DEFAULT 'MEMBER',
    joined_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_group_members
        PRIMARY KEY (group_id, user_id),

    CONSTRAINT fk_group_members_group
        FOREIGN KEY (group_id)
        REFERENCES groups(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_group_members_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT chk_group_members_role
        CHECK (role IN ('OWNER', 'ADMIN', 'MEMBER'))
);

-- 4. MESSAGES
CREATE TABLE messages (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    group_id UUID NOT NULL,
    sender_id UUID NOT NULL,
    content TEXT NOT NULL,

    sent_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_messages_group
        FOREIGN KEY (group_id)
        REFERENCES groups(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_messages_sender
        FOREIGN KEY (sender_id)
        REFERENCES users(id)
        ON DELETE RESTRICT,

    CONSTRAINT chk_messages_content_not_empty
        CHECK (length(trim(content)) > 0)
);

-- =========================================
-- INDEXES
-- =========================================

-- Find groups created by a user
CREATE INDEX idx_groups_created_by
    ON groups(created_by);

-- Find groups a user has joined
CREATE INDEX idx_group_members_user_id
    ON group_members(user_id);

-- Retrieve messages in a group by time
CREATE INDEX idx_messages_group_sent_at
    ON messages(group_id, sent_at DESC);

-- Find messages sent by a user
CREATE INDEX idx_messages_sender_id
    ON messages(sender_id);
