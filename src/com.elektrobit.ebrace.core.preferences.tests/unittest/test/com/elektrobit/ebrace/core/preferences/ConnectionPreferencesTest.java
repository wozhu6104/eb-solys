/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.core.preferences;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.elektrobit.ebrace.core.interactor.api.connect.ConnectionSettings;
import com.elektrobit.ebrace.core.interactor.api.resources.model.connection.ConnectionType;
import com.elektrobit.ebrace.core.preferences.impl.PreferenceConnectionType;
import com.elektrobit.ebrace.core.preferences.impl.PreferencesServiceImpl;
import com.elektrobit.ebrace.core.preferences.impl.PropertiesStore;

public class ConnectionPreferencesTest
{
    private static final String TEST_EXTENSION = "testExtension";
    private static final String TEST_NAME = "testName";
    private static final int TEST_DEFAULT_PORT = 1111;
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();
    private PreferencesServiceImpl preferencesService;
    private File tmpPreferenceFile;
    private ConnectionType connectionType;

    @Before
    public void setup() throws Exception
    {
        tmpPreferenceFile = tempFolder.newFile( "preferences.properties" );
        preferencesService = new PreferencesServiceImpl( new PropertiesStore( tmpPreferenceFile ),
                                                         () -> "default path" );
        connectionType = new PreferenceConnectionType( TEST_NAME, TEST_EXTENSION, TEST_DEFAULT_PORT );
    }

    @Test
    public void noConnectionOnStart() throws Exception
    {
        assertEquals( 0, preferencesService.getConnections().size() );
    }

    @Test
    public void oneConnectionAfterAddingConnection() throws Exception
    {
        addConnectionToService( "Connection 1", "localhost", 1234, true, connectionType );

        assertEquals( 1, preferencesService.getConnections().size() );
    }

    private ConnectionSettings addConnectionToService(String name, String host, int port, boolean saveToFile,
            ConnectionType connectionType)
    {
        List<ConnectionSettings> connections = new ArrayList<>();
        ConnectionSettings connectionSettings = new ConnectionSettings( name, host, port, saveToFile, connectionType );
        connections.add( connectionSettings );

        preferencesService.setConnections( connections );
        List<ConnectionSettings> connectionsLoaded = preferencesService.getConnections();
        Assert.assertEquals( 1, connectionsLoaded.size() );

        return connectionsLoaded.get( 0 );
    }

    @Test
    public void nameOfConnectionCorrect() throws Exception
    {
        ConnectionSettings connectionSettings = addConnectionToService( "Connection 1",
                                                                        "localhost",
                                                                        1234,
                                                                        true,
                                                                        connectionType );

        assertEquals( "Connection 1", connectionSettings.getName() );
    }

    @Test
    public void hostOfConnectionCorrect() throws Exception
    {
        ConnectionSettings connectionSettings = addConnectionToService( "Connection 1",
                                                                        "localhost",
                                                                        1234,
                                                                        true,
                                                                        connectionType );

        assertEquals( "localhost", connectionSettings.getHost() );
    }

    @Test
    public void portOfConnectionCorrect() throws Exception
    {
        ConnectionSettings connectionSettings = addConnectionToService( "Connection 1",
                                                                        "localhost",
                                                                        1234,
                                                                        true,
                                                                        connectionType );

        assertEquals( 1234, connectionSettings.getPort() );
    }

    @Test
    public void connectionTypeCorrect() throws Exception
    {
        ConnectionSettings connectionSettings = addConnectionToService( "Connection 1",
                                                                        "localhost",
                                                                        1234,
                                                                        true,
                                                                        connectionType );
        ConnectionType result = connectionSettings.getConnectionType();

        assertEquals( TEST_NAME, result.getName() );
        assertEquals( TEST_EXTENSION, result.getExtension() );
    }

    @Test
    public void saveToFileOfConnectionCorrect() throws Exception
    {
        ConnectionSettings connectionSettings = addConnectionToService( "Connection 1",
                                                                        "localhost",
                                                                        1234,
                                                                        true,
                                                                        connectionType );

        assertTrue( "Expecting save to file is set.", connectionSettings.isSaveToFile() );
    }

}
