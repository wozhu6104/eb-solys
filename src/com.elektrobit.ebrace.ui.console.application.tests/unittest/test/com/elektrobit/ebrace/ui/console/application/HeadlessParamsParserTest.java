/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.ui.console.application;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import com.elektrobit.ebrace.core.interactor.api.headlessexecutor.HeadlessParamConstants;
import com.elektrobit.ebrace.core.interactor.api.headlessexecutor.HeadlessParams;
import com.elektrobit.ebrace.platform.commandlineparser.extern.interfaces.CommandLineParser;
import com.elektrobit.ebrace.platform.commandlineparser.extern.interfaces.ValueOfArgumentMissingException;
import com.elektrobit.ebrace.ui.console.application.impl.HeadlessParamsParser;

public class HeadlessParamsParserTest
{
    @Test
    public void testNoParams() throws ValueOfArgumentMissingException
    {
        CommandLineParser mockedCommandLineParser = Mockito.mock( CommandLineParser.class );

        setExceptionForParam( HeadlessParamConstants.SCRIPT_PARAMETER_SWITCH, mockedCommandLineParser );
        setExceptionForParam( HeadlessParamConstants.FILE_TO_LOAD_PARAMETER_SWITCH, mockedCommandLineParser );
        setExceptionForParam( HeadlessParamConstants.CONNECT_PARAMETER_SWITCH, mockedCommandLineParser );
        setExceptionForParam( HeadlessParamConstants.DESTINATION_FILE_PREFIX_PARAMETER_SWITCH,
                              mockedCommandLineParser );
        setExceptionForParam( HeadlessParamConstants.DESTINATION_PATH_PARAMETER_SWITCH, mockedCommandLineParser );
        Mockito.when( mockedCommandLineParser.hasArg( HeadlessParamConstants.HELP_PARAMETER_SWITCH ) )
                .thenReturn( false );

        HeadlessParamsParser sut = new HeadlessParamsParser( mockedCommandLineParser );
        HeadlessParams params = sut.getParams();

        Assert.assertNull( params.scriptName );
        Assert.assertNull( params.fileToLoadName );
        Assert.assertNull( params.targetIpAdress );
        Assert.assertNull( params.targetPort );
        Assert.assertNull( params.saveToFilePrefix );
        Assert.assertNull( params.saveToFilePath );
        Assert.assertFalse( params.isHelpSwitchSet );
    }

    @Test
    public void testAllParams() throws ValueOfArgumentMissingException
    {
        CommandLineParser mockedCommandLineParser = Mockito.mock( CommandLineParser.class );

        String scriptFileName = "script.jar";
        String datafileName = "datafile.bin";
        String ipAdress = "192.168.0.1";
        String port = "1234";
        String prefixName = "prefix";
        String path = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();

        setValueForParam( HeadlessParamConstants.SCRIPT_PARAMETER_SWITCH, scriptFileName, mockedCommandLineParser );

        setValueForParam( HeadlessParamConstants.FILE_TO_LOAD_PARAMETER_SWITCH, datafileName, mockedCommandLineParser );
        setValueForParam( HeadlessParamConstants.CONNECT_PARAMETER_SWITCH,
                          ipAdress + HeadlessParamConstants.IP_AND_PORT_SEPARATOR + port,
                          mockedCommandLineParser );
        setValueForParam( HeadlessParamConstants.DESTINATION_FILE_PREFIX_PARAMETER_SWITCH,
                          prefixName,
                          mockedCommandLineParser );
        setValueForParam( HeadlessParamConstants.DESTINATION_PATH_PARAMETER_SWITCH, path, mockedCommandLineParser );

        Mockito.when( mockedCommandLineParser.hasArg( HeadlessParamConstants.HELP_PARAMETER_SWITCH ) )
                .thenReturn( true );

        HeadlessParamsParser sut = new HeadlessParamsParser( mockedCommandLineParser );
        HeadlessParams params = sut.getParams();

        Assert.assertEquals( scriptFileName, params.scriptName );
        Assert.assertEquals( params.fileToLoadName, datafileName );
        Assert.assertEquals( params.targetIpAdress, ipAdress );
        Assert.assertEquals( params.targetPort, port );
        Assert.assertEquals( params.saveToFilePrefix, prefixName );
        Assert.assertEquals( params.saveToFilePath, path );
        Assert.assertTrue( params.isHelpSwitchSet );
    }

    private void setValueForParam(String param, String value, CommandLineParser mockedCommandLineParser)
            throws ValueOfArgumentMissingException
    {
        Mockito.when( mockedCommandLineParser.getValue( param ) ).thenReturn( value );
    }

    private void setExceptionForParam(String param, CommandLineParser mockedCommandLineParser)
            throws ValueOfArgumentMissingException
    {
        Mockito.doThrow( new ValueOfArgumentMissingException( param ) ).when( mockedCommandLineParser )
                .getValue( param );
    }
}
