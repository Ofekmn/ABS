package util;

import com.google.gson.Gson;

public class Constants {

    public final static int REFRESH_RATE = 500;

    // Server resources locations
    public final static String BASE_DOMAIN = "localhost";
    private final static String BASE_URL = "http://" + BASE_DOMAIN + ":8080";
    private final static String CONTEXT_PATH = "/ABS";
    private final static String FULL_SERVER_PATH = BASE_URL + CONTEXT_PATH;
    public final static String ADMIN_UPDATE_PAGE= FULL_SERVER_PATH +"/adminUpdate";
    public final static String LOGIN_PAGE = FULL_SERVER_PATH + "/loginShortResponse";
    public final static String INCREASE_YAZ_PAGE = FULL_SERVER_PATH + "/increaseYaz";
    public final static String REWIND_PAGE = FULL_SERVER_PATH + "/rewind";

    // GSON instance
    public final static Gson GSON_INSTANCE = new Gson();
}

