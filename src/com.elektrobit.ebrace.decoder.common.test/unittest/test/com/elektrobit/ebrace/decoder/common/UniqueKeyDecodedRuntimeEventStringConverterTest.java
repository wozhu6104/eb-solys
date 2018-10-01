/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.decoder.common;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.elektrobit.ebsolys.core.targetdata.api.decoder.DecodedNode;
import com.elektrobit.ebsolys.core.targetdata.api.decoder.DecodedTree;
import com.elektrobit.ebsolys.decoder.common.api.GenericDecodedTree;
import com.elektrobit.ebsolys.decoder.common.converter.impl.UniqueKeyDecodedRuntimeEventStringConverterImpl;

import junit.framework.Assert;

public class UniqueKeyDecodedRuntimeEventStringConverterTest
{
    private static final String COMPLEX_JSON_TREE = "{\"rootNodeID_0\":{\"Message Type_1\":\"Broadcast\",\"DBUS_MSG_PARAM_TYPE_STRUCT_2\":{\"DBUS_MSG_PARAM_TYPE_STRUCT_3\":{\"DBUS_MSG_PARAM_TYPE_UINT32_4\":\"351\"},\"DBUS_MSG_PARAM_TYPE_STRUCT_5\":{\"DBUS_MSG_PARAM_TYPE_INT32_6\":\"138020541\",\"DBUS_MSG_PARAM_TYPE_INT32_7\":\"574944544\"}},\"DBUS_MSG_PARAM_TYPE_INT32_8\":\"123\"}}";
    private static final String SIMPLE_JSON_TREE = "{\"rootNodeID_0\":{\"Message Type_1\":\"Broadcast\"}}";
    private static DecodedTree COMPLEX_DECODED_TREE = null;
    private static DecodedTree SIMPLE_DECODED_TREE = null;
    private UniqueKeyDecodedRuntimeEventStringConverterImpl converter = null;

    @BeforeClass
    public static void initialize()
    {

        COMPLEX_DECODED_TREE = new GenericDecodedTree( "rootNodeID" );
        DecodedNode rootNode = COMPLEX_DECODED_TREE.getRootNode();
        rootNode.createChildNode( "Message Type", "Broadcast" );
        DecodedNode child2 = rootNode.createChildNode( "DBUS_MSG_PARAM_TYPE_STRUCT" );
        DecodedNode child2child1 = child2.createChildNode( "DBUS_MSG_PARAM_TYPE_STRUCT" );
        child2child1.createChildNode( "DBUS_MSG_PARAM_TYPE_UINT32", "351" );
        DecodedNode child2child2 = child2.createChildNode( "DBUS_MSG_PARAM_TYPE_STRUCT" );
        child2child2.createChildNode( "DBUS_MSG_PARAM_TYPE_INT32", "138020541" );
        child2child2.createChildNode( "DBUS_MSG_PARAM_TYPE_INT32", "574944544" );
        rootNode.createChildNode( "DBUS_MSG_PARAM_TYPE_INT32", "123" );

        SIMPLE_DECODED_TREE = new GenericDecodedTree( "rootNodeID" );
        SIMPLE_DECODED_TREE.getRootNode().createChildNode( "Message Type", "Broadcast" );
    }

    @Before
    public void initializeConverter()
    {
        converter = new UniqueKeyDecodedRuntimeEventStringConverterImpl();
    }

    @Test
    public void conversionToStringWorks() throws Exception
    {
        Assert.assertEquals( COMPLEX_JSON_TREE, converter.convertToString( COMPLEX_DECODED_TREE ) );
    }

    @Test
    public void conversionToSimpleStringWorks() throws Exception
    {
        Assert.assertEquals( SIMPLE_JSON_TREE, converter.convertToString( SIMPLE_DECODED_TREE ) );
    }

    @Test
    public void conversionToSimpleTreeWorks() throws Exception
    {
        Assert.assertEquals( SIMPLE_DECODED_TREE, converter.convertFromString( SIMPLE_JSON_TREE ) );
    }

    @Test
    public void conversionToTreeWorks() throws Exception
    {
        Assert.assertEquals( COMPLEX_DECODED_TREE, converter.convertFromString( COMPLEX_JSON_TREE ) );
    }
}
