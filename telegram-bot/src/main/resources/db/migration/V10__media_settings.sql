INSERT INTO public.media_settings (key_name, file_name) VALUES
 ('AUDIO_SCHEDULER_INTRO',''),
 ('AUDIO_SCHEDULER_OUTRO',''),
 ('AUDIO_WEB_PANEL',''),
 ('AUDIO_WEB_PANEL_END',''),
 ('AUDIO_DEVELOPER_INTRO',''),
 ('AUDIO_DEVELOPER_END',''),
 ('AUDIO_FINAL','')
ON CONFLICT (key_name)
DO UPDATE SET file_name = EXCLUDED.file_name;
