/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.targetdata.importer.networkdataimporter;

import java.util.List;

import org.eclipse.core.runtime.Path;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.elektrobit.ebrace.common.utils.GenericOSGIServiceTracker;
import com.elektrobit.ebrace.core.tracefile.api.LoadFileService;
import com.elektrobit.ebrace.targetdata.importer.internal.NetworkDataImporter;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventAcceptor;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;

public class NetworkDataImporterTest
{

    @Rule
    public ExpectedException exception = ExpectedException.none();

    public static final String filePath = "testdata" + Path.SEPARATOR;

    public static final String fileName = "pdml_test_file";
    public static final String extension = ".pdml";
    public static final String value = "<packet>";
    public static final int numberOfLines = 1;
    public static final int numberOfNewRuntimeEvents = 4;

    private static RuntimeEventAcceptor runtimeEventAcceptor;
    private static LoadFileService loadFileService;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
        runtimeEventAcceptor = new GenericOSGIServiceTracker<RuntimeEventAcceptor>( RuntimeEventAcceptor.class )
                .getService();
        loadFileService = new GenericOSGIServiceTracker<LoadFileService>( LoadFileService.class ).getService();
    }

    @Test
    public void mportFile()
    {
        loadFileService.loadFile( filePath + fileName + extension );

        RuntimeEventChannel<?> runtimeEventTestChannel = getRuntimeEventTestChannel();
        Assert.assertNotNull( runtimeEventTestChannel );

        List<RuntimeEvent<?>> runtimeEventsOfRuntimeEventChannel = runtimeEventAcceptor
                .getRuntimeEventsOfRuntimeEventChannel( runtimeEventTestChannel );
        Assert.assertEquals( numberOfNewRuntimeEvents, runtimeEventsOfRuntimeEventChannel.size() );
        Assert.assertEquals( value,
                             runtimeEventsOfRuntimeEventChannel.get( 0 ).getValue().toString()
                                     .substring( 0, value.length() ) );
    }

    private RuntimeEventChannel<?> getRuntimeEventTestChannel()
    {
        for (RuntimeEventChannel<?> channel : runtimeEventAcceptor.getRuntimeEventChannels())
        {
            if (channel.getName().startsWith( NetworkDataImporter.TRACE_CHANNEL_PREFIX + fileName + extension ))
            {
                return channel;
            }
        }
        return null;
    }

}
