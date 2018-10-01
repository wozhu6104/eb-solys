/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.core.interactor.connect;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.elektrobit.ebrace.core.interactor.api.connect.ConnectionSettings;
import com.elektrobit.ebrace.core.interactor.api.resources.model.connection.ConnectionType;

public class ConnectionSettingsTest
{
    private ConnectionSettings connectionSettings;
    private ConnectionType connectionType1;
    private ConnectionType connectionType2;

    @Before
    public void setup()
    {
        connectionType1 = new ConnectionType()
        {
            @Override
            public String getName()
            {
                return "type1";
            }

            @Override
            public String getExtension()
            {
                return "extension1";
            }

            @Override
            public int getDefaultPort()
            {
                return 1234;
            }
        };

        connectionType2 = new ConnectionType()
        {
            @Override
            public String getName()
            {
                return "type2";
            }

            @Override
            public String getExtension()
            {
                return "extension2";
            }

            @Override
            public int getDefaultPort()
            {
                return 1234;
            }
        };
        connectionSettings = new ConnectionSettings( "Name", "Host", 1234, true, connectionType1 );
    }

    @Test
    public void checkNameGetterWorks()
    {
        assertEquals( "Name", connectionSettings.getName() );
    }

    @Test
    public void checkHostGetterWorks()
    {
        assertEquals( "Host", connectionSettings.getHost() );
    }

    @Test
    public void checkPortGetterWorks()
    {
        assertEquals( 1234, connectionSettings.getPort() );
    }

    @Test
    public void checkSaveToFileGetterWorks()
    {
        assertEquals( true, connectionSettings.isSaveToFile() );
    }

    @Test
    public void checkEqualsWithNull()
    {
        assertFalse( connectionSettings.equals( null ) );
    }

    @Test
    public void checkEqualsWithSameInstance()
    {
        assertTrue( connectionSettings.equals( connectionSettings ) );
    }

    @Test
    public void checkEqualsWithAllDifferentFieldValues()
    {
        assertFalse( connectionSettings
                .equals( new ConnectionSettings( "Name2", "Host2", 234, false, connectionType2 ) ) );
    }

    @Test
    public void checkEqualsWithDifferentNameFieldValues()
    {
        assertFalse( connectionSettings
                .equals( new ConnectionSettings( "Name2", "Host", 1234, true, connectionType1 ) ) );
    }

    @Test
    public void checkEqualsWithSameFieldValues()
    {
        assertTrue( connectionSettings
                .equals( new ConnectionSettings( "Name", "Host", 1234, true, connectionType2 ) ) );
    }

    @Test
    public void checkEqualsWithOnlyNameSameFieldValues()
    {
        assertTrue( connectionSettings
                .equals( new ConnectionSettings( "Name", "Host2", 12345, false, connectionType2 ) ) );
    }

    @Test
    public void checkHashCodeStaysConstant()
    {
        assertEquals( 2420454, connectionSettings.hashCode() );
    }

    @Test
    public void checkHashCodeWithSameFieldValues()
    {
        assertEquals( new ConnectionSettings( "Name", "Host", 1234, true, connectionType1 ).hashCode(),
                      connectionSettings.hashCode() );
    }

    @Test
    public void checkHashCodeWithOnlyNameSameFieldValues()
    {
        assertEquals( new ConnectionSettings( "Name", "Host2", 12345, false, connectionType2 ).hashCode(),
                      connectionSettings.hashCode() );
    }

    @Test
    public void checkHashCodeWithAllDifferentFieldValues()
    {
        assertNotEquals( new ConnectionSettings( "Name2", "Host2", 234, false, connectionType2 ).hashCode(),
                         connectionSettings.hashCode() );
    }

    @Test
    public void checkHashCodeWithDifferentNameFieldValues()
    {
        assertNotEquals( new ConnectionSettings( "Name2", "Host", 1234, true, connectionType1 ).hashCode(),
                         connectionSettings.hashCode() );
    }

}
