CREATE TABLE IF NOT EXISTS payments (
    id BIGSERIAL PRIMARY KEY,

    chat_id BIGINT NOT NULL REFERENCES subscribers(chat_id),

    payload TEXT NOT NULL UNIQUE,
    provider_payment_id TEXT,

    amount INTEGER NOT NULL,          -- в копейках
    currency VARCHAR(3) NOT NULL,

    status VARCHAR(20) NOT NULL,      -- CREATED / PAID / FAILED / REFUNDED

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    paid_at TIMESTAMP
);

ALTER TABLE payments
ADD CONSTRAINT paid_at_required_if_paid
CHECK (
    status <> 'PAID'
    OR paid_at IS NOT NULL
);

CREATE INDEX idx_payments_chat_paid
ON payments (chat_id, paid_at)
WHERE status = 'PAID';

ALTER TABLE payments
ADD CONSTRAINT uq_payments_id_chat
UNIQUE (id, chat_id);