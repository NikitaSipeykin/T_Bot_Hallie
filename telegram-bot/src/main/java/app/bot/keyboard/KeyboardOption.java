package app.bot.keyboard;

public record KeyboardOption(
    String text,
    String callback,
    String url
) {

  public static KeyboardOption callback(String text, String callback) {
    return new KeyboardOption(text, callback, null);
  }

  public static KeyboardOption url(String text, String url) {
    return new KeyboardOption(text, null, url);
  }
}

