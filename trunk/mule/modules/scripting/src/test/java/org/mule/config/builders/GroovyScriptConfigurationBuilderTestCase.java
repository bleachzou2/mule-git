/* 
* $Id: GroovyScriptConfigurationBuilderTestCase.java 2181 2006-06-04 23:09:23Z holger $
* ------------------------------------------------------------------------------------------------------
* 
* Copyright (c) SymphonySoft Limited. All rights reserved.
* http://www.symphonysoft.com
* 
* The software in this package is published under the terms of the BSD
* style license a copy of which has been included with this distribution in
* the LICENSE.txt file. 
*
*/
package org.mule.config.builders;

import org.mule.config.ConfigurationBuilder;
import org.mule.config.builders.ScriptConfigurationBuilder;
import org.mule.tck.AbstractScriptConfigBuilderTestCase;

/**
 *
 * @author <a href="mailto:ross.mason@symphonysoft.com">Ross Mason</a>
 * @version $Revision: 2181 $
 */
public class GroovyScriptConfigurationBuilderTestCase extends AbstractScriptConfigBuilderTestCase
{
    public String getConfigResources() {
        return "mule-config.groovy";
    }

    public ConfigurationBuilder getBuilder() {
       ScriptConfigurationBuilder builder = new ScriptConfigurationBuilder("groovy");
        return builder;
    }
}
