/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.core.interactor.headlessexecutor.runners;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.elektrobit.ebrace.core.interactor.headlessexecutor.runners.file.impl.FileLoadRunner;
import com.elektrobit.ebrace.core.tracefile.api.LoadFileService;

public class FileLoadRunnerTest
{
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    private FileLoadRunner runner;
    private LoadFileService loadFileService;

    @Before
    public void setup()
    {
        loadFileService = mock( LoadFileService.class );
        runner = new FileLoadRunner( loadFileService );
    }

    @Test
    public void paramsNotOkIfPathToFileNull() throws Exception
    {
        assertFalse( "Expecting params not ok, if file param is null.", runner.paramsOk( null ) );
    }

    @Test
    public void paramsNotOkIfFileNotExists() throws Exception
    {
        assertFalse( "Expecting params not ok, if file does not exists.", runner.paramsOk( "FileDoesNotExist" ) );
    }

    @Test
    public void paramsOkIfFileExists() throws Exception
    {
        assertTrue( "Expecting params ok, if file does exists.",
                    runner.paramsOk( folder.newFile().getAbsolutePath() ) );
    }

    @Test
    public void resultOkIfFileLoadingOk() throws Exception
    {
        when( loadFileService.loadFile( "" ) ).thenReturn( true );
        assertTrue( "Expecting run ok, if file loading ok.", runner.run( "" ) );
    }

    @Test
    public void resultNOkIfFileLoadingNOk() throws Exception
    {
        when( loadFileService.loadFile( "" ) ).thenReturn( false );
        assertFalse( "Expecting run result not ok, if file loading not ok.", runner.run( "" ) );
    }
}
