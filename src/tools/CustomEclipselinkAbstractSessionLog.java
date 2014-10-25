package tools;

import Main.Main;
import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.JavaLog;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.logging.SessionLogEntry;

/**
 * Created by tloehr on 13.02.14.
 */

public class CustomEclipselinkAbstractSessionLog extends AbstractSessionLog implements SessionLog {
    /* @see org.eclipse.persistence.logging.AbstractSessionLog#log(org.eclipse.persistence.logging.SessionLogEntry)
     */
    @Override
    public void log(SessionLogEntry sessionLogEntry) {
        if (sessionLogEntry.getLevel() >= JavaLog.INFO || sessionLogEntry.hasException()) {
            Main.debug("[JPA] " + sessionLogEntry.getMessage());
        } else if (sessionLogEntry.getMessage().startsWith("INSERT") || sessionLogEntry.getMessage().startsWith("DELETE") || sessionLogEntry.getMessage().startsWith("UPDATE")) {
            Main.debug("[JPA] " + sessionLogEntry.getMessage());
        }
    }
}