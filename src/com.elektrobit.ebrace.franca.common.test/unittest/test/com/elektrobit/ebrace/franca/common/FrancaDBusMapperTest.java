/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.franca.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import com.elektrobit.ebrace.dbus.decoder.api.DBusDecoderConstants;
import com.elektrobit.ebrace.franca.common.franca.mapper.api.FrancaDBusDecodedRuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.decoder.DecodedNode;
import com.elektrobit.ebsolys.core.targetdata.api.decoder.DecodedRuntimeEvent;
import com.elektrobit.ebsolys.core.targetdata.api.decoder.DecodedTree;
import com.elektrobit.ebsolys.decoder.common.api.GenericDecodedTree;

public class FrancaDBusMapperTest
{
    private FrancaDBusDecodedRuntimeEvent francaDecodedEvent;

    @SuppressWarnings("unused")
    @Before
    public void setup()
    {

        DecodedTree tree = new GenericDecodedTree( "trace.dbus.sessionbus" );
        DecodedNode interfaceNode = tree.getRootNode()
                .createChildNode( "org.genivi.navigation.navigationcore.MapMatchedPosition.GetPosition" );
        DecodedNode metaDataNode = tree.getRootNode().createChildNode( "MetaData" );
        DecodedNode messageTypeNode = metaDataNode.createChildNode( DBusDecoderConstants.MESSAGE_TYPE, "Response" );

        DecodedNode errorNode = interfaceNode.createChildNode( "DBUS_MSG_PARAM_TYPE_ERROR", "0" );
        DecodedNode mapNode = interfaceNode.createChildNode( "DBUS_MSG_PARAM_TYPE_ARRAY" );
        DecodedNode dictEntryNode = mapNode.createChildNode( "DBUS_MSG_PARAM_TYPE_DICT_ENTRY" );
        DecodedNode dictEntryKeyNode = dictEntryNode.createChildNode( "DBUS_MSG_PARAM_TYPE_INT32", "160" );
        DecodedNode dictEntryValueStructNode = dictEntryNode.createChildNode( "DBUS_MSG_PARAM_TYPE_STRUCT" );
        DecodedNode variantTypeNode = dictEntryValueStructNode.createChildNode( "DBUS_MSG_PARAM_TYPE_BYTE", "0" );
        DecodedNode variantNode = dictEntryValueStructNode.createChildNode( "DBUS_MSG_PARAM_TYPE_VARIANT" );
        DecodedNode variantValueNode = variantNode.createChildNode( "DBUS_MSG_PARAM_TYPE_DOUBLE", "47.42716650652187" );

        DecodedRuntimeEvent decodedRuntimeEvent = mock( DecodedRuntimeEvent.class );
        when( decodedRuntimeEvent.getDecodedTree() ).thenReturn( tree );

        francaDecodedEvent = new FrancaDBusDecodedRuntimeEvent( FrancaModelLoaderHelper.loadFrancaDefaultModels(),
                                                                decodedRuntimeEvent );

    }

    @Test
    public void isRootNodeCorrect() throws Exception
    {
        DecodedNode rootNode = francaDecodedEvent.getDecodedTree().getRootNode();

        assertEquals( "trace.dbus.sessionbus", rootNode.getName() );
        assertNull( rootNode.getValue() );
    }

    @Test
    public void isFirstNodeCorrect() throws Exception
    {
        DecodedNode firstNode = francaDecodedEvent.getDecodedTree().getRootNode().getChildren().get( 0 );

        assertEquals( "org.genivi.navigation.navigationcore.MapMatchedPosition.GetPosition", firstNode.getName() );
        assertNull( firstNode.getValue() );
    }

    @Test
    public void isMessageTypeCorrect() throws Exception
    {
        DecodedNode messageTypeNode = francaDecodedEvent.getDecodedTree().getRootNode().getChildren().get( 1 )
                .getChildren().get( 0 );

        assertEquals( DBusDecoderConstants.MESSAGE_TYPE, messageTypeNode.getName() );
        assertEquals( "Response", messageTypeNode.getValue() );
    }

    @Test
    public void isErrorValueCorrect() throws Exception
    {
        DecodedNode positionNode = francaDecodedEvent.getDecodedTree().getRootNode().getChildren().get( 0 )
                .getChildren().get( 0 );

        assertEquals( "result", positionNode.getName() );
        assertEquals( "OK", positionNode.getValue() );
    }

    @Test
    public void isPositionDictCorrect() throws Exception
    {
        DecodedNode positionNode = francaDecodedEvent.getDecodedTree().getRootNode().getChildren().get( 0 )
                .getChildren().get( 1 );

        assertEquals( "position", positionNode.getName() );
        assertNull( positionNode.getValue() );
    }

    @Test
    public void isPositionDictItem1Correct() throws Exception
    {
        DecodedNode positionItemDictNode = francaDecodedEvent.getDecodedTree().getRootNode().getChildren().get( 0 )
                .getChildren().get( 1 ).getChildren().get( 0 );

        assertEquals( "PositionItemDict", positionItemDictNode.getName() );
        assertNull( positionItemDictNode.getValue() );
    }

    @Test
    public void isPositionDictItem1KeyCorrect() throws Exception
    {
        DecodedNode positionItemDictKeyNode = francaDecodedEvent.getDecodedTree().getRootNode().getChildren().get( 0 )
                .getChildren().get( 1 ).getChildren().get( 0 ).getChildren().get( 0 );

        assertEquals( "PositionItemDict", positionItemDictKeyNode.getName() );
        assertEquals( "LATITUDE", positionItemDictKeyNode.getValue() );
    }

    @Test
    public void isPositionDictItem1ValueUnionCorrect() throws Exception
    {
        DecodedNode positionItemDictValueUnionNode = francaDecodedEvent.getDecodedTree().getRootNode().getChildren()
                .get( 0 ).getChildren().get( 1 ).getChildren().get( 0 ).getChildren().get( 1 );

        assertEquals( "PositionItemValue", positionItemDictValueUnionNode.getName() );
        assertNull( positionItemDictValueUnionNode.getValue() );
    }

    @Test
    public void isPositionDictItem1ValueUnionContentCorrect() throws Exception
    {
        DecodedNode positionItemDictValueUnionContentNode = francaDecodedEvent.getDecodedTree().getRootNode()
                .getChildren().get( 0 ).getChildren().get( 1 ).getChildren().get( 0 ).getChildren().get( 1 )
                .getChildren().get( 0 );

        assertEquals( "doubleValue", positionItemDictValueUnionContentNode.getName() );
        assertEquals( "47.42716650652187", positionItemDictValueUnionContentNode.getValue() );
    }

}
