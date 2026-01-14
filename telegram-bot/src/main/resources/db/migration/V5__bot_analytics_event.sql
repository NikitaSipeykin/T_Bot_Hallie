CREATE TABLE IF NOT EXISTS bot_analytics_event (
    id BIGSERIAL PRIMARY KEY,

    -- Пользователь
    chat_id BIGINT REFERENCES subscribers(chat_id),

    -- Тип события
    event_type VARCHAR(50) NOT NULL,
    /*
        SUBSCRIBE
        UNSUBSCRIBE
        BLOCK_VIEW
        BUTTON_CLICK
        PAYMENT_START
        PAYMENT_SUCCESS
        PAYMENT_FAILED
        PERSONAL_DATA_SUBMIT
        EXIT
    */

    -- Контекст программы
    block_name VARCHAR(255),
    -- program_blocks.name

    button_text VARCHAR(255),

    -- Платежи
    payment_id BIGINT REFERENCES payments(id),
    payment_status VARCHAR(20),
    payment_error_code TEXT,

    -- Гибкое расширение
    metadata JSONB,

    -- Время события
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- основной для Grafana
CREATE INDEX IF NOT EXISTS idx_analytics_event_type_time
ON bot_analytics_event (event_type, created_at);

-- пользовательские сценарии
CREATE INDEX IF NOT EXISTS idx_analytics_chat_time
ON bot_analytics_event (chat_id, created_at);

-- анализ блоков
CREATE INDEX IF NOT EXISTS idx_analytics_block
ON bot_analytics_event (block_name);

-- платежи
CREATE INDEX IF NOT EXISTS idx_analytics_payment
ON bot_analytics_event (payment_id);

-- для JSONB (если понадобится)
CREATE INDEX IF NOT EXISTS idx_analytics_metadata
ON bot_analytics_event USING GIN (metadata);