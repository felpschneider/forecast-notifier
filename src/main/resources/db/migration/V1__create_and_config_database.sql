CREATE TABLE users
(
    id            BIGSERIAL PRIMARY KEY,
    name          VARCHAR(255) NOT NULL,
    email         VARCHAR(255) NULL UNIQUE,
    phone_number  VARCHAR(20)  NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    opt_in        BOOLEAN      NOT NULL DEFAULT TRUE,
    active        BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE cities
(
    id_cptec   BIGINT PRIMARY KEY,
    name       VARCHAR(255) NOT NULL,
    state_code VARCHAR(2)   NOT NULL,
    is_coastal BOOLEAN      NULL,
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE subscriptions
(
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT       NOT NULL,
    city_id         BIGINT       NOT NULL,
    cron_expression VARCHAR(100) NOT NULL,
    active          BOOLEAN      NOT NULL DEFAULT TRUE,
    last_sent_at    TIMESTAMP,
    created_at      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_users FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_cities FOREIGN KEY (city_id) REFERENCES cities (id_cptec),
    CONSTRAINT uq_user_city UNIQUE (user_id, city_id)
);

CREATE TABLE sessions
(
    id         VARCHAR(36) PRIMARY KEY, -- UUID
    user_id    BIGINT    NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_session_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE notification_channels
(
    id           BIGSERIAL PRIMARY KEY,
    user_id      BIGINT    NOT NULL,
    web_opt_in   BOOLEAN   NOT NULL,
    email_opt_in BOOLEAN   NOT NULL,
    sms_opt_in   BOOLEAN   NOT NULL,
    push_opt_in  BOOLEAN   NOT NULL,
    created_at   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notification_channels_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT uq_notification_channels_user UNIQUE (user_id)
);

-- Adicionando índices
CREATE INDEX idx_users_email ON users (email);
CREATE INDEX idx_users_phone ON users (phone_number);
CREATE INDEX idx_cities_name ON cities (name);
CREATE INDEX idx_subscriptions_user ON subscriptions (user_id);
CREATE INDEX idx_subscriptions_city ON subscriptions (city_id);
CREATE INDEX idx_subscriptions_active ON subscriptions (active);
CREATE INDEX idx_subscriptions_last_sent ON subscriptions (last_sent_at);
CREATE INDEX idx_sessions_user_id ON sessions (user_id);
CREATE INDEX idx_sessions_expires_at ON sessions (expires_at);
CREATE INDEX idx_notification_channels_user ON notification_channels (user_id);

