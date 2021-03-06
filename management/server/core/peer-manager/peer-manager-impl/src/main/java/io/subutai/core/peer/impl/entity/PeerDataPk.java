package io.subutai.core.peer.impl.entity;


import java.io.Serializable;


class PeerDataPk implements Serializable
{
    private String id;
    private String source;


    public PeerDataPk()
    {

    }


    public String getId()
    {
        return id;
    }


    public void setId( final String id )
    {
        this.id = id;
    }


    public String getSource()
    {
        return source;
    }


    public void setSource( final String source )
    {
        this.source = source;
    }


    @Override
    public boolean equals( Object obj )
    {
        if ( obj instanceof PeerDataPk )
        {
            PeerDataPk ppk = ( PeerDataPk ) obj;

            return ppk.getId().equals( this.id ) && ppk.getSource().equals( this.source );
        }

        return false;
    }


    @Override
    public int hashCode()
    {
        return id.hashCode() + source.hashCode();
    }
}
