package io.subutai.common.host;


import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Sets;


/**
 * Host interfaces collection
 */
public class HostInterfaces implements Serializable
{

    @JsonProperty( "hostId" )
    private String hostId;

    @JsonProperty( "interfaces" )
    private Set<HostInterfaceModel> interfaces = new HashSet<>();


    public HostInterfaces( @JsonProperty( "hostId" ) final String hostId,
                           @JsonProperty( "interfaces" ) final Set<HostInterfaceModel> interfaces )
    {
        this.hostId = hostId;
        this.interfaces = interfaces;
    }


    public HostInterfaces()
    {

    }


    public String getHostId()
    {
        return hostId;
    }


    public HostInterfaceModel findByIp( final String ip )
    {
        Preconditions.checkNotNull( ip );
        HostInterfaceModel result = NullHostInterface.getInstance();

        for ( Iterator<HostInterfaceModel> i = interfaces.iterator();
              i.hasNext() && result instanceof NullHostInterface; )
        {
            HostInterfaceModel c = i.next();
            if ( ip.equals( c.getIp() ) )
            {
                result = c;
            }
        }
        return result;
    }


    public HostInterfaceModel findByName( final String name )
    {
        Preconditions.checkNotNull( name );
        HostInterfaceModel result = NullHostInterface.getInstance();
        if ( interfaces == null )
        {
            return result;
        }
        for ( Iterator<HostInterfaceModel> i = interfaces.iterator();
              i.hasNext() && result instanceof NullHostInterface; )
        {
            HostInterfaceModel c = i.next();
            if ( name.equals( c.getName() ) )
            {
                result = c;
            }
        }
        return result;
    }


    public Set<HostInterfaceModel> filterByIp( final String pattern )
    {
        Preconditions.checkNotNull( pattern );
        Set<HostInterfaceModel> result = Sets.filter( interfaces, new Predicate<HostInterface>()
        {
            @Override
            public boolean apply( final HostInterface intf )
            {
                return intf.getIp().matches( pattern );
            }
        } );

        return Collections.unmodifiableSet( result );
    }


    public Set<HostInterfaceModel> filterByName( final String pattern )
    {
        Preconditions.checkNotNull( pattern );
        return Sets.filter( interfaces, new Predicate<HostInterfaceModel>()
        {
            @Override
            public boolean apply( final HostInterfaceModel intf )
            {
                return intf.getName().matches( pattern );
            }
        } );
    }


    public int size()
    {
        return interfaces.size();
    }


    public void addHostInterface( final HostInterfaceModel hostInterfaceModel )
    {
        this.interfaces.add( hostInterfaceModel );
    }


    @JsonIgnore
    public Set<HostInterfaceModel> getAll()
    {
        if ( this.interfaces != null )
        {
            return Collections.unmodifiableSet( this.interfaces );
        }
        else
        {
            return new HashSet<>();
        }
    }


    @Override
    public String toString()
    {
        return "HostInterfaces{" + "interfaces=" + interfaces + '}';
    }
}
