/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.targetdata.importer.raceusecaselogimporter;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

import com.elektrobit.ebrace.targetdata.importer.internal.raceusecaselogimporter.SolysGenericLogImporterService;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventAcceptor;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.Unit;

public class SolysGenericLogImporterServiceTest
{
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @SuppressWarnings("unchecked")
    @Test
    public void channelCreated() throws Exception
    {
        SolysGenericLogImporterService importerService = new SolysGenericLogImporterService();

        RuntimeEventAcceptor runtimeEventAcceptor = mock( RuntimeEventAcceptor.class );

        String fileContent = "trace.ustats.info << 01-01-1970_00:00:01.234 | {\"Type\":\"LOGGING_STARTED\",\"JvmStartupTime\":\"07-28-2017_12:08:29.223\"}";

        File fileToImport = folder.newFile();
        FileUtils.writeStringToFile( fileToImport, fileContent );

        importerService.bind( runtimeEventAcceptor );
        importerService.processFileContent( fileToImport );

        verify( runtimeEventAcceptor ).createOrGetRuntimeEventChannel( "trace.ustats.info", Unit.TEXT, "" );
        verify( runtimeEventAcceptor ).acceptEventMicros( Mockito.eq( 1234000L ),
                                                          Mockito.any( RuntimeEventChannel.class ),
                                                          Mockito.eq( null ),
                                                          Mockito.eq( "{\"Type\":\"LOGGING_STARTED\",\"JvmStartupTime\":\"07-28-2017_12:08:29.223\"}" ) );

    }

}
