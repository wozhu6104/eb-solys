/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.script.wizard;

import org.junit.Test;

public class ScriptSourceGeneratorTest
{
    // private static final String SCRIPT_NAME = "TestScriptName";

    @Test
    public void dummyTest() throws Exception
    {

    }

    // @Ignore
    // @Test
    // public void TestGlobalContextNoBeforeNoAfter()
    // {
    // String scriptSource = ScriptSourceGenerator.generateClassContent( SCRIPT_NAME,
    // ScriptContext.GLOBAL_CONTEXT,
    // false,
    // false );
    //
    // assertAllTagsRemoved( scriptSource );
    //
    // Assert.assertTrue( scriptSource.contains( SCRIPT_NAME ) );
    // Assert.assertFalse( scriptSource.contains( ScriptSourceGenerator.IMPORT_ANNOTATION_BEFORE_RACE_SCRIPT ) );
    // Assert.assertFalse( scriptSource.contains( ScriptSourceGenerator.IMPORT_ANNOTATION_AFTER_RACE_SCRIPT ) );
    // Assert.assertFalse( scriptSource.contains( ScriptSourceGenerator.IMPORT_RUNTIME_EVENT_CHANNEL ) );
    // Assert.assertFalse( scriptSource.contains( ScriptSourceGenerator.IMPORT_RUNTIME_EVENT_LIST ) );
    // Assert.assertFalse( scriptSource.contains( ScriptSourceGenerator.IMPORT_TIME_MARKER ) );
    //
    // Assert.assertFalse( scriptSource.contains( getBeforeMethodSource() ) );
    // Assert.assertFalse( scriptSource.contains( getAfterMethodSource() ) );
    // Assert.assertTrue( scriptSource.contains( getGlobalExecuteMethodSource() ) );
    // Assert.assertFalse( scriptSource.contains( getTimeMarkerMethodSource() ) );
    // Assert.assertFalse( scriptSource.contains( getEventListMethodSource() ) );
    // Assert.assertFalse( scriptSource.contains( getChannelExecuteMethodSource() ) );
    // }
    //
    // @Ignore
    // @Test
    // public void TestGlobalContextYesBeforeNoAfter()
    // {
    // String scriptSource = ScriptSourceGenerator.generateClassContent( SCRIPT_NAME,
    // ScriptContext.GLOBAL_CONTEXT,
    // true,
    // false );
    //
    // assertAllTagsRemoved( scriptSource );
    //
    // Assert.assertTrue( scriptSource.contains( SCRIPT_NAME ) );
    // Assert.assertTrue( scriptSource.contains( ScriptSourceGenerator.IMPORT_ANNOTATION_BEFORE_RACE_SCRIPT ) );
    // Assert.assertFalse( scriptSource.contains( ScriptSourceGenerator.IMPORT_ANNOTATION_AFTER_RACE_SCRIPT ) );
    // Assert.assertFalse( scriptSource.contains( ScriptSourceGenerator.IMPORT_RUNTIME_EVENT_CHANNEL ) );
    // Assert.assertFalse( scriptSource.contains( ScriptSourceGenerator.IMPORT_RUNTIME_EVENT_LIST ) );
    // Assert.assertFalse( scriptSource.contains( ScriptSourceGenerator.IMPORT_TIME_MARKER ) );
    //
    // Assert.assertTrue( scriptSource.contains( getBeforeMethodSource() ) );
    // Assert.assertFalse( scriptSource.contains( getAfterMethodSource() ) );
    // Assert.assertTrue( scriptSource.contains( getGlobalExecuteMethodSource() ) );
    // Assert.assertFalse( scriptSource.contains( getTimeMarkerMethodSource() ) );
    // Assert.assertFalse( scriptSource.contains( getEventListMethodSource() ) );
    // Assert.assertFalse( scriptSource.contains( getChannelExecuteMethodSource() ) );
    // }
    //
    // @Ignore
    // @Test
    // public void TestGlobalContextYesBeforeYesAfter()
    // {
    // String scriptSource = ScriptSourceGenerator.generateClassContent( SCRIPT_NAME,
    // ScriptContext.GLOBAL_CONTEXT,
    // true,
    // true );
    //
    // assertAllTagsRemoved( scriptSource );
    //
    // Assert.assertTrue( scriptSource.contains( SCRIPT_NAME ) );
    // Assert.assertTrue( scriptSource.contains( ScriptSourceGenerator.IMPORT_ANNOTATION_BEFORE_RACE_SCRIPT ) );
    // Assert.assertTrue( scriptSource.contains( ScriptSourceGenerator.IMPORT_ANNOTATION_AFTER_RACE_SCRIPT ) );
    // Assert.assertFalse( scriptSource.contains( ScriptSourceGenerator.IMPORT_RUNTIME_EVENT_CHANNEL ) );
    // Assert.assertFalse( scriptSource.contains( ScriptSourceGenerator.IMPORT_RUNTIME_EVENT_LIST ) );
    // Assert.assertFalse( scriptSource.contains( ScriptSourceGenerator.IMPORT_TIME_MARKER ) );
    //
    // Assert.assertTrue( scriptSource.contains( getBeforeMethodSource() ) );
    // Assert.assertTrue( scriptSource.contains( getAfterMethodSource() ) );
    // Assert.assertTrue( scriptSource.contains( getGlobalExecuteMethodSource() ) );
    // Assert.assertFalse( scriptSource.contains( getTimeMarkerMethodSource() ) );
    // Assert.assertFalse( scriptSource.contains( getEventListMethodSource() ) );
    // Assert.assertFalse( scriptSource.contains( getChannelExecuteMethodSource() ) );
    // }
    //
    // @Ignore
    // @Test
    // public void TestChannelContextNoBeforeNoAfter()
    // {
    // String scriptSource = ScriptSourceGenerator.generateClassContent( SCRIPT_NAME,
    // ScriptContext.CHANNEL_CONTEXT,
    // false,
    // false );
    //
    // assertAllTagsRemoved( scriptSource );
    //
    // Assert.assertTrue( scriptSource.contains( SCRIPT_NAME ) );
    // Assert.assertFalse( scriptSource.contains( ScriptSourceGenerator.IMPORT_ANNOTATION_BEFORE_RACE_SCRIPT ) );
    // Assert.assertFalse( scriptSource.contains( ScriptSourceGenerator.IMPORT_ANNOTATION_AFTER_RACE_SCRIPT ) );
    // Assert.assertTrue( scriptSource.contains( ScriptSourceGenerator.IMPORT_RUNTIME_EVENT_CHANNEL ) );
    // Assert.assertFalse( scriptSource.contains( ScriptSourceGenerator.IMPORT_RUNTIME_EVENT_LIST ) );
    // Assert.assertFalse( scriptSource.contains( ScriptSourceGenerator.IMPORT_TIME_MARKER ) );
    //
    // Assert.assertFalse( scriptSource.contains( getBeforeMethodSource() ) );
    // Assert.assertFalse( scriptSource.contains( getAfterMethodSource() ) );
    // Assert.assertFalse( scriptSource.contains( getGlobalExecuteMethodSource() ) );
    // Assert.assertFalse( scriptSource.contains( getTimeMarkerMethodSource() ) );
    // Assert.assertFalse( scriptSource.contains( getEventListMethodSource() ) );
    // Assert.assertTrue( scriptSource.contains( getChannelExecuteMethodSource() ) );
    // }
    //
    // @Ignore
    // @Test
    // public void TestEventListContextNoBeforeNoAfter()
    // {
    // String scriptSource = ScriptSourceGenerator
    // .generateClassContent( SCRIPT_NAME, ScriptContext.EVENTLIST_CONTEXT, false, false );
    //
    // assertAllTagsRemoved( scriptSource );
    //
    // Assert.assertTrue( scriptSource.contains( SCRIPT_NAME ) );
    // Assert.assertFalse( scriptSource.contains( ScriptSourceGenerator.IMPORT_ANNOTATION_BEFORE_RACE_SCRIPT ) );
    // Assert.assertFalse( scriptSource.contains( ScriptSourceGenerator.IMPORT_ANNOTATION_AFTER_RACE_SCRIPT ) );
    // Assert.assertFalse( scriptSource.contains( ScriptSourceGenerator.IMPORT_RUNTIME_EVENT_CHANNEL ) );
    // Assert.assertTrue( scriptSource.contains( ScriptSourceGenerator.IMPORT_RUNTIME_EVENT_LIST ) );
    // Assert.assertFalse( scriptSource.contains( ScriptSourceGenerator.IMPORT_TIME_MARKER ) );
    //
    // Assert.assertFalse( scriptSource.contains( getBeforeMethodSource() ) );
    // Assert.assertFalse( scriptSource.contains( getAfterMethodSource() ) );
    // Assert.assertFalse( scriptSource.contains( getGlobalExecuteMethodSource() ) );
    // Assert.assertFalse( scriptSource.contains( getTimeMarkerMethodSource() ) );
    // Assert.assertTrue( scriptSource.contains( getEventListMethodSource() ) );
    // Assert.assertFalse( scriptSource.contains( getChannelExecuteMethodSource() ) );
    // }
    //
    // @Ignore
    // @Test
    // public void TestTimeMarkerContextNoBeforeNoAfter()
    // {
    // String scriptSource = ScriptSourceGenerator
    // .generateClassContent( SCRIPT_NAME, ScriptContext.TIMEMARKER_CONTEXT, false, false );
    //
    // assertAllTagsRemoved( scriptSource );
    //
    // Assert.assertTrue( scriptSource.contains( SCRIPT_NAME ) );
    // Assert.assertFalse( scriptSource.contains( ScriptSourceGenerator.IMPORT_ANNOTATION_BEFORE_RACE_SCRIPT ) );
    // Assert.assertFalse( scriptSource.contains( ScriptSourceGenerator.IMPORT_ANNOTATION_AFTER_RACE_SCRIPT ) );
    // Assert.assertFalse( scriptSource.contains( ScriptSourceGenerator.IMPORT_RUNTIME_EVENT_CHANNEL ) );
    // Assert.assertFalse( scriptSource.contains( ScriptSourceGenerator.IMPORT_RUNTIME_EVENT_LIST ) );
    // Assert.assertTrue( scriptSource.contains( ScriptSourceGenerator.IMPORT_TIME_MARKER ) );
    //
    // Assert.assertFalse( scriptSource.contains( getBeforeMethodSource() ) );
    // Assert.assertFalse( scriptSource.contains( getAfterMethodSource() ) );
    // Assert.assertFalse( scriptSource.contains( getGlobalExecuteMethodSource() ) );
    // Assert.assertTrue( scriptSource.contains( getTimeMarkerMethodSource() ) );
    // Assert.assertFalse( scriptSource.contains( getEventListMethodSource() ) );
    // Assert.assertFalse( scriptSource.contains( getChannelExecuteMethodSource() ) );
    // }
    //
    // private void assertAllTagsRemoved(String scriptSource)
    // {
    // Assert.assertFalse( scriptSource.contains( ScriptSourceGenerator.TAG_AFTER_ANNOTATION_IMPORT ) );
    // Assert.assertFalse( scriptSource.contains( ScriptSourceGenerator.TAG_AFTER_METHOD ) );
    // Assert.assertFalse( scriptSource.contains( ScriptSourceGenerator.TAG_BEFORE_ANNOTATION_IMPORT ) );
    // Assert.assertFalse( scriptSource.contains( ScriptSourceGenerator.TAG_BEFORE_METHOD ) );
    // Assert.assertFalse( scriptSource.contains( ScriptSourceGenerator.TAG_CLASSNAME ) );
    // Assert.assertFalse( scriptSource.contains( ScriptSourceGenerator.TAG_EXECUTE_METHOD ) );
    // Assert.assertFalse( scriptSource.contains( ScriptSourceGenerator.TAG_EXECUTE_PARAM_IMPORT ) );
    // }
    //
    // private String getBeforeMethodSource()
    // {
    // return getStringFromFilePath( "resources/ScriptBeforeMethodTemplate.txt" );
    // }
    //
    // private String getAfterMethodSource()
    // {
    // return getStringFromFilePath( "resources/ScriptAfterMethodTemplate.txt" );
    // }
    //
    // private String getGlobalExecuteMethodSource()
    // {
    // return getStringFromFilePath( "resources/ScriptGlobalMethodTemplate.txt" );
    // }
    //
    // private String getChannelExecuteMethodSource()
    // {
    // return getStringFromFilePath( "resources/ScriptChannelMethodTemplate.txt" );
    // }
    //
    // private String getEventListMethodSource()
    // {
    // return getStringFromFilePath( "resources/ScriptEventListMethodTemplate.txt" );
    // }
    //
    // private String getTimeMarkerMethodSource()
    // {
    // return getStringFromFilePath( "resources/ScriptTimeMarkerMethodTemplate.txt" );
    // }
    //
    // private static String getStringFromFilePath(String path)
    // {
    // URI uri = FileHelper.locateFileInBundle( ResourcePlugin.PLUGIN_ID, path );
    // File file = new File( uri );
    // return getStringFromFile( file );
    // }
    //
    // private static String getStringFromFile(File file)
    // {
    // String result = null;
    // try
    // {
    // result = StringFromFileReaderHelper.stringFromFile( file );
    // }
    // catch (IOException e)
    // {
    // e.printStackTrace();
    // }
    // return result;
    // }
}
