package io.subutai.core.hintegration.impl;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPPrivateKey;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.bouncycastle.openpgp.PGPSecretKeyRing;
import org.bouncycastle.openpgp.PGPUtil;
import org.bouncycastle.openpgp.operator.jcajce.JcaKeyFingerprintCalculator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.jaxrs.ext.form.Form;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;

import io.subutai.common.security.SecurityProvider;
import io.subutai.common.security.crypto.certificate.CertificateData;
import io.subutai.common.security.crypto.certificate.CertificateTool;
import io.subutai.common.security.crypto.key.KeyPairType;
import io.subutai.common.security.crypto.keystore.KeyStoreData;
import io.subutai.common.security.crypto.keystore.KeyStoreTool;
import io.subutai.common.security.crypto.keystore.KeyStoreType;
import io.subutai.common.security.crypto.pgp.KeyPair;
import io.subutai.common.security.crypto.pgp.PGPEncryptionUtil;
import io.subutai.common.security.crypto.pgp.PGPKeyUtil;
import io.subutai.core.hintegration.api.HIntegrationException;
import io.subutai.core.hintegration.api.Integration;
import io.subutai.core.hintegration.impl.settings.HSettings;
import io.subutai.core.peer.api.PeerManager;
import io.subutai.core.security.api.SecurityManager;
import io.subutai.core.security.api.crypto.KeyManager;
import io.subutai.hub.common.dto.EnvironmentDTO;
import io.subutai.hub.common.dto.RegistrationDTO;
import io.subutai.hub.common.dto.TrustDataDto;
import io.subutai.hub.common.json.JsonUtil;
import io.subutai.hub.common.pgp.crypto.PGPSign;
import io.subutai.hub.common.pgp.key.PGPKeyHelper;
import io.subutai.hub.common.pgp.message.PGPMessenger;


public class IntegrationImpl implements Integration
{
    private static final Logger LOG = LoggerFactory.getLogger( IntegrationImpl.class.getName() );

    private static final String SERVER_NAME = "52.88.77.35";
    private SecurityManager securityManager;
    private PeerManager peerManager;
    public static final String OWNER_USER_ID = "owner@subutai.io";
    private BundleContext bundleContext;
    private PGPPublicKey hubPublicKey;
    private PGPPublicKey ownerPublicKey;
    private PGPPublicKey peerPublicKey;
    private ScheduledExecutorService hearbeatExecutorService = Executors.newSingleThreadScheduledExecutor();
    private HeartbeatProcessor processor = new HeartbeatProcessor();


    public void setSecurityManager( final SecurityManager securityManager )
    {
        this.securityManager = securityManager;
    }


    public void setPeerManager( final PeerManager peerManager )
    {
        this.peerManager = peerManager;
    }


    public void setBundleContext( final BundleContext bundleContext )
    {
        this.bundleContext = bundleContext;
    }


    public void init() throws IOException, PGPException
    {
        LOG.debug( "H-INTEGRATION" );

        generateKeys();

        this.hubPublicKey = PGPKeyHelper.readPublicKey( HSettings.HUB_PUB_KEY );
        this.ownerPublicKey = getOwnerPubKeyRing().getPublicKey();
        this.peerPublicKey = getPeerPubKeyRing().getPublicKey();
        this.hearbeatExecutorService.scheduleWithFixedDelay( processor, 10, 30, TimeUnit.SECONDS );
    }


