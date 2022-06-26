package util;

import com.google.gson.Gson;

public class Constants {
    // global constants
    public final static String LINE_SEPARATOR = System.getProperty("line.separator");
    public final static String JHON_DOE = "<Anonymous>";
    public final static int REFRESH_RATE = 2000;
    public final static String CHAT_LINE_FORMATTING = "%tH:%tM:%tS | %.10s: %s%n";

    // fxml locations
    public final static String MAIN_PAGE_FXML_RESOURCE_LOCATION = "/chat/client/component/main/chat-app-main.fxml";
    public final static String LOGIN_PAGE_FXML_RESOURCE_LOCATION = "/chat/client/component/login/login.fxml";
    public final static String CHAT_ROOM_FXML_RESOURCE_LOCATION = "/chat/client/component/chatroom/chat-room-main.fxml";

    // Server resources locations
    public final static String BASE_DOMAIN = "localhost";
    private final static String BASE_URL = "http://" + BASE_DOMAIN + ":8080";
    private final static String CONTEXT_PATH = "/ABS";
    private final static String FULL_SERVER_PATH = BASE_URL + CONTEXT_PATH;

    public final static String LOGIN_PAGE = FULL_SERVER_PATH + "/loginShortResponse";
    public final static String UPLOAD_FILE_PAGE = FULL_SERVER_PATH + "/upload-file";
    public final static String CHARGE_PAGE = FULL_SERVER_PATH + "/charge";
    public final static String WITHDRAW_PAGE = FULL_SERVER_PATH + "/withdraw";
    public final static String NEW_LOAN_PAGE = FULL_SERVER_PATH + "/createLoan";
    public final static String FILTER_PAGE= FULL_SERVER_PATH +"/filter";
    public final static String SCRAMBLE_PAGE= FULL_SERVER_PATH+ "/scramble";
    // GSON instance
    public final static Gson GSON_INSTANCE = new Gson();
}
