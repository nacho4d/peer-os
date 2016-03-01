package io.subutai.core.executor.rest;


import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.ws.rs.core.Response;

import org.bouncycastle.openpgp.PGPPublicKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

import io.subutai.common.host.HeartBeat;
import io.subutai.common.host.HeartbeatListener;
import io.subutai.common.security.crypto.pgp.ContentAndSignatures;
import io.subutai.common.settings.SystemSettings;
import io.subutai.common.util.JsonUtil;
import io.subutai.core.security.api.SecurityManager;
import io.subutai.core.security.api.crypto.EncryptionTool;


//temporarily made rest-impl as subscription service for heartbeat listeners
//TODO extract separate service/class for this purpose^
public class RestServiceImpl implements RestService
{
    private static final Logger LOG = LoggerFactory.getLogger( RestServiceImpl.class.getName() );
    private final SecurityManager securityManager;

    protected Set<HeartbeatListener> listeners =
            Collections.newSetFromMap( new ConcurrentHashMap<HeartbeatListener, Boolean>() );
    protected ExecutorService notifier = Executors.newCachedThreadPool();


    public RestServiceImpl( final SecurityManager securityManager )
    {
        Preconditions.checkNotNull( securityManager );

        this.securityManager = securityManager;
    }


    public void dispose()
    {
        notifier.shutdown();
    }


    public void addListener( HeartbeatListener listener )
    {
        if ( listener != null )
        {
            listeners.add( listener );
        }
    }


    public void removeListener( HeartbeatListener listener )
    {
        if ( listener != null )
        {
            listeners.remove( listener );
        }
    }


    @Override
    public Response processHeartbeat( final String heartbeat )
    {
        try
        {
            String decryptedHeartbeat = heartbeat;

            if ( SystemSettings.getEncryptionState() )
            {

                EncryptionTool encryptionTool = securityManager.getEncryptionTool();

                EncryptedResponseWrapper responseWrapper =
                        JsonUtil.fromJson( heartbeat, EncryptedResponseWrapper.class );

                ContentAndSignatures contentAndSignatures =
                        encryptionTool.decryptAndReturnSignatures( responseWrapper.getResponse().getBytes() );

                PGPPublicKey hostKeyForVerifying =
                        securityManager.getKeyManager().getPublicKey( responseWrapper.getHostId() );

                if ( encryptionTool.verifySignature( contentAndSignatures, hostKeyForVerifying ) )
                {
                    decryptedHeartbeat = new String( contentAndSignatures.getDecryptedContent() );
                }
                else
                {
                    throw new IllegalArgumentException( String.format( "Verification failed%nDecrypted Message: %s",
                            new String( contentAndSignatures.getDecryptedContent() ) ) );
                }
            }

            final HeartBeat heartBeat = JsonUtil.fromJson( decryptedHeartbeat, HeartBeat.class );

            for ( final HeartbeatListener listener : listeners )
            {
                notifier.submit( new Runnable()
                {
                    @Override
                    public void run()
                    {
                        try
                        {
                            listener.onHeartbeat( heartBeat );
                        }
                        catch ( Exception e )
                        {
                            LOG.error( "Error in processHeartbeat", e );
                        }
                    }
                } );
            }

            return Response.accepted().build();
        }
        catch ( Exception e )
        {
            return Response.status( Response.Status.INTERNAL_SERVER_ERROR ).
                    entity( e.getMessage() ).build();
        }
    }
}
