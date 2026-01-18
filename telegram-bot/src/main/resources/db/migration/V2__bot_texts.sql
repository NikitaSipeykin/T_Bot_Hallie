CREATE TABLE IF NOT exists bot_texts  (
  id VARCHAR(255) PRIMARY KEY,
  value TEXT NOT NULL,
  handler VARCHAR(255)
);

INSERT INTO public.bot_texts (id,value,handler) VALUES
	 ('START','%s!  I can do more than just reply to messages.
I work like a real digital service.

Want me to show you?',
     'StartCommandHandler'),
	 ('PAYMENT','PAYMENT','PAYMENT'),
	 ('NOT_EMAIL','It appears this is not an email. Please try again:','MailRequestMessageHandler'),
	 ('SEND_EMAIL','Email sent','MailRequestMessageHandler'),
	 ('NEED_PAYMENT','Looks like you haven''t finished paying yet!','NeedPaymentMessageHandler'),
	 ('BROADCAST_FAIL','An error occurred during the broadcasting process.','BroadcastCommandHandler'),
     ('UNSUBSCRIBE','You have successfully unsubscribed.','UnsubscribeCommandHandler'),
     ('BROADCAST_SUCCESS','The broadcast has been sent!','BroadcastCommandHandler'),
     ('MENU_DEFAULT','not ready yet','MenuCommandHandler'),
	 ('ERROR','Something went wrong!','DefaultMessageHandler');

INSERT INTO public.bot_texts (id,value,handler) VALUES
	 ('PROJECT_DESCRIPTION_BUTTON_YES','Yes, sign up!','IntroPaymentCallbackHandler'),
	 ('PROJECT_DESCRIPTION_BUTTON_INFO','Tell me more.','IntroPaymentCallbackHandler'),
	 ('PAYMENT_CHOOSE_CURRENCY','Select the currency for payment.','PaymentCallbackHandler'),
	 ('PAYMENT_ERROR','Payment is currently unavailable. Try calling up the menu.','PaymentCallbackHandler'),
     ('SEND_INVOICE_TITLE','Access to the program','SendInvoicePaymentCallbackHandler'),
     ('SEND_INVOICE_DESCRIPTION','Full access to the program','SendInvoicePaymentCallbackHandler'),
     ('SEND_INVOICE_LABEL','Access','SendInvoicePaymentCallbackHandler'),
     ('CURRENCY_CHOOSE_ANOTHER','Select another currency!','SendInvoicePaymentCallbackHandler'),
     ('CURRENCY_ERROR','Payment in this currency is temporarily unavailable.','SendInvoicePaymentCallbackHandler'),
	 ('CURRENCY_CHOOSE_BUTTON_BACK','Back','SendInvoicePaymentCallbackHandler');

INSERT INTO public.bot_texts (id,value,handler) VALUES
	 ('ADMIN_QUESTION_USER_WARN','The following message will be sent directly to the administrator!','AdminQuestionCommandHandler'),
	 ('START_COMMAND_USERNAME_FILLER','friend','StartCommandHandler'),
	 ('STEP_BACK','Would you like to return to the product description?','StepBackMessageHandler'),
	 ('STEP_BACK_BUTTON_YES','Yes','StepBackMessageHandler'),
     ('PAYMENT_PROJECT_INTRO','✅ Payment successful! Welcome to the program','SuccessPaymentMessageHandler'),
     ('PAYMENT_PROJECT_INTRO_BUTTON','Begin','SuccessPaymentMessageHandler'),
     ('MAIL_ERROR','Incorrect code! Please try again.','WaitEmailStateHandler'),
     ('MAIL_SUCCESS','Great! Here''s your gift 🎁','WaitEmailStateHandler');

INSERT INTO public.bot_texts (id,value,handler) VALUES
	 ('BOT_ABILITY_BUTTON','See what I can do','StartCommandHandler'),
	 ('DEMO_MODE_BUTTON','Turn on demo mode','StartCommandHandler'),
	 ('WHO_IS_HALLIE_BUTTON','Who are you, Hallie?','StartCommandHandler'),
	 ('HALLIE_INTRODUCTION','I help you:
• communicate
• make decisions
• stay on track
• and get things done

I don''t just talk.
I *do*.'
    ,'WhoIsHallieCallbackHandler'),
     ('HALLIE_INTRODUCTION_BUTTON_YES','Yes, let''s go','WhoIsHallieCallbackHandler'),
     ('HALLIE_INTRODUCTION_BUTTON_BACK','Back','WhoIsHallieCallbackHandler'),
     ('HALLIE_INTRODUCTION_WANT_TO_SEE','Want to see how it looks in practice?',''),
     ('BOT_ABILITY_INTRODUCTION','I''ll show you everything step by step.
No overload.

Let''s start with something simple.','BotAbilityCallbackHandler'),
     ('BOT_ABILITY_ASK','Tell me, why are you here?','BotAbilityCallbackHandler'),
     ('BOT_ABILITY_BUTTON_NEED_BOT','I need a bot','BotAbilityCallbackHandler');

INSERT INTO public.bot_texts (id,value,handler) VALUES
	 ('BOT_ABILITY_BUTTON_CURIOUS','Just curious','BotAbilityCallbackHandler'),
	 ('BOT_ABILITY_BUTTON_DEV','I''m a developer','BotAbilityCallbackHandler'),
	 ('BEFORE_DEMO_NEED_BOT','I will guide you just as I guide real clients:
step by step.','BeforeDemoCallbackHandler'),
	 ('BEFORE_DEMO_CURIOUS','You will see how the system works from the inside.','BeforeDemoCallbackHandler'),
     ('BEFORE_DEMO_DEV','Sometimes I''ll give you a hint about what''s going on under the hood 😉','BeforeDemoCallbackHandler'),
     ('BEFORE_DEMO_BEGIN','Wanna start?','BeforeDemoCallbackHandler'),
     ('BEFORE_DEMO_BEGIN_BUTTON','Let''s go','BeforeDemoCallbackHandler'),
     ('DEMO_MEDIA','Video, voice, text — I use the format that works best in the moment.','DemoStartCallbackHandler'),
     ('DEMO_MOMENT','Next — an important point.','DemoStartCallbackHandler'),
     ('DEMO_ACCESS','In the demo:
• you can see everything
• but not everything is available

Just like in the real product.','DemoStartCallbackHandler');

INSERT INTO public.bot_texts (id,value,handler) VALUES
	 ('DEMO_ACCESS_BUTTON_UNLOCK','Unlock full access','DemoStartCallbackHandler'),
	 ('DEMO_ACCESS_BUTTON_CONTINUE','Continue to demo','DemoStartCallbackHandler'),
	 ('ACCESS_PAYMENT','The payment went through. I have updated your access.','DemoAccessCallbackHandler'),
	 ('ACCESS_NEXT','Don''t worry, it wasn''t a real payment. Now I can guide you further—without restrictions.','DemoAccessCallbackHandler'),
     ('ACCESS_BUTTON_NEXT','Let''s go next','DemoAccessCallbackHandler'),
     ('EMAIL','I can send you a letter:
• with what you have already seen
• and how it can be applied','EmailCallbackHandler'),
     ('SEND_EMAIL_BUTTON','Yes, send it','EmailCallbackHandler'),
     ('NO_EMAIL_BUTTON','Not now','EmailCallbackHandler'),
     ('ASK_EMAIL','Just write your email address','EmailAskCallbackHandler'),
     ('NO_ACCESS','We''re continuing with the demo.Just keep in mind — some steps will be closed.','DemoNoAccessCallbackHandler');

INSERT INTO public.bot_texts (id,value,handler) VALUES
	 ('EMAIL_WAS_SENT_BUTTON','Next','MailRequestMessageHandler'),
     ('GOAL_SUMMARY','• goal: %s!  
• access: %s!  
• step: status check
','StateCallbackHandler'),
     ('HALLIE_USE_IT','I use this to:
• avoid repeating myself
• avoid getting in the way
• and help at the right time
','StateCallbackHandler');
