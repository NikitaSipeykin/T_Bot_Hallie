CREATE TABLE transcription_jobs (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id          BIGINT NOT NULL,
    chat_id          BIGINT NOT NULL,
    telegram_file_id VARCHAR(255),
    file_name        VARCHAR(255),
    file_size        BIGINT,
    duration_seconds INTEGER,
    status           VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    error_message    TEXT,
    retry_count      INTEGER DEFAULT 0,
    created_at       TIMESTAMP NOT NULL DEFAULT NOW(),
    completed_at     TIMESTAMP
);

CREATE TABLE transcription_results (
    id                 UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    job_id             UUID NOT NULL REFERENCES transcription_jobs(id),
    full_text          TEXT,
    summary            TEXT,
    word_count         INTEGER,
    processing_time_ms BIGINT,
    created_at         TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_trans_jobs_user_id ON transcription_jobs(user_id);
CREATE INDEX idx_trans_jobs_status  ON transcription_jobs(status);
CREATE INDEX idx_trans_results_job  ON transcription_results(job_id);
