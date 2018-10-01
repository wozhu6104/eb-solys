/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.common.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.elektrobit.ebrace.common.utils.JsonHelper;

public class JsonHelperTest
{
    @Test
    public void getFieldFromJsonString()
    {
        String jsonString = "{\"appId\":\"SINA\", \"contextId\":\"SINC\", \"numArgs\":\"1\", \"Value\":\"GetPosition was called.\"}";
        String fieldName = "Value";

        String expected = "GetPosition was called.";
        String actual = JsonHelper.getFieldFromJsonString( jsonString, fieldName );
        assertEquals( expected, actual );
    }

    @Test
    public void noJsonString() throws Exception
    {
        String expected = null;
        String actual = JsonHelper.getFieldFromJsonString( "[26827314]DA1.DC1: service(3842), ok, 02 00 00 00 00",
                                                           "appId" );
        assertEquals( expected, actual );
    }

    @Test
    public void validJson()
    {
        assertTrue( JsonHelper
                .isJson( "{\"Action\":\"com.ebsolys.intent.EDIT_PLUGIN_CONFIG\",\"Extras\":{\"PLUGIN\":\"logcat-monitor\",\"VALUE\":\"DEBUG\",\"PARAMETER\":\"logPriority\"}}" ) );
    }

    @Test
    public void validJsonWithWhitespaces()
    {
        assertTrue( JsonHelper
                .isJson( " {\"Action\":\"com.ebsolys.intent.EDIT_PLUGIN_CONFIG\",\"Extras\":{\"PLUGIN\":\"logcat-monitor\",\"VALUE\":\"DEBUG\",\"PARAMETER\":\"logPriority\"}} " ) );
    }

    @Test
    public void corruptedJson()
    {
        assertFalse( JsonHelper
                .isJson( "{{\"Action\":\"com.ebsolys.intent.EDIT_PLUGIN_CONFIG\",\"Extras\":{\"PLUGIN\":\"logcat-monitor\",\"VALUE\":\"DEBUG\",\"PARAMETER\":\"logPriority\"}}" ) );
    }

    @Test
    public void noJsonWithinBrackets()
    {
        assertFalse( JsonHelper.isJson( "{no-json}" ) );
    }

}
