/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.core.interactor.loadfile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

import com.elektrobit.ebrace.core.importerregistry.api.ImporterRegistry;
import com.elektrobit.ebrace.core.interactor.api.loadfile.OpenFileInteractionCallback;
import com.elektrobit.ebrace.core.interactor.api.usermessagelogger.UserMessageLoggerTypes;
import com.elektrobit.ebrace.core.interactor.loadfile.LoadFileInteractionUseCaseImpl;
import com.elektrobit.ebrace.core.tracefile.api.LoadFileService;
import com.elektrobit.ebrace.core.usermessagelogger.api.UserMessageLogger;

import test.com.elektrobit.ebrace.core.interactor.UseCaseBaseTest;

public class LoadFileInteractionUseCaseTest extends UseCaseBaseTest
{
    private String fileName = "C:\folder\file.bin";
    private OpenFileInteractionCallback mockedCallback;
    private LoadFileService mockedLoadFileService;
    private UserMessageLogger mockedUserMessageLogger;
    private LoadFileInteractionUseCaseImpl loadFileInteractionUseCase;
    private ImporterRegistry mockedImporterRegistry;

    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Before
    public void setUp() throws IOException
    {
        mockedCallback = Mockito.mock( OpenFileInteractionCallback.class );
        mockedLoadFileService = Mockito.mock( LoadFileService.class );
        mockedUserMessageLogger = Mockito.mock( UserMessageLogger.class );
        mockedImporterRegistry = Mockito.mock( ImporterRegistry.class );
        loadFileInteractionUseCase = new LoadFileInteractionUseCaseImpl( mockedCallback,
                                                                         mockedLoadFileService,
                                                                         mockedImporterRegistry,
                                                                         mockedUserMessageLogger );

        File temporaryBinFile = temporaryFolder.newFile( "file.bin" );
        fileName = temporaryBinFile.getAbsolutePath();

        Mockito.when( mockedLoadFileService.isFileNotTooBig( fileName ) ).thenReturn( true );
        Mockito.when( mockedLoadFileService.isFileNotLoaded( fileName ) ).thenReturn( true );
        Mockito.when( mockedLoadFileService.isFileEmpty( fileName ) ).thenReturn( false );
    }

    @Test
    public void testFileLoadingOK() throws Exception
    {
        loadFileInteractionUseCase.openFile( fileName );

        Mockito.verify( mockedLoadFileService ).loadFile( fileName );
    }

    @Test
    public void testFileTooBig() throws Exception
    {
        Mockito.when( mockedLoadFileService.isFileNotTooBig( fileName ) ).thenReturn( false );

        loadFileInteractionUseCase.openFile( fileName );

        Mockito.verify( mockedLoadFileService ).isFileNotTooBig( fileName );
        Mockito.verify( mockedCallback ).onFileTooBig( fileName );
    }

    @Test
    public void testFileAlreadyLoaded() throws Exception
    {
        Mockito.when( mockedLoadFileService.isFileNotLoaded( fileName ) ).thenReturn( false );

        loadFileInteractionUseCase.openFile( fileName );

        Mockito.verify( mockedLoadFileService ).isFileNotLoaded( fileName );
        Mockito.verify( mockedUserMessageLogger )
                .logUserMessage( UserMessageLoggerTypes.ERROR,
                                 LoadFileInteractionUseCaseImpl.FILE_ALREADY_LOADED_MESSAGE );
        Mockito.verify( mockedCallback ).onFileAlreadyLoaded( fileName );
    }

    @Test
    public void testFileIsEmpty() throws Exception
    {
        Mockito.when( mockedLoadFileService.isFileEmpty( fileName ) ).thenReturn( true );

        loadFileInteractionUseCase.openFile( fileName );

        Mockito.verify( mockedLoadFileService ).isFileEmpty( fileName );
        Mockito.verify( mockedUserMessageLogger ).logUserMessage( UserMessageLoggerTypes.ERROR,
                                                                  LoadFileInteractionUseCaseImpl.EMPTY_FILE_MESSAGE );
        Mockito.verify( mockedCallback ).onFileEmpty( fileName );
    }

    @Test
    public void testGetAnotherFileExtensions() throws Exception
    {
        List<String> types = Arrays.asList( new String[]{"CSV File", "XML File", "EB solys File"} );
        List<String> extensions = Arrays.asList( new String[]{"csv", "xml", "bin"} );

        List<List<String>> typesAndExtensions = new ArrayList<List<String>>();
        typesAndExtensions.add( types );
        typesAndExtensions.add( extensions );

        Mockito.when( mockedImporterRegistry.getSupportedFileTypesAndExtensions() ).thenReturn( typesAndExtensions );

        List<List<String>> result = loadFileInteractionUseCase.getAnotherFilesTypesAndExtensions();

        Assert.assertEquals( 2, result.size() );
        Assert.assertEquals( 4, result.get( 0 ).size() );
        Assert.assertEquals( 4, result.get( 1 ).size() );

        String allFilesExpectedType = result.get( 0 ).get( 0 );
        String allFilesExpectedExtension = result.get( 1 ).get( 0 );

        String csvExpectedType = result.get( 0 ).get( 1 );
        String csvExpectedExtension = result.get( 1 ).get( 1 );

        String xmlExpectedType = result.get( 0 ).get( 2 );
        String xmlExpectedExtension = result.get( 1 ).get( 2 );

        Assert.assertEquals( "All Importable Files", allFilesExpectedType );
        Assert.assertEquals( "*.csv;*.xml;*.bin", allFilesExpectedExtension );

        Assert.assertEquals( "CSV File (*.csv)", csvExpectedType );
        Assert.assertEquals( "*.csv", csvExpectedExtension );

        Assert.assertEquals( "XML File (*.xml)", xmlExpectedType );
        Assert.assertEquals( "*.xml", xmlExpectedExtension );
    }
}
