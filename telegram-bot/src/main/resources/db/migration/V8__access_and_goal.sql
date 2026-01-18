CREATE TABLE IF NOT exists access_and_goal  (
  id BIGSERIAL PRIMARY KEY,
  chat_id BIGINT NOT NULL REFERENCES subscribers(chat_id),
  goal TEXT NOT NULL DEFAULT '',
  access BOOLEAN DEFAULT FALSE
);