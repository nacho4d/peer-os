package io.subutai.core.environment.impl.workflow.creation.steps;


import java.util.Set;

import com.google.common.collect.Sets;

import io.subutai.common.environment.Topology;
import io.subutai.common.peer.Peer;
import io.subutai.common.protocol.N2NConfig;
import io.subutai.core.environment.impl.entity.EnvironmentImpl;


/**
 * N2N setup step
 */
public class N2NSetupStep
{

    private final Topology topology;
    private final EnvironmentImpl environment;


    public N2NSetupStep( final Topology topology, final EnvironmentImpl environment )
    {
        this.topology = topology;
        this.environment = environment;
    }


    public Set<N2NConfig> execute()
    {

        //obtain already participating peers
        Set<Peer> peers = Sets.newHashSet( topology.getAllPeers() );

        //todo setup tunnels, create edge
        return null;
    }
}
