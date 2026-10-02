CREATE TABLE users (
    id uuid PRIMARY KEY DEFAULT UUID_GENERATE_V4()
);

CREATE INDEX idx_users_id ON users (id);