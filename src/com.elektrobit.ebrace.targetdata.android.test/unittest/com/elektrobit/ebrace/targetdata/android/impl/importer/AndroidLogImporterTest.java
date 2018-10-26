/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.targetdata.android.impl.importer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import com.elektrobit.ebrace.common.utils.FileHelper;
import com.elektrobit.ebrace.common.utils.JsonHelper;
import com.elektrobit.ebrace.targetdata.adapter.androidlog.AndroidLogTAProto.OutputFormat;
import com.elektrobit.ebrace.targetdata.android.impl.common.AndroidLogParserFactory;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventAcceptor;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.RuntimeEventChannel;
import com.elektrobit.ebsolys.core.targetdata.api.runtime.eventhandling.Unit;

public class AndroidLogImporterTest
{
    private static Calendar cal = null;

    private final static String SAMPLE_LOG_TIME = "05-25 11:10:24.157 I/SystemServer( 1346): Backup Service";
    private final static String SAMPLE_LOG_THREADTIME = "05-25 11:10:24.157  1346  1346 I SystemServer: Backup Service";
    private final static String SAMPLE_LOG_LONG = "[ 05-25 11:10:24.157  1346: 1346 I/SystemServer ]";

    private final static String CHANNEL_ASSERT = "trace.android.logs.assert";
    private final static String CHANNEL_DEBUG = "trace.android.logs.debug";
    private final static String CHANNEL_ERROR = "trace.android.logs.error";
    private final static String CHANNEL_INFO = "trace.android.logs.info";
    private final static String CHANNEL_VERBOSE = "trace.android.logs.verbose";
    private final static String CHANNEL_WARNING = "trace.android.logs.warning";

    @BeforeClass
    public static void setup()
    {
        cal = Calendar.getInstance();
        cal.set( Calendar.YEAR, 1970 );
        cal.set( Calendar.MONTH, Calendar.MAY );
        cal.set( Calendar.DAY_OF_MONTH, 25 );
        cal.set( Calendar.HOUR_OF_DAY, 11 );
        cal.set( Calendar.MINUTE, 10 );
        cal.set( Calendar.SECOND, 24 );
        cal.set( Calendar.MILLISECOND, 157 );

    }

    @Test
    public void testGetParser() throws Exception
    {
        Assert.assertTrue( AndroidLogParserFactory.createParser( SAMPLE_LOG_TIME,
                                                                 null ) instanceof AndroidLogLineParserTime );

        Assert.assertTrue( AndroidLogParserFactory.createParser( SAMPLE_LOG_THREADTIME,
                                                                 null ) instanceof AndroidLogLineParserThreadtime );

        Assert.assertTrue( AndroidLogParserFactory.createParser( SAMPLE_LOG_LONG,
                                                                 null ) instanceof AndroidLogLineParserLong );

        Assert.assertNull( AndroidLogParserFactory.createParser( "Some excellent rubbish", null ) );
    }

    @Test
    public void testParserTime() throws Exception
    {
        AndroidLogLineParserAbstract p = AndroidLogParserFactory.createParser( SAMPLE_LOG_TIME, null );

        Assert.assertNotNull( p.getMatcher( SAMPLE_LOG_TIME ) );
        Assert.assertTrue( p.getMatcher( SAMPLE_LOG_TIME ).matches() );

        Matcher matcher = p.getMatcher( SAMPLE_LOG_TIME );
        if (matcher.matches())
        {
            Assert.assertEquals( 'I', p.getLogLevel( matcher ) );
            Assert.assertEquals( cal.getTimeInMillis() * 1000, p.getTimeStamp( matcher ) );
            Assert.assertEquals( "I/SystemServer( 1346): Backup Service", p.getValue( matcher, null ) );
        }

    }

    @Test
    public void testParserThreadTime() throws Exception
    {
        AndroidLogLineParserAbstract p = AndroidLogParserFactory.createParser( SAMPLE_LOG_THREADTIME, null );

        Assert.assertNotNull( p.getMatcher( SAMPLE_LOG_THREADTIME ) );
        Assert.assertTrue( p.getMatcher( SAMPLE_LOG_THREADTIME ).matches() );

        Matcher matcher = p.getMatcher( SAMPLE_LOG_THREADTIME );
        if (matcher.matches())
        {
            Assert.assertEquals( 'I', p.getLogLevel( matcher ) );
            Assert.assertEquals( cal.getTimeInMillis() * 1000, p.getTimeStamp( matcher ) );

            String jsonString = p.getValue( matcher, null );
            Assert.assertEquals( "1346", JsonHelper.getFieldFromJsonString( jsonString, "PID" ) );
            Assert.assertEquals( "1346", JsonHelper.getFieldFromJsonString( jsonString, "TID" ) );
            Assert.assertEquals( "SystemServer", JsonHelper.getFieldFromJsonString( jsonString, "Tag" ) );
            Assert.assertEquals( "Backup Service", JsonHelper.getFieldFromJsonString( jsonString, "Value" ) );
        }

    }

    @Test
    public void testParserLong() throws Exception
    {
        AndroidLogLineParserAbstract p = AndroidLogParserFactory.createParser( SAMPLE_LOG_LONG, null );

        Assert.assertNotNull( p.getMatcher( SAMPLE_LOG_LONG ) );
        Assert.assertTrue( p.getMatcher( SAMPLE_LOG_LONG ).matches() );

        Matcher matcher = p.getMatcher( SAMPLE_LOG_LONG );
        if (matcher.matches())
        {
            Assert.assertEquals( 'I', p.getLogLevel( matcher ) );
            Assert.assertEquals( cal.getTimeInMillis() * 1000, p.getTimeStamp( matcher ) );
            // Assert.assertEquals( "1346 1346 I SystemServer: Backup Service", p.getValue( matcher, null ) );
        }

    }

    @Test
    public void testAcceptMessageTime()
    {

        RuntimeEventAcceptor runtimeEventAcceptor = Mockito.mock( RuntimeEventAcceptor.class );
        @SuppressWarnings("unchecked")
        RuntimeEventChannel<String> channel = Mockito.mock( RuntimeEventChannel.class );

        Mockito.when( runtimeEventAcceptor
                .createOrGetRuntimeEventChannel( Mockito.anyString(), Mockito.eq( Unit.TEXT ), Mockito.anyString() ) )
                .thenReturn( channel );

        AndroidLogLineParserAbstract parser = AndroidLogParserFactory.createParser( OutputFormat.TIME,
                                                                                    runtimeEventAcceptor );

        Matcher m = parser.getMatcher( SAMPLE_LOG_TIME );
        if (m.matches())
        {
            Assert.assertTrue( parser
                    .acceptMessage( parser.getTimeStamp( m ), parser.getLogLevel( m ), parser.getValue( m, null ) ) );
            Mockito.verify( runtimeEventAcceptor ).createOrGetRuntimeEventChannel( CHANNEL_INFO, Unit.TEXT, "" );
        }
    }

    @Test
    public void testAcceptMessageThreadTime()
    {

        RuntimeEventAcceptor runtimeEventAcceptor = Mockito.mock( RuntimeEventAcceptor.class );
        @SuppressWarnings("unchecked")
        RuntimeEventChannel<String> channel = Mockito.mock( RuntimeEventChannel.class );
        List<String> columns = Arrays.asList( "PID", "TID", "Tag", "Value" );

        Mockito.when( runtimeEventAcceptor.createOrGetRuntimeEventChannel( Mockito.anyString(),
                                                                           Mockito.eq( Unit.TEXT ),
                                                                           Mockito.anyString(),
                                                                           Mockito.anyListOf( String.class ) ) )
                .thenReturn( channel );

        AndroidLogLineParserAbstract parser = AndroidLogParserFactory.createParser( OutputFormat.THREADTIME,
                                                                                    runtimeEventAcceptor );

        Matcher m = parser.getMatcher( SAMPLE_LOG_THREADTIME );
        if (m.matches())
        {
            Assert.assertTrue( parser
                    .acceptMessage( parser.getTimeStamp( m ), parser.getLogLevel( m ), parser.getValue( m, null ) ) );
            Mockito.verify( runtimeEventAcceptor )
                    .createOrGetRuntimeEventChannel( CHANNEL_INFO, Unit.TEXT, "", columns );
        }
    }

    @Test
    public void testAcceptMessageLong() throws IOException
    {

        RuntimeEventAcceptor runtimeEventAcceptor = Mockito.mock( RuntimeEventAcceptor.class );
        @SuppressWarnings("unchecked")
        RuntimeEventChannel<String> channel = Mockito.mock( RuntimeEventChannel.class );
        BufferedReader reader = Mockito.mock( BufferedReader.class );

        Mockito.when( runtimeEventAcceptor
                .createOrGetRuntimeEventChannel( Mockito.anyString(), Mockito.eq( Unit.TEXT ), Mockito.anyString() ) )
                .thenReturn( channel );

        Mockito.when( reader.readLine() ).thenReturn( "" );

        AndroidLogLineParserAbstract parser = AndroidLogParserFactory.createParser( OutputFormat.LONG,
                                                                                    runtimeEventAcceptor );

        Matcher m = parser.getMatcher( SAMPLE_LOG_LONG );
        if (m.matches())
        {
            // parser.acceptMessage( parser.getTimeStamp( m ), parser.getLogLevel( m ), parser.getValue( m, null ) );
            Assert.assertTrue( parser
                    .acceptMessage( parser.getTimeStamp( m ), parser.getLogLevel( m ), parser.getValue( m, reader ) ) );
            Mockito.verify( runtimeEventAcceptor ).createOrGetRuntimeEventChannel( CHANNEL_INFO, Unit.TEXT, "" );
        }
    }

    @Test
    public void testProcessLine() throws IOException
    {

        RuntimeEventAcceptor runtimeEventAcceptor = Mockito.mock( RuntimeEventAcceptor.class );
        RuntimeEventAcceptor runtimeEventAcceptorJson = Mockito.mock( RuntimeEventAcceptor.class );
        @SuppressWarnings("unchecked")
        RuntimeEventChannel<String> channel = Mockito.mock( RuntimeEventChannel.class );
        BufferedReader reader = Mockito.mock( BufferedReader.class );

        Mockito.when( runtimeEventAcceptor
                .createOrGetRuntimeEventChannel( Mockito.anyString(), Mockito.eq( Unit.TEXT ), Mockito.anyString() ) )
                .thenReturn( channel );

        Mockito.when( runtimeEventAcceptorJson.createOrGetRuntimeEventChannel( Mockito.anyString(),
                                                                               Mockito.eq( Unit.TEXT ),
                                                                               Mockito.anyString(),
                                                                               Mockito.anyListOf( String.class ) ) )
                .thenReturn( channel );

        AndroidLogLineParserAbstract parser1 = AndroidLogParserFactory.createParser( OutputFormat.THREADTIME,
                                                                                     runtimeEventAcceptorJson );

        Assert.assertTrue( parser1.processLine( SAMPLE_LOG_THREADTIME, reader, null ) );

        AndroidLogLineParserAbstract parser2 = AndroidLogParserFactory.createParser( OutputFormat.TIME,
                                                                                     runtimeEventAcceptor );

        Assert.assertTrue( parser2.processLine( SAMPLE_LOG_TIME, reader, null ) );

        AndroidLogLineParserAbstract parser3 = AndroidLogParserFactory.createParser( OutputFormat.LONG,
                                                                                     runtimeEventAcceptor );

        Assert.assertTrue( parser3.processLine( SAMPLE_LOG_LONG, reader, null ) );

    }

    @Test
    public void testAcceptMessageAllChannels()
    {
        RuntimeEventAcceptor runtimeEventAcceptor = Mockito.mock( RuntimeEventAcceptor.class );
        @SuppressWarnings("unchecked")
        RuntimeEventChannel<String> channel = Mockito.mock( RuntimeEventChannel.class );
        List<String> columns = Arrays.asList( "PID", "TID", "Tag", "Value" );
        Mockito.when( runtimeEventAcceptor.createOrGetRuntimeEventChannel( Mockito.anyString(),
                                                                           Mockito.eq( Unit.TEXT ),
                                                                           Mockito.anyString(),
                                                                           Mockito.anyListOf( String.class ) ) )
                .thenReturn( channel );

        AndroidLogLineParserAbstract parser = AndroidLogParserFactory.createParser( OutputFormat.THREADTIME,
                                                                                    runtimeEventAcceptor );

        Assert.assertTrue( parser.acceptMessage( 1000, 'A', "" ) );
        Mockito.verify( runtimeEventAcceptor ).createOrGetRuntimeEventChannel( CHANNEL_ASSERT, Unit.TEXT, "", columns );
        Assert.assertTrue( parser.acceptMessage( 1000, 'D', "" ) );
        Mockito.verify( runtimeEventAcceptor ).createOrGetRuntimeEventChannel( CHANNEL_DEBUG, Unit.TEXT, "", columns );
        Assert.assertTrue( parser.acceptMessage( 1000, 'E', "" ) );
        Mockito.verify( runtimeEventAcceptor ).createOrGetRuntimeEventChannel( CHANNEL_ERROR, Unit.TEXT, "", columns );
        Assert.assertTrue( parser.acceptMessage( 1000, 'I', "" ) );
        Mockito.verify( runtimeEventAcceptor ).createOrGetRuntimeEventChannel( CHANNEL_INFO, Unit.TEXT, "", columns );
        Assert.assertTrue( parser.acceptMessage( 1000, 'V', "" ) );
        Mockito.verify( runtimeEventAcceptor )
                .createOrGetRuntimeEventChannel( CHANNEL_VERBOSE, Unit.TEXT, "", columns );
        Assert.assertTrue( parser.acceptMessage( 1000, 'W', "" ) );
        Mockito.verify( runtimeEventAcceptor )
                .createOrGetRuntimeEventChannel( CHANNEL_WARNING, Unit.TEXT, "", columns );

        Assert.assertFalse( parser.acceptMessage( -1, 'X', "" ) );

    }

    @Test
    public void testProcessLineWithFileThreadTime() throws IOException
    {
        String pathToBundleRootFolder = FileHelper.getBundleRootFolderOfClass( this.getClass() );
        BufferedReader reader = new BufferedReader( new FileReader( new File( pathToBundleRootFolder
                + "testdata/a_thread_time.logcat" ) ) );
        String line = "";
        RuntimeEventAcceptor runtimeEventAcceptor = Mockito.mock( RuntimeEventAcceptor.class );
        @SuppressWarnings("unchecked")
        RuntimeEventChannel<String> channel = Mockito.mock( RuntimeEventChannel.class );

        Mockito.when( runtimeEventAcceptor.createOrGetRuntimeEventChannel( Mockito.anyString(),
                                                                           Mockito.eq( Unit.TEXT ),
                                                                           Mockito.anyString(),
                                                                           Mockito.anyListOf( String.class ) ) )
                .thenReturn( channel );

        AndroidLogLineParserAbstract parser = AndroidLogParserFactory.createParser( OutputFormat.THREADTIME,
                                                                                    runtimeEventAcceptor );

        while (line != null)
        {
            line = reader.readLine();

            if (line != null && line.length() > 0 && parser.getMatcher( line ).matches())
            {
                Assert.assertTrue( parser.processLine( line, reader, null ) );
            }
        }

        reader.close();
    }

    @Test
    public void testProcessLineWithFileTime() throws IOException
    {
        String pathToBundleRootFolder = FileHelper.getBundleRootFolderOfClass( this.getClass() );
        BufferedReader reader = new BufferedReader( new FileReader( new File( pathToBundleRootFolder
                + "testdata/a_time.logcat" ) ) );
        String line = "";
        RuntimeEventAcceptor runtimeEventAcceptor = Mockito.mock( RuntimeEventAcceptor.class );
        @SuppressWarnings("unchecked")
        RuntimeEventChannel<String> channel = Mockito.mock( RuntimeEventChannel.class );

        Mockito.when( runtimeEventAcceptor
                .createOrGetRuntimeEventChannel( Mockito.anyString(), Mockito.eq( Unit.TEXT ), Mockito.anyString() ) )
                .thenReturn( channel );

        AndroidLogLineParserAbstract parser = AndroidLogParserFactory.createParser( OutputFormat.TIME,
                                                                                    runtimeEventAcceptor );

        while (line != null)
        {
            line = reader.readLine();

            if (line != null && line.length() > 0 && parser.getMatcher( line ).matches())
            {
                Assert.assertTrue( parser.processLine( line, reader, null ) );
            }
        }

        reader.close();
    }

    @Test
    public void testProcessLineWithFileLong() throws IOException
    {
        String pathToBundleRootFolder = FileHelper.getBundleRootFolderOfClass( this.getClass() );
        BufferedReader reader = new BufferedReader( new FileReader( new File( pathToBundleRootFolder
                + "testdata/a_long.logcat" ) ) );
        String line = "";
        RuntimeEventAcceptor runtimeEventAcceptor = Mockito.mock( RuntimeEventAcceptor.class );
        @SuppressWarnings("unchecked")
        RuntimeEventChannel<String> channel = Mockito.mock( RuntimeEventChannel.class );

        Mockito.when( runtimeEventAcceptor
                .createOrGetRuntimeEventChannel( Mockito.anyString(), Mockito.eq( Unit.TEXT ), Mockito.anyString() ) )
                .thenReturn( channel );

        AndroidLogLineParserAbstract parser = AndroidLogParserFactory.createParser( OutputFormat.LONG,
                                                                                    runtimeEventAcceptor );

        while (line != null)
        {
            line = reader.readLine();

            if (line != null && line.length() > 0 && parser.getMatcher( line ).matches())
            {
                Assert.assertTrue( parser.processLine( line, reader, null ) );
            }
        }

        reader.close();
    }

    @Test
    public void testGetChannel()
    {

        RuntimeEventAcceptor runtimeEventAcceptor = Mockito.mock( RuntimeEventAcceptor.class );
        @SuppressWarnings("unchecked")
        RuntimeEventChannel<String> channel = Mockito.mock( RuntimeEventChannel.class );

        Mockito.when( runtimeEventAcceptor.createOrGetRuntimeEventChannel( Mockito.anyString(),
                                                                           Mockito.eq( Unit.TEXT ),
                                                                           Mockito.anyString(),
                                                                           Mockito.anyListOf( String.class ) ) )
                .thenReturn( channel );

        AndroidLogLineParserAbstract parser = AndroidLogParserFactory.createParser( OutputFormat.THREADTIME,
                                                                                    runtimeEventAcceptor );

        parser.getChannel( 'A' );
        Mockito.verify( runtimeEventAcceptor )
                .createOrGetRuntimeEventChannel( CHANNEL_ASSERT, Unit.TEXT, "", parser.getColumns() );
        parser.getChannel( 'I' );
        Mockito.verify( runtimeEventAcceptor )
                .createOrGetRuntimeEventChannel( CHANNEL_INFO, Unit.TEXT, "", parser.getColumns() );
        parser.getChannel( 'D' );
        Mockito.verify( runtimeEventAcceptor )
                .createOrGetRuntimeEventChannel( CHANNEL_DEBUG, Unit.TEXT, "", parser.getColumns() );
        parser.getChannel( 'W' );
        Mockito.verify( runtimeEventAcceptor )
                .createOrGetRuntimeEventChannel( CHANNEL_WARNING, Unit.TEXT, "", parser.getColumns() );
        parser.getChannel( 'E' );
        Mockito.verify( runtimeEventAcceptor )
                .createOrGetRuntimeEventChannel( CHANNEL_ERROR, Unit.TEXT, "", parser.getColumns() );
        parser.getChannel( 'V' );
        Mockito.verify( runtimeEventAcceptor )
                .createOrGetRuntimeEventChannel( CHANNEL_VERBOSE, Unit.TEXT, "", parser.getColumns() );

    }

}
