CREATE TABLE IF NOT exists media_settings (
    key_name VARCHAR(255) PRIMARY KEY,
    file_name VARCHAR(255)
);

INSERT INTO public.media_settings (key_name,file_name) VALUES
	 ('AUDIO_HALLIE_START',''),
	 ('AUDIO_HALLIE_WHO_AM_I',''),
	 ('AUDIO_HALLIE_BOT_ABILITY',''),
	 ('AUDIO_BEFORE_DEMO_NEED_BOT',''),
	 ('AUDIO_BEFORE_DEMO_CURIOUS',''),
	 ('AUDIO_BEFORE_DEMO_DEV',''),
	 ('AUDIO_EMAIL_AUDIO',''),
	 ('AUDIO_DEMO_AUDIO',''),
	 ('AUDIO_ACCESS_AUDIO',''),
	 ('AUDIO_STATE_AUDIO','');

INSERT INTO public.media_settings (key_name,file_name) VALUES
     ('AUDIO_HALLIE_USE_IT',''),
	 ('AUDIO_ASK_EMAIL_AUDIO','');

INSERT INTO public.media_settings (key_name,file_name) VALUES
	 ('VIDEO_ACCESS_VIDEO',''),
	 ('VIDEO_DEMO_CIRCLE','');