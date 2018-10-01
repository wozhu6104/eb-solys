/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.dbus.decoder;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.elektrobit.ebrace.dbus.decoder.api.DBusDecoderConstants;
import com.elektrobit.ebrace.dbus.decoder.api.SignatureSummaryBuilder;
import com.elektrobit.ebsolys.core.targetdata.api.decoder.DecodedNode;
import com.elektrobit.ebsolys.decoder.common.api.GenericDecodedTree;

public class DecodeRuntimeEventSummaryBuilderTest
{
    private GenericDecodedTree srcTree;

    @SuppressWarnings("unused")
    @Before
    public void setup()
    {
        srcTree = new GenericDecodedTree( "trace.dbus.sessionbus" );
        DecodedNode firstNode = srcTree.getRootNode()
                .createChildNode( "org.genivi.navigationcore.MapMatchedPosition.GetPosition" );
        DecodedNode messageTypeNode = firstNode.createChildNode( DBusDecoderConstants.MESSAGE_TYPE, "Response" );
        DecodedNode mapNode = firstNode.createChildNode( "DBUS_MSG_PARAM_TYPE_ARRAY" );
        DecodedNode dictEntryNode = mapNode.createChildNode( "DBUS_MSG_PARAM_TYPE_DICT_ENTRY" );
        DecodedNode dictEntryKeyNode = dictEntryNode.createChildNode( "DBUS_MSG_PARAM_TYPE_INT32", "160" );
        DecodedNode dictEntryValueStructNode = dictEntryNode.createChildNode( "DBUS_MSG_PARAM_TYPE_STRUCT" );
        DecodedNode variantTypeNode = dictEntryValueStructNode.createChildNode( "DBUS_MSG_PARAM_TYPE_BYTE", "0" );
        DecodedNode variantNode = dictEntryValueStructNode.createChildNode( "DBUS_MSG_PARAM_TYPE_VARIANT" );
        DecodedNode variantValueNode = variantNode.createChildNode( "DBUS_MSG_PARAM_TYPE_DOUBLE", "47.42716650652187" );
    }

    @Test
    public void isSummaryCorrect() throws Exception
    {
        assertEquals( "org.genivi.navigationcore.MapMatchedPosition.GetPosition(DBUS_MSG_PARAM_TYPE_ARRAY)",
                      SignatureSummaryBuilder.createSummary( srcTree ) );
    }
}
