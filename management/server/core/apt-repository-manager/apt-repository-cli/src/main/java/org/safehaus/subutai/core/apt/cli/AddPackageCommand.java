package org.safehaus.subutai.core.apt.cli;


import org.safehaus.subutai.common.settings.Common;
import org.safehaus.subutai.core.agent.api.AgentManager;
import org.safehaus.subutai.core.apt.api.AptRepoException;
import org.safehaus.subutai.core.apt.api.AptRepositoryManager;

import org.apache.karaf.shell.commands.Argument;
import org.apache.karaf.shell.commands.Command;
import org.apache.karaf.shell.console.OsgiCommandSupport;


@Command( scope = "apt", name = "add-package", description = "Add package to apt repository by path" )
public class AddPackageCommand extends OsgiCommandSupport
{
    @Argument( index = 0, name = "package path", required = true, multiValued = false,
            description = "path to package" )
    String packagePath;

    private AptRepositoryManager aptRepositoryManager;
    private AgentManager agentManager;


    public void setAptRepositoryManager( final AptRepositoryManager aptRepositoryManager )
    {
        this.aptRepositoryManager = aptRepositoryManager;
    }


    public void setAgentManager( final AgentManager agentManager )
    {
        this.agentManager = agentManager;
    }


    @Override
    protected Object doExecute()
    {

        try
        {
            aptRepositoryManager
                    .addPackageByPath( agentManager.getAgentByHostname( Common.MANAGEMENT_AGENT_HOSTNAME ), packagePath,
                            false );
        }
        catch ( AptRepoException e )
        {
            System.out.println( e );
        }
        return null;
    }
}
