package org.safehaus.subutai.plugin.hive.impl.handler;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.safehaus.subutai.common.exception.ClusterException;
import org.safehaus.subutai.common.exception.ClusterSetupException;
import org.safehaus.subutai.common.exception.CommandException;
import org.safehaus.subutai.common.protocol.AbstractOperationHandler;
import org.safehaus.subutai.common.protocol.ClusterSetupStrategy;
import org.safehaus.subutai.common.protocol.CommandResult;
import org.safehaus.subutai.common.protocol.EnvironmentBlueprint;
import org.safehaus.subutai.common.protocol.RequestBuilder;
import org.safehaus.subutai.core.environment.api.exception.EnvironmentBuildException;
import org.safehaus.subutai.core.environment.api.exception.EnvironmentDestroyException;
import org.safehaus.subutai.core.environment.api.helper.Environment;
import org.safehaus.subutai.core.peer.api.ContainerHost;
import org.safehaus.subutai.plugin.common.api.ClusterOperationHandlerInterface;
import org.safehaus.subutai.plugin.common.api.ClusterOperationType;
import org.safehaus.subutai.plugin.hadoop.api.Hadoop;
import org.safehaus.subutai.plugin.hadoop.api.HadoopClusterConfig;
import org.safehaus.subutai.plugin.hive.api.HiveConfig;
import org.safehaus.subutai.plugin.hive.api.SetupType;
import org.safehaus.subutai.plugin.hive.impl.Commands;
import org.safehaus.subutai.plugin.hive.impl.HiveImpl;
//import org.safehaus.subutai.plugin.hive.impl.
import org.safehaus.subutai.plugin.hive.impl.SetupStrategyOverHadoop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;


/**
 * This class handles operations that are related to whole cluster.
 */
public class ClusterOperationHandler extends AbstractOperationHandler<HiveImpl, HiveConfig>
        implements ClusterOperationHandlerInterface
{
    private static final Logger LOG = LoggerFactory.getLogger( ClusterOperationHandler.class.getName() );
    private ClusterOperationType operationType;
    private HiveConfig config;
    private HadoopClusterConfig hadoopConfig;

    private ExecutorService executor = Executors.newCachedThreadPool();


    public ClusterOperationHandler( final HiveImpl manager, final HiveConfig config,
                                    final HadoopClusterConfig hadoopConfig, final ClusterOperationType operationType )
    {
        super( manager, config  );
        this.operationType = operationType;
        this.config = config;
        this.hadoopConfig = hadoopConfig;
        trackerOperation = manager.getTracker().createTrackerOperation( HiveConfig.PRODUCT_KEY,
                String.format( "Creating %s tracker object...", clusterName ) );
    }


    public void run()
    {
        Preconditions.checkNotNull( config, "Configuration is null !!!" );
        switch ( operationType )
        {
            case INSTALL:
                executor.execute( new Runnable()
                {
                    public void run()
                    {
                        setupCluster();
                    }
                } );
                break;
            case UNINSTALL:
                executor.execute( new Runnable()
                {
                    public void run()
                    {
                        destroyCluster();
                    }
                } );
                break;
            case START_ALL:
            case STOP_ALL:
            case STATUS_ALL:
                runOperationOnContainers( operationType );
                break;
        }
    }


    @Override
    public void runOperationOnContainers( ClusterOperationType clusterOperationType )
    {
        Environment environment = manager.getEnvironmentManager().getEnvironmentByUUID( config.getEnvironmentId() );
        CommandResult result = null;
        switch ( clusterOperationType )
        {
            case START_ALL:
                for ( ContainerHost containerHost : environment.getContainers() )
                {
                    result = executeCommand( containerHost, Commands.startCommand );
                }
                break;
            case STOP_ALL:
                for ( ContainerHost containerHost : environment.getContainers() )
                {
                    result = executeCommand( containerHost, Commands.stopCommand );
                }
                break;
            case STATUS_ALL:
                for ( ContainerHost containerHost : environment.getContainers() )
                {
                    result = executeCommand( containerHost, Commands.statusCommand );
                }
                break;
        }
        NodeOperationHandler.logResults( trackerOperation, result );
    }


    private CommandResult executeCommand( ContainerHost containerHost, String command )
    {
        CommandResult result = null;
        try
        {
            result = containerHost.execute( new RequestBuilder( command ) );
        }
        catch ( CommandException e )
        {
            LOG.error( "Could not execute command correctly. ", command );
            e.printStackTrace();
        }
        return result;
    }


    @Override
    public void setupCluster()
    {

        Environment environment = manager.getEnvironmentManager().getEnvironmentByUUID( hadoopConfig.getEnvironmentId() );
        SetupStrategyOverHadoop setupStrategyOverHadoop = new SetupStrategyOverHadoop( environment, manager, config, hadoopConfig, trackerOperation );
        try
        {
            setupStrategyOverHadoop.setup();
        }
        catch ( ClusterSetupException e )
        {
            e.printStackTrace();
        }

        //        if ( Strings.isNullOrEmpty( config.getClusterName() ) )
        //        {
        //            trackerOperation.addLogFailed( "Malformed configuration" );
        //            return;
        //        }
        //
        //        if ( manager.getCluster( clusterName ) != null )
        //        {
        //            trackerOperation.addLogFailed( String.format( "Cluster with name '%s' already exists", clusterName ) );
        //            return;
        //        }
        //
        //        try
        //        {
        //            Environment env = manager.getEnvironmentManager()
        //                                     .buildEnvironment( manager.getDefaultEnvironmentBlueprint( config ) );
        //
        //            ClusterSetupStrategy clusterSetupStrategy =
        //                    manager.getClusterSetupStrategy( env, config, trackerOperation );
        //            clusterSetupStrategy.setup();
        //
        //            trackerOperation.addLogDone( String.format( "Cluster %s set up successfully", clusterName ) );
        //        }
        //        catch ( EnvironmentBuildException | ClusterSetupException e )
        //        {
        //            trackerOperation.addLogFailed(
        //                    String.format( "Failed to setup Elasticsearch cluster %s : %s", clusterName, e.getMessage() ) );
        //        }
    }


    @Override
    public void destroyCluster()
    {
        HiveConfig config = manager.getCluster( clusterName );
        if ( config == null )
        {
            trackerOperation.addLogFailed(
                    String.format( "Cluster with name %s does not exist. Operation aborted", clusterName ) );
            return;
        }

        try
        {
            trackerOperation.addLog( "Destroying environment..." );
            manager.getEnvironmentManager().destroyEnvironment( config.getEnvironmentId() );
            manager.getPluginDAO().deleteInfo( HiveConfig.PRODUCT_KEY, config.getClusterName() );
            trackerOperation.addLogDone( "Cluster destroyed" );
        }
        catch ( EnvironmentDestroyException e )
        {
            trackerOperation.addLogFailed( String.format( "Error running command, %s", e.getMessage() ) );
            LOG.error( e.getMessage(), e );
        }
    }
}
