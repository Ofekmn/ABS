package abs.utils;

import engine.EngineImpl;
import jakarta.servlet.ServletContext;

public class ServletUtils {
    private static final String USER_MANAGER_ATTRIBUTE_NAME = "engine";

    /*
    Note how the synchronization is done only on the question and\or creation of the relevant managers and once they exists -
    the actual fetch of them is remained un-synchronized for performance POV
     */
    private static final Object userManagerLock = new Object();

    public static EngineImpl getEngineManager(ServletContext servletContext) {

        synchronized (userManagerLock) {
            if (servletContext.getAttribute(USER_MANAGER_ATTRIBUTE_NAME) == null) {
                servletContext.setAttribute(USER_MANAGER_ATTRIBUTE_NAME, new EngineImpl());
            }
        }
        return (EngineImpl) servletContext.getAttribute(USER_MANAGER_ATTRIBUTE_NAME);
    }
}
