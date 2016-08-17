package io.subutai.common.environment;


import org.codehaus.jackson.annotate.JsonProperty;

import io.subutai.common.peer.ContainerSize;


/**
 * Node group schema
 */
public class NodeSchema
{
    @JsonProperty( "name" )
    private String name;

    @JsonProperty( "size" )
    private ContainerSize size;

    @JsonProperty( "templateName" )
    private String templateName;

    @JsonProperty( "templateId" )
    private String templateId;


    //TODO:TEMPLATE replace templateName with templateId when UI side is ready
    public NodeSchema( @JsonProperty( "name" ) final String name, @JsonProperty( "size" ) final ContainerSize size,
                       @JsonProperty( "templateName" ) final String templateName, @JsonProperty( "templateId" ) final String templateId )
    {
        this.name = name;
        this.size = size;
        this.templateName = templateName;
    }


    public String getName()
    {
        return name;
    }


    public ContainerSize getSize()
    {
        return size;
    }


    public String getTemplateName()
    {
        return templateName;
    }


    public String getTemplateId()
    {
        return templateId;
    }


    public void setTemplateId( final String templateId )
    {
        this.templateId = templateId;
    }
}
