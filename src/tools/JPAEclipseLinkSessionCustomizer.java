package tools;

/**
 * Created by tloehr on 13.02.14.
 */

import org.eclipse.persistence.config.SessionCustomizer;
import org.eclipse.persistence.logging.JavaLog;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.sessions.Session;


public class JPAEclipseLinkSessionCustomizer implements SessionCustomizer {
    public void customize(Session aSession) throws Exception {

        // create a custom logger
        SessionLog aCustomLogger = new CustomEclipselinkAbstractSessionLog();
        aCustomLogger.setLevel(JavaLog.FINEST);
        aSession.setSessionLog(aCustomLogger);
    }
}