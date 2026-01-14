CREATE TABLE IF NOT EXISTS user_state_entity (
	chat_id BIGINT REFERENCES subscribers(chat_id),
	state varchar(255) NULL,
	CONSTRAINT user_state_entity_pkey PRIMARY KEY (chat_id)
);