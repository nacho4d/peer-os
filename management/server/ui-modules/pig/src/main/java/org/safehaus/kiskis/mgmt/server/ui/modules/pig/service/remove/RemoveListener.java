package org.safehaus.kiskis.mgmt.server.ui.modules.pig.service.remove;

import org.safehaus.kiskis.mgmt.server.ui.modules.pig.common.chain.Context;
import org.safehaus.kiskis.mgmt.server.ui.modules.pig.service.AbstractListener;
import org.safehaus.kiskis.mgmt.server.ui.modules.pig.service.UILogger;
import org.safehaus.kiskis.mgmt.shared.protocol.Response;

public class RemoveListener extends AbstractListener {

    public RemoveListener(UILogger log) {
        super(log, "Pig installed. Removing, please wait...");
    }

    @Override
    public boolean onResponse(Context context, String stdOut, String stdErr, Response response) {

        String msg = response.getExitCode() == null || response.getExitCode() == 0
                ? "Pig removed successfully"
                : "Error occurred while removing Pig. Please see the server logs for details.";

        LOG.info(msg);
        LOG.info("Completed");

        return false;
    }

}
