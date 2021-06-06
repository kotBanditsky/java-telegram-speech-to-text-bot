public class Config {
    public static final String TELEGRAM_BOT_TOKEN = new PrivateProperties().getProperty("TELEGRAM_BOT_TOKEN");
    public static final String TELEGRAM_BOT_USERNAME = new PrivateProperties().getProperty("TELEGRAM_BOT_USERNAME");
    public static final String SAVE_MSG = new PrivateProperties().getProperty("SAVE_MSG");
    public static final String DELETE_MSG = new PrivateProperties().getProperty("DELETE_MSG");
    public static final String MONGO_CONNECT = new PrivateProperties().getProperty("MONGO_CONNECT");
}