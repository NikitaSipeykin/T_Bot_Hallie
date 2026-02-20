INSERT INTO public.bot_texts (id,value,handler) VALUES
('SCHEDULER','Let''s move on to reviewing the delayed message.','StateCallbackHandler'),
('SCHEDULER_BUTTON','Let''s go!','StateCallbackHandler'),
('SCHEDULER_TEXT','The bot can write at any time, according to a schedule, or under certain conditions. For example, let''s imagine that a user is taking a course, and during the course, the bot will send materials in stages and notify them when the next part becomes available.',
'SchedulerCallbackHandler'),
('SCHEDULER_NEXT','I suggest moving on.','SchedulerCallbackHandler'),
('SCHEDULER_NEXT_BUTTON','Ok, let''s go next!','SchedulerCallbackHandler'),
('WEB_PANEL_TEXT','The web panel allows you to interact with the bot without getting into the code.

There you can:
• change texts
• change media files
• broadcast messages to users
• view analytics
• manage scripts','WebPanelCallbackHandler'),
('WEB_PANEL_TEXT_BUTTON','Who created me?','WebPanelCallbackHandler'),
('DEVELOPER_TEXT','I was made by a live backend developer.
Not a constructor.
Not a template.

A person who thinks about logic,
support, and results.','DeveloperCallbackHandler'),
('DEVELOPER_TEXT_BUTTON','I want more','DeveloperCallbackHandler'),
('NIK_THE_DEV_TEXT','If you need a bot:
• for a product
• for a business
• for a service

I know how to do it right.','NikTheDevCallbackHandler'),
('ORDER_BOT_BUTTON','🛠 Order a bot','NikTheDevCallbackHandler'),
('WRITE_TO_DEV_BUTTON','💬 Write to the developer','NikTheDevCallbackHandler'),
('GITHUB_BUTTON','📎 GitHub','NikTheDevCallbackHandler'),
('AI_BUTTON','Try out the smart bot''s functionality.','NikTheDevCallbackHandler'),
('CONTACTS_TEXT','Hello!
I am a Java developer who creates bots and backend services that truly solve business problems.

What I can do:
• Turnkey Telegram/WhatsApp bots
• Backend on Spring Boot
• Integrations with APIs, payments, CRM
• Process automation and server deployment

📩 Contact:
  telegram: @NikSipeykin
  e-mail: niksipeykin@gmail.com

Open to freelance and long-term projects.','ContactsCallbackHandler');

