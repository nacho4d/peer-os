package org.safehaus.subutai.core.peer.api;


import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.safehaus.subutai.common.protocol.Agent;
import org.safehaus.subutai.common.protocol.PeerCommandMessage;
import org.safehaus.subutai.core.container.api.ContainerCreateException;
import org.safehaus.subutai.core.peer.api.message.PeerMessageException;
import org.safehaus.subutai.core.peer.api.message.PeerMessageListener;


/**
 * Created by bahadyr on 8/28/14.
 */
public interface PeerManager
{

    boolean register( Peer peer );

    boolean update( Peer peer );

    public UUID getSiteId();

    public List<Peer> peers();

    boolean unregister( String uuid );

    public Peer getPeerByUUID( UUID uuid );

    //    public String getRemoteId( String baseUrl );

    public void addPeerMessageListener( PeerMessageListener listener );

    public void removePeerMessageListener( PeerMessageListener listener );

    public String sendPeerMessage( Peer peer, String recipient, String message ) throws PeerMessageException;

    public String processPeerMessage( String peerId, String recipient, String message ) throws PeerMessageException;

    public boolean isPeerReachable( Peer peer ) throws PeerException;

    public Set<Agent> getConnectedAgents( String environmentId ) throws PeerException;

    public Set<Agent> getConnectedAgents( Peer peer, String environmentId ) throws PeerException;

    public Set<Agent> createContainers( UUID envId, UUID peerId, String template, int numberOfNodes, String strategy )
            throws ContainerCreateException;

    @Deprecated
    public boolean startContainer( PeerContainer container );

    @Deprecated
    public boolean stopContainer( PeerContainer container );

    @Deprecated
    public boolean isContainerConnected( PeerContainer container );

    @Deprecated
    public Set<PeerContainer> getContainers();

    @Deprecated
    public void addContainer( PeerContainer peerContainer );

    @Deprecated
    public void invoke( PeerCommandMessage peerCommandMessage );

    List<PeerGroup> peersGroups();

    void deletePeerGroup( PeerGroup group );

    boolean savePeerGroup( PeerGroup group );

    public PeerInterface getPeer( UUID peerId );


    //    public Set<ContainerHost> createContainers( UUID envId, String templateName, int quantity, String strategyId,
    //                                                List<Criteria> criteria ) throws ContainerCreateException;

    @Deprecated
    public boolean isConnected( Host host );

    //    public ManagementHost getManagementHost();

    public LocalPeer getLocalPeer();

    PeerGroup getPeerGroup( UUID peerGroupId );
}
