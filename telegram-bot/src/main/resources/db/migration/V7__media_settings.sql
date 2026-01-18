INSERT INTO public.media_settings (key_name, file_name) VALUES
 ('AUDIO_HALLIE_START','Hallie_start.mp3'),
 ('AUDIO_HALLIE_BOT_ABILITY','Hallie_bot_ability.mp3'),
 ('AUDIO_BEFORE_DEMO_DEV','Before_demo_dev.mp3'),
 ('AUDIO_ACCESS_AUDIO','Access_audio.mp3'),
 ('AUDIO_BEFORE_DEMO_CURIOUS','Before_demo_curious.mp3'),
 ('AUDIO_BEFORE_DEMO_NEED_BOT','Before_demo_need_bot.mp3'),
 ('AUDIO_DEMO_AUDIO','Demo_audio.mp3'),
 ('AUDIO_HALLIE_WHO_AM_I','Hallie_who_am_i_V2.mp3'),
 ('AUDIO_EMAIL_AUDIO','Email_audio_V2.mp3'),
 ('AUDIO_ASK_EMAIL_AUDIO','Ask_Email.mp3')
ON CONFLICT (key_name)
DO UPDATE SET file_name = EXCLUDED.file_name;