    private void generateKeys()
    {
        try
        {
            final KeyManager keyManager = securityManager.getKeyManager();


            //**************Get Peer Public keyring ******************************************
            PGPPublicKeyRing peerPublicKeyRing = keyManager.getPublicKeyRing( null );
            String fingerprint = PGPKeyUtil.getFingerprint( peerPublicKeyRing.getPublicKey().getFingerprint() );


            //**************Saving certificate******************************************
            io.subutai.common.security.crypto.key.KeyManager sslkeyMan =
                    new io.subutai.common.security.crypto.key.KeyManager();
            KeyPairGenerator keyPairGenerator = sslkeyMan.prepareKeyPairGeneration( KeyPairType.RSA, 1024 );
            java.security.KeyPair sslKeyPair = sslkeyMan.generateKeyPair( keyPairGenerator );

            KeyStoreData keyStoreData = new KeyStoreData();
            keyStoreData.setKeyStoreFile( HSettings.PEER_KEYSTORE );
            keyStoreData.setAlias( "root_server_px1" );
            keyStoreData.setPassword( "subutai" );
            keyStoreData.setKeyStoreType( KeyStoreType.JKS );

            KeyStoreTool keyStoreTool = new KeyStoreTool();
            KeyStore sslkeyStore = keyStoreTool.load( keyStoreData );


            CertificateData certificateData = new CertificateData();
            certificateData.setCommonName( fingerprint );

            CertificateTool certificateTool = new CertificateTool();
            X509Certificate x509cert = certificateTool
                    .generateSelfSignedCertificate( sslkeyStore, sslKeyPair, SecurityProvider.BOUNCY_CASTLE,
                            certificateData );

            keyStoreTool.saveX509Certificate( sslkeyStore, keyStoreData, x509cert, sslKeyPair );


            //****************Save Owner secret key ****************************************
            KeyPair ownerKeyPair = keyManager.generateKeyPair( OWNER_USER_ID, false );

            File ownerSecretKeyFile = new File( HSettings.PEER_OWNER_SECRET_KEY );

            try ( FileOutputStream fop = new FileOutputStream( ownerSecretKeyFile ) )
            {
                if ( !ownerSecretKeyFile.exists() )
                {
                    ownerSecretKeyFile.createNewFile();
                }

                fop.write( ownerKeyPair.getSecKeyring() );
                fop.flush();
                fop.close();

                LOG.debug( "owner secret key genereated" );
            }
            catch ( Exception ex )
            {
                ex.printStackTrace();
            }


            //****************Save Owner Public key ****************************************
            File ownerPubKeyFile = new File( HSettings.PEER_OWNER_PUB_KEY );

            try ( FileOutputStream fop = new FileOutputStream( ownerPubKeyFile ) )
            {
                if ( !ownerPubKeyFile.exists() )
                {
                    ownerPubKeyFile.createNewFile();
                }

                fop.write( ownerKeyPair.getPubKeyring() );
                fop.flush();
                fop.close();

                LOG.debug( "owner public key genereated" );
            }
            catch ( Exception ex )
            {
                ex.printStackTrace();
            }


            //****************Sign pub of peer with owner ****************************************
            PGPSecretKeyRing ownerSecretKeyRing = PGPKeyUtil.readSecretKeyRing( ownerKeyPair.getSecKeyring() );
            PGPPublicKeyRing publicKeyRing = PGPEncryptionUtil
                    .signPublicKey( peerPublicKeyRing, OWNER_USER_ID, ownerSecretKeyRing.getSecretKey(), "12345678" );


            //****************Save Peer Public key ****************************************
            File peerPubKeyFile = new File( HSettings.PEER_PUB_KEY );

            try ( FileOutputStream fop = new FileOutputStream( peerPubKeyFile ) )
            {
                if ( !peerPubKeyFile.exists() )
                {
                    peerPubKeyFile.createNewFile();
                }

                publicKeyRing.encode( fop );
                fop.flush();
                fop.close();

                LOG.debug( "peer public key genereated" );
            }
            catch ( Exception ex )
            {
                ex.printStackTrace();
            }

            LOG.debug( "Is encryption key of owner?:" + ownerSecretKeyRing.getPublicKey().isEncryptionKey() );
            LOG.debug( "Is encryption key of peer?:" + publicKeyRing.getPublicKey().isEncryptionKey() );
            LOG.debug( "Is signed: " + PGPEncryptionUtil.verifyPublicKey( publicKeyRing.getPublicKey(), OWNER_USER_ID,
                    ownerSecretKeyRing.getPublicKey() ) );
            //
            //            //****************Loading hub key from resources ****************************************
            //            InputStream is = bundleContext.getBundle().getEntry( "keys/hub.public.gpg" ).openStream();
            //            PGPPublicKey hPubKey = PGPKeyUtil.readPublicKey( is );
        }
        catch ( Exception ex )
        {
            ex.printStackTrace();
        }
    }


