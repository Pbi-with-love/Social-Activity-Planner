-- Social Activity Planner - chat + group schema (PostgreSQL)

CREATE SCHEMA IF NOT EXISTS social_activity;

-- UUIDv7: time-ordered UUID (PostgreSQL 18 has uuidv7() built in; this is for PG <= 17)
CREATE OR REPLACE FUNCTION social_activity.uuidv7() RETURNS uuid AS $$
  SELECT encode(
    set_bit(set_bit(
      overlay(uuid_send(gen_random_uuid())
              PLACING substring(int8send((extract(epoch FROM clock_timestamp()) * 1000)::bigint) FROM 3)
              FROM 1 FOR 6),
    52, 1), 53, 1), 'hex')::uuid;
$$ LANGUAGE sql VOLATILE;

CREATE TABLE social_activity.users (
  id            uuid PRIMARY KEY DEFAULT social_activity.uuidv7(),
  username      varchar(50)  NOT NULL UNIQUE,
  email         varchar(255) NOT NULL UNIQUE,
  password_hash varchar(255) NOT NULL,
  created_at    timestamptz  NOT NULL DEFAULT now(),
  updated_at    timestamptz  NOT NULL DEFAULT now()
);

-- Message container (shared by group chats and direct chats)
CREATE TABLE social_activity.conversation (
  id              uuid PRIMARY KEY DEFAULT social_activity.uuidv7(),
  type            varchar(10) NOT NULL CHECK (type IN ('group', 'direct')),
  last_message_id uuid,                     -- FK added after the message table exists
  created_at      timestamptz NOT NULL DEFAULT now()
);

-- Activity group (planning engine domain)
CREATE TABLE social_activity.activity_group (
  id              uuid PRIMARY KEY DEFAULT social_activity.uuidv7(),
  name            varchar(100) NOT NULL,
  description     text,
  created_by      uuid NOT NULL REFERENCES social_activity.users(id),
  conversation_id uuid NOT NULL UNIQUE REFERENCES social_activity.conversation(id),
  created_at      timestamptz NOT NULL DEFAULT now(),
  updated_at      timestamptz NOT NULL DEFAULT now()
);

-- Group membership is the single source of truth for group chat access
CREATE TABLE social_activity.group_member (
  group_id  uuid NOT NULL REFERENCES social_activity.activity_group(id) ON DELETE CASCADE,
  user_id   uuid NOT NULL REFERENCES social_activity.users(id) ON DELETE CASCADE,
  role      varchar(10) NOT NULL DEFAULT 'member' CHECK (role IN ('owner', 'admin', 'member')),
  joined_at timestamptz NOT NULL DEFAULT now(),
  PRIMARY KEY (group_id, user_id)
);
CREATE INDEX ON social_activity.group_member (user_id);

-- Direct chat: the user pair is the single source of truth for direct chat access
CREATE TABLE social_activity.direct_conversation (
  conversation_id uuid PRIMARY KEY REFERENCES social_activity.conversation(id) ON DELETE CASCADE,
  user_low        uuid NOT NULL REFERENCES social_activity.users(id),
  user_high       uuid NOT NULL REFERENCES social_activity.users(id),
  status          varchar(10) NOT NULL DEFAULT 'pending' CHECK (status IN ('pending', 'accepted')),
  requested_by    uuid NOT NULL REFERENCES social_activity.users(id),
  created_at      timestamptz NOT NULL DEFAULT now(),
  CHECK (user_low < user_high),
  CHECK (requested_by IN (user_low, user_high)),
  UNIQUE (user_low, user_high)
);
CREATE INDEX ON social_activity.direct_conversation (user_high);   -- user_low is already indexed by the UNIQUE constraint

CREATE TABLE social_activity.message (
  id              uuid PRIMARY KEY DEFAULT social_activity.uuidv7(),
  conversation_id uuid NOT NULL REFERENCES social_activity.conversation(id) ON DELETE CASCADE,
  sender_id       uuid REFERENCES social_activity.users(id),        -- NULL = system message
  batch_id        uuid,                                             -- shared by messages sent together
  type            varchar(10) NOT NULL DEFAULT 'text' CHECK (type IN ('text', 'system')),
  content         text NOT NULL,
  sent_at         timestamptz NOT NULL DEFAULT now(),
  updated_at      timestamptz,
  deleted_at      timestamptz,
  CHECK ((type = 'system') = (sender_id IS NULL))
);
CREATE INDEX ON social_activity.message (conversation_id, id DESC);
CREATE INDEX ON social_activity.message (batch_id) WHERE batch_id IS NOT NULL;

ALTER TABLE social_activity.conversation
  ADD FOREIGN KEY (last_message_id) REFERENCES social_activity.message(id) ON DELETE SET NULL;

-- The DB keeps last_message_id up to date on each new message, so the app does not have to
-- Schema-qualified because the trigger runs with the session search_path, which may not include social_activity
CREATE FUNCTION social_activity.set_last_message() RETURNS trigger AS $$
BEGIN
  UPDATE social_activity.conversation SET last_message_id = NEW.id
  WHERE id = NEW.conversation_id
    AND (last_message_id IS NULL OR last_message_id < NEW.id);
  RETURN NULL;
END $$ LANGUAGE plpgsql;

CREATE TRIGGER message_set_last AFTER INSERT ON social_activity.message
FOR EACH ROW EXECUTE FUNCTION social_activity.set_last_message();

-- Read state only; it does NOT grant access
CREATE TABLE social_activity.read_state (
  user_id              uuid NOT NULL REFERENCES social_activity.users(id) ON DELETE CASCADE,
  conversation_id      uuid NOT NULL REFERENCES social_activity.conversation(id) ON DELETE CASCADE,
  last_read_message_id uuid REFERENCES social_activity.message(id) ON DELETE SET NULL,
  updated_at           timestamptz NOT NULL DEFAULT now(),
  PRIMARY KEY (user_id, conversation_id)
);
