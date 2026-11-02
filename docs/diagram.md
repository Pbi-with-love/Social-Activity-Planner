erDiagram
    USER {
        UUID id PK
        VARCHAR username
        VARCHAR email
        VARCHAR password_hash
        TIMESTAMP created_at
        TIMESTAMP updated_at
    }

    GROUP {
        UUID id PK
        VARCHAR name
        USER created_by FK
        TEXT description 
        TIMESTAMP created_at
        TIMESTAMP updated_at
    }

    GROUP_MEMBER {
        UUID group_id PK, FK
        UUID user_id PK, FK
        VARCHAR role
        TIMESTAMP joined_at
    }

    MESSAGE {
        UUID id PK
        UUID group_id FK
        UUID sender_id FK
        TEXT content
        TIMESTAMP sent_at
        TIMESTAMP updated_at
    }

    USER ||--o{ GROUP_MEMBER : "joins"
    GROUP ||--o{ GROUP_MEMBER : "has"

    USER ||--o{ GROUP : "creates"

    GROUP ||--o{ MESSAGE : "contains"
    USER ||--o{ MESSAGE : "sends"
