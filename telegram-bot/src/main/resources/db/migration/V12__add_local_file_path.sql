ALTER TABLE transcription_jobs
    ADD COLUMN IF NOT EXISTS local_file_path VARCHAR(512);