    @Override
    public void registerOwnerPubKey() throws HIntegrationException
    {
        String baseUrl = String.format( "https://test.stage-hub.net/" );
        WebClient client = io.subutai.core.hintegration.impl.HttpClient.createTrustedWebClient( baseUrl );

        client.type( MediaType.APPLICATION_FORM_URLENCODED ).accept( MediaType.APPLICATION_JSON );

        Form form = new Form();
        try
        {
            form.set( "keytext", PGPEncryptionUtil.armorByteArrayToString( getOwnerPubKeyRing().getEncoded() ) );
        }
        catch ( PGPException | IOException e )
        {
            throw new HIntegrationException( "Could not read owner pub key", e );
        }

        Response response = client.path( "pks/add" ).post( form );

        if ( response.getStatus() == HttpStatus.SC_CREATED )
        {
            LOG.debug( "Owner pub key successfully registered." );
        }
        else
        {
            LOG.error( "Owner pub key registration problem." );
        }
    }


    @Override
    public void registerPeerPubKey() throws HIntegrationException
    {
        String baseUrl = String.format( "https://test.stage-hub.net/" );
        WebClient client = io.subutai.core.hintegration.impl.HttpClient.createTrustedWebClient( baseUrl );

        client.type( MediaType.APPLICATION_FORM_URLENCODED ).accept( MediaType.APPLICATION_JSON );

        Form form = new Form();
        try
        {
            form.set( "keytext", PGPEncryptionUtil.armorByteArrayToString( getPeerPubKeyRing().getEncoded() ) );
        }
        catch ( PGPException | IOException e )
        {
            throw new HIntegrationException( "Could not read peer pub key", e );
        }

        Response response = client.path( "pks/add" ).post( form );

        if ( response.getStatus() == HttpStatus.SC_CREATED )
        {
            LOG.debug( "Owner pub key successfully registered." );
        }
        else
        {
            LOG.error( "Owner pub key registration problem." );
        }
    }


    @Override
    public void sendTrustData() throws HIntegrationException
    {
        String baseUrl = String.format( "https://%s:4000", SERVER_NAME );

        try
        {
            String path = String.format( "/rest/v1/keyserver/keys/%s/trust/%s", peerPublicKey.getKeyID(),
                    ownerPublicKey.getKeyID() );

            TrustDataDto trustDataDto =
                    new TrustDataDto( String.valueOf( getPeerPubKeyRing().getPublicKey().getKeyID() ),
                            String.valueOf( getOwnerPubKeyRing().getPublicKey().getKeyID() ),
                            TrustDataDto.TrustLevel.FULL );

            byte[] serverFingerprint = hubPublicKey.getFingerprint();

            KeyStore keyStore = KeyStore.getInstance( "JKS" );

            keyStore.load( new FileInputStream( HSettings.PEER_KEYSTORE ), "subutai".toCharArray() );

            WebClient client = HttpClient
                    .createTrustedWebClientWithAuth( baseUrl + path, keyStore, "subutai".toCharArray(),
                            serverFingerprint );

            byte[] cborData = JsonUtil.toCbor( trustDataDto );

            KeyManager keyManager = securityManager.getKeyManager();
            PGPPrivateKey senderKey = keyManager.getPrivateKey( null );
            PGPMessenger messenger = new PGPMessenger( senderKey, hubPublicKey );

            byte[] encryptedData = messenger.produce( cborData );
            Response r = client.post( encryptedData );

            if ( r.getStatus() != HttpStatus.SC_NO_CONTENT )
            {
                throw new HIntegrationException( "Could not send trust data: " + r.readEntity( String.class ) );
            }
        }
        catch ( PGPException | IOException | KeyStoreException | UnrecoverableKeyException | NoSuchAlgorithmException |
                CertificateException e )
        {
            throw new HIntegrationException( e.toString(), e );
        }
    }


