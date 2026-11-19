---
config:
  layout: elk
---

erDiagram
    USERS {
        UUID id PK
        VARCHAR username "UNIQUE"
        VARCHAR email "UNIQUE"
        VARCHAR password_hash
        TIMESTAMP created_at
        TIMESTAMP updated_at
    }
    ACTIVITY_GROUP {
        UUID id PK
        VARCHAR name
        TEXT description
        UUID created_by FK
        UUID conversation_id FK "UNIQUE, NOT NULL"
        TIMESTAMP created_at
        TIMESTAMP updated_at
    }
    GROUP_MEMBER {
        UUID group_id PK, FK
        UUID user_id PK, FK
        VARCHAR role "owner | admin | member"
        TIMESTAMP joined_at
    }
    CONVERSATION {
        UUID id PK
        VARCHAR type "group | direct"
        UUID last_message_id FK "nullable, set by trigger"
        TIMESTAMP created_at
    }
    DIRECT_CONVERSATION {
        UUID conversation_id PK, FK
        UUID user_low FK "UNIQUE(user_low, user_high)"
        UUID user_high FK "CHECK user_low < user_high"
        VARCHAR status "pending | accepted"
        UUID requested_by FK
        TIMESTAMP created_at
    }
    MESSAGE {
        UUID id PK "UUIDv7"
        UUID conversation_id FK
        UUID sender_id FK "NULL = system"
        UUID batch_id "nullable, multi-send"
        VARCHAR type "text | system"
        TEXT content
        TIMESTAMP sent_at
        TIMESTAMP updated_at
        TIMESTAMP deleted_at "soft delete"
    }
    READ_STATE {
        UUID user_id PK, FK
        UUID conversation_id PK, FK
        UUID last_read_message_id FK
        TIMESTAMP updated_at
    }

    USERS ||--o{ ACTIVITY_GROUP : "creates"
    USERS ||--o{ GROUP_MEMBER : "joins"
    ACTIVITY_GROUP ||--o{ GROUP_MEMBER : "has"
    ACTIVITY_GROUP ||--|| CONVERSATION : "group chat"

    USERS ||--o{ DIRECT_CONVERSATION : "pairs"
    DIRECT_CONVERSATION ||--|| CONVERSATION : "direct chat"

    CONVERSATION ||--o{ MESSAGE : "contains"
    USERS ||--o{ MESSAGE : "sends"

    USERS ||--o{ READ_STATE : "tracks"
    CONVERSATION ||--o{ READ_STATE : "read by"
