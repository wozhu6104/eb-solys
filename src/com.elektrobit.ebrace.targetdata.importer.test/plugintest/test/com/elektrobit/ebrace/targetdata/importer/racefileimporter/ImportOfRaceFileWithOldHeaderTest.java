/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.targetdata.importer.racefileimporter;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.elektrobit.ebrace.core.importerregistry.api.Importer;
import com.elektrobit.ebrace.core.interactor.api.usermessagelogger.UserMessageLoggerTypes;
import com.elektrobit.ebrace.core.usermessagelogger.api.UserMessageLogger;
import com.elektrobit.ebrace.core.usermessagelogger.api.UserMessageLoggerListener;
import com.elektrobit.ebrace.dev.test.util.services.CoreServiceHelper;
import com.elektrobit.ebrace.dev.test.util.targetdata.TargetDataFileBuilder;
import com.elektrobit.ebrace.targetadapter.communicator.connectionhandling.VersionHandler;
import com.elektrobit.ebrace.targetagent.protocol.commondefinitions.TargetAgentProtocolCommonDefinitions.MessageType;
import com.elektrobit.ebsolys.core.targetdata.api.reset.ResetNotifier;

import junit.framework.Assert;
import test.com.elektrobit.ebrace.targetdata.dlt.DltMessages;

public class ImportOfRaceFileWithOldHeaderTest implements UserMessageLoggerListener
{
    private int numberOfUserMessages;
    private String firstMessage;
    private String secondMessage;
    private UserMessageLogger userMessageLogger;
    private Importer raceFileImporter;
    private File raceTestFile;

    @Before
    public void setup()
    {
        numberOfUserMessages = 0;
        userMessageLogger = CoreServiceHelper.getUserMessageLogger();
        userMessageLogger.register( this );
        raceFileImporter = CoreServiceHelper.getImporterRegistry().getImporterForFileExtension( "bin" );
        raceTestFile = new File( createOldHeaderTestfile() );
        ResetNotifier resetNotifier = CoreServiceHelper.getResetNotifier();
        resetNotifier.performReset();
    }

    @Test
    public void checkIfUserMessageIsSent() throws Exception
    {
        raceFileImporter.importFile( raceTestFile );

        Assert.assertEquals( 1, numberOfUserMessages );
        Assert.assertTrue( "Importing of a race file with old header should trigger a user message.",
                           firstMessage != null );
    }

    @Test
    public void checkIfUserMessageIsSentAfterAReset() throws Exception
    {
        raceFileImporter.importFile( raceTestFile );
        CoreServiceHelper.getResetNotifier().performReset();
        raceFileImporter.importFile( raceTestFile );

        Assert.assertEquals( 2, numberOfUserMessages );
        Assert.assertTrue( "Importing of a race file with old header after a reset should trigger a user message.",
                           secondMessage != null );
    }

    private String createOldHeaderTestfile()
    {
        String fileName = "useCaseTestTrace.bin";

        TargetDataFileBuilder builder = new TargetDataFileBuilder( VersionHandler.getVersionToken(), true );
        for (int i = 0; i < 100; i++)
        {
            long timestamp = 1000 + i;

            builder.addProtoMessage( timestamp,
                                     MessageType.MSG_TYPE_GENIVI_DLT_MONITOR_PLUGIN,
                                     DltMessages.getDltDummyMessage().toByteArray() );

        }

        File file = builder.createFile( fileName );
        return file.getAbsolutePath();
    }

    @Override
    public void newUserMessageReceived(UserMessageLoggerTypes type, String message)
    {
        numberOfUserMessages++;
        if (numberOfUserMessages == 1)
        {
            this.firstMessage = message;
        }
        else if (numberOfUserMessages == 2)
        {
            this.secondMessage = message;
        }
    }

    @After
    public void cleanUp()
    {
        userMessageLogger.unregister( this );
    }
}
