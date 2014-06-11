package org.safehaus.subutai.impl.packagemanager;

import org.safehaus.subutai.api.agentmanager.AgentManager;
import org.safehaus.subutai.api.commandrunner.CommandRunner;
import org.safehaus.subutai.api.packagemanager.PackageManager;
import org.safehaus.subutai.api.packagemanager.storage.PackageInfoStorage;

public abstract class DebPackageManagerBase implements PackageManager {

    AgentManager agentManager;
    CommandRunner commandRunner;

    PackageInfoStorage storage;
    String location = "/opt/subutai";
    String filename = "packages";

    public AgentManager getAgentManager() {
        return agentManager;
    }

    public void setAgentManager(AgentManager agentManager) {
        this.agentManager = agentManager;
    }

    public CommandRunner getCommandRunner() {
        return commandRunner;
    }

    public void setCommandRunner(CommandRunner commandRunner) {
        this.commandRunner = commandRunner;
    }

    public PackageInfoStorage getStorage() {
        return storage;
    }

    public void setStorage(PackageInfoStorage storage) {
        this.storage = storage;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

}
