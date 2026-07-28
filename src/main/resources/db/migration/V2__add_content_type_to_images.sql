ALTER TABLE images
    ADD COLUMN content_type VARCHAR(50) NOT NULL DEFAULT 'image/jpeg';