/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.core.tracefile.internal;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.mockito.Mockito;

import com.elektrobit.ebrace.core.tracefile.internal.FileSizeLimitServiceImpl;
import com.elektrobit.ebrace.platform.commandlineparser.extern.interfaces.CommandLineParser;
import com.elektrobit.ebrace.platform.commandlineparser.extern.interfaces.ValueOfArgumentMissingException;

public class FileSizeLimitServiceImplTest
{
    @Test
    public void testValidValue() throws Exception
    {
        CommandLineParser mockedCommandlineParserWithReturnValue = mockedCommandlineParserWithReturnValue( "200" );
        FileSizeLimitServiceImpl sut = new FileSizeLimitServiceImpl();
        sut.bind( mockedCommandlineParserWithReturnValue );

        assertEquals( 200, sut.getMaxSolysFileSizeMB() );
    }

    @Test
    public void testInvalidValue() throws Exception
    {
        CommandLineParser mockedCommandlineParserWithReturnValue = mockedCommandlineParserWithReturnValue( "abc" );
        FileSizeLimitServiceImpl sut = new FileSizeLimitServiceImpl();
        sut.bind( mockedCommandlineParserWithReturnValue );

        assertEquals( FileSizeLimitServiceImpl.DEFAULT_FILE_LIMIT_MB, sut.getMaxSolysFileSizeMB() );
    }

    @Test
    public void testZeroMeansUnlimited() throws Exception
    {
        CommandLineParser mockedCommandlineParserWithReturnValue = mockedCommandlineParserWithReturnValue( "0" );
        FileSizeLimitServiceImpl sut = new FileSizeLimitServiceImpl();
        sut.bind( mockedCommandlineParserWithReturnValue );

        assertEquals( Long.MAX_VALUE, sut.getMaxSolysFileSizeMB() );
    }

    private CommandLineParser mockedCommandlineParserWithReturnValue(String value)
            throws ValueOfArgumentMissingException
    {
        CommandLineParser mockedCommandlineParser = Mockito.mock( CommandLineParser.class );
        Mockito.when( mockedCommandlineParser.getValue( FileSizeLimitServiceImpl.SOLYS_LIMIT_FILE_SIZE_PARAMETER ) )
                .thenReturn( value );
        Mockito.when( mockedCommandlineParser.hasArg( FileSizeLimitServiceImpl.SOLYS_LIMIT_FILE_SIZE_PARAMETER ) )
                .thenReturn( true );
        return mockedCommandlineParser;
    }

    @Test
    public void testNoValueSet() throws Exception
    {
        CommandLineParser mockedCommandlineParser = Mockito.mock( CommandLineParser.class );
        FileSizeLimitServiceImpl sut = new FileSizeLimitServiceImpl();
        sut.bind( mockedCommandlineParser );

        assertEquals( FileSizeLimitServiceImpl.DEFAULT_FILE_LIMIT_MB, sut.getMaxSolysFileSizeMB() );
    }
}