    @Override
    public void register() throws HIntegrationException
    {
        String baseUrl = String.format( "https://%s:4000", SERVER_NAME );

        try
        {
            String path = String.format( "/rest/v1/peers/%s", peerManager.getLocalPeerInfo().getId() );

            RegistrationDTO registrationData = new RegistrationDTO( PGPKeyHelper.getFingerprint( ownerPublicKey ) );

            byte[] serverFingerprint = hubPublicKey.getFingerprint();

            KeyStore keyStore = KeyStore.getInstance( "JKS" );

            keyStore.load( new FileInputStream( HSettings.PEER_KEYSTORE ), "subutai".toCharArray() );

            WebClient client = HttpClient
                    .createTrustedWebClientWithAuth( baseUrl + path, keyStore, "subutai".toCharArray(),
                            serverFingerprint );

            byte[] cborData = JsonUtil.toCbor( registrationData );

            KeyManager keyManager = securityManager.getKeyManager();
            PGPPrivateKey senderKey = keyManager.getPrivateKey( null );
            PGPMessenger messenger = new PGPMessenger( senderKey, hubPublicKey );

            byte[] encryptedData = messenger.produce( cborData );
            Response r = client.post( encryptedData );


            if ( r.getStatus() != HttpStatus.SC_NO_CONTENT )
            {
                throw new HIntegrationException( "Could not register local peer: " + r.readEntity( String.class ) );
            }
        }
        catch ( PGPException | IOException | KeyStoreException | UnrecoverableKeyException | NoSuchAlgorithmException |
                CertificateException e )
        {
            throw new HIntegrationException( e.toString(), e );
        }
    }


    private PGPPublicKeyRing getOwnerPubKeyRing() throws IOException
    {
        InputStream in = PGPEncryptionUtil.getFileInputStream( HSettings.PEER_OWNER_PUB_KEY );
        PGPPublicKeyRing ownerPubKeyRing =
                new PGPPublicKeyRing( PGPUtil.getDecoderStream( in ), new JcaKeyFingerprintCalculator() );
        return ownerPubKeyRing;
    }


    private PGPPublicKeyRing getHPubKeyRing() throws IOException
    {
        InputStream in = PGPEncryptionUtil.getFileInputStream( HSettings.HUB_PUB_KEY );
        PGPPublicKeyRing peerPubKeyRing =
                new PGPPublicKeyRing( PGPUtil.getDecoderStream( in ), new JcaKeyFingerprintCalculator() );
        return peerPubKeyRing;
    }


    private PGPPublicKeyRing getPeerPubKeyRing() throws IOException
    {
        InputStream in = PGPEncryptionUtil.getFileInputStream( HSettings.PEER_PUB_KEY );
        PGPPublicKeyRing peerPubKeyRing =
                new PGPPublicKeyRing( PGPUtil.getDecoderStream( in ), new JcaKeyFingerprintCalculator() );
        return peerPubKeyRing;
    }


    private PGPSecretKeyRing getPeerSecretKeyRing() throws IOException, PGPException
    {
        InputStream in = PGPEncryptionUtil.getFileInputStream( HSettings.PEER_PUB_KEY );
        PGPSecretKeyRing secretKeyRing =
                new PGPSecretKeyRing( PGPUtil.getDecoderStream( in ), new JcaKeyFingerprintCalculator() );
        return secretKeyRing;
    }
}
