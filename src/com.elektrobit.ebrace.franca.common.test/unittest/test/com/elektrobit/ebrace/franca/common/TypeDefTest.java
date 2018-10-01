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
import com.elektrobit.ebsolys.decoder.common.api.GenericDecodedTree;

public class TypeDefTest
{
    private FrancaDBusDecodedRuntimeEvent francaDecodedEvent;

    @SuppressWarnings("unused")
    @Before
    public void setup()
    {
        GenericDecodedTree srcTree = new GenericDecodedTree( "trace.dbus.sessionbus" );
        DecodedNode interfaceNode = srcTree.getRootNode()
                .createChildNode( "org.genivi.navigation.poiservice.POIContentAccess.AddCategories" );
        DecodedNode metaDataNode = srcTree.getRootNode().createChildNode( "MetaData" );
        DecodedNode messageTypeNode = metaDataNode.createChildNode( DBusDecoderConstants.MESSAGE_TYPE, "Response" );
        DecodedNode arrayNode = interfaceNode.createChildNode( "DBUS_MSG_PARAM_TYPE_ARRAY" );
        DecodedNode arrayContentNode = arrayNode.createChildNode( "DBUS_MSG_PARAM_TYPE_UINT32", "256" );

        DecodedRuntimeEvent decodedRuntimeEvent = mock( DecodedRuntimeEvent.class );
        when( decodedRuntimeEvent.getDecodedTree() ).thenReturn( srcTree );

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

        assertEquals( "org.genivi.navigation.poiservice.POIContentAccess.AddCategories", firstNode.getName() );
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
    public void isArrayNodeCorrect() throws Exception
    {
        DecodedNode arrayNode = francaDecodedEvent.getDecodedTree().getRootNode().getChildren().get( 0 ).getChildren()
                .get( 0 );

        assertEquals( "poiCategoriesId", arrayNode.getName() );
        assertNull( arrayNode.getValue() );
    }

    @Test
    public void isArrayEntry1Correct() throws Exception
    {
        DecodedNode arrayEntry1Node = francaDecodedEvent.getDecodedTree().getRootNode().getChildren().get( 0 )
                .getChildren().get( 0 ).getChildren().get( 0 );

        assertEquals( "CategoryID[0]", arrayEntry1Node.getName() );
        assertEquals( "256", arrayEntry1Node.getValue() );
    }
}
