package org.safehaus.subutai.configuration.manager.impl.utils;


import org.safehaus.subutai.configuration.manager.api.ConfigTypeEnum;

import com.google.gson.JsonObject;


/**
 * Created by bahadyr on 7/21/14.
 */
public interface ConfigParser {
    JsonObject parserConfig(String pathToConfig, ConfigTypeEnum configTypeEnum);

//    public void setProperty( String path, String value );

//    public Object getProperty( String path );
}
