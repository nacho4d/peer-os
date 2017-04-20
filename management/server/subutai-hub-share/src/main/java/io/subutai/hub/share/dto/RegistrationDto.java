package io.subutai.hub.share.dto;


public class RegistrationDto
{
    private String ownerFingerprint;

    private String ownerEmail;

    private String ownerPassword;

    private PeerInfoDto peerInfo;

    private UserTokenDto userToken;

    private String temp1;


    public RegistrationDto()
    {
    }


    public RegistrationDto( String ownerFingerprint )
    {
        this.ownerFingerprint = ownerFingerprint;
    }


    public String getOwnerFingerprint()
    {
        return ownerFingerprint;
    }


    public PeerInfoDto getPeerInfo()
    {
        return peerInfo;
    }


    public void setPeerInfo( final PeerInfoDto peerInfo )
    {
        this.peerInfo = peerInfo;
    }


    public void setPeerVersion( final String version )
    {
        this.peerInfo.setVersion( version );
    }


    public void setOwnerFingerprint( final String ownerFingerprint )
    {
        this.ownerFingerprint = ownerFingerprint;
    }


    public String getOwnerEmail()
    {
        return ownerEmail;
    }


    public void setOwnerEmail( final String ownerEmail )
    {
        this.ownerEmail = ownerEmail;
    }


    public String getOwnerPassword()
    {
        return ownerPassword;
    }


    public void setOwnerPassword( final String ownerPassword )
    {
        this.ownerPassword = ownerPassword;
    }


    public UserTokenDto getUserToken()
    {
        return userToken;
    }


    public void setUserToken( final UserTokenDto userToken )
    {
        this.userToken = userToken;
    }


    public String getTemp1()
    {
        return temp1;
    }


    public void setTemp1( final String temp1 )
    {
        this.temp1 = temp1;
    }

}
