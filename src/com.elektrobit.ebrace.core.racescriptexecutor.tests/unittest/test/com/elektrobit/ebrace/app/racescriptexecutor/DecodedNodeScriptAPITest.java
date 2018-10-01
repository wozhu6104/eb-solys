/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.app.racescriptexecutor;

import static java.util.Arrays.asList;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.elektrobit.ebrace.app.racescriptexecutor.impl.service.ScriptContextImpl;
import com.elektrobit.ebrace.resources.api.manager.ResourcesModelManager;
import com.elektrobit.ebsolys.core.targetdata.api.decoder.DecodedNode;

public class DecodedNodeScriptAPITest
{
    ScriptContextImpl raceScriptContextImpl;
    DecodedNode decodedRootNodeMock = Mockito.mock( DecodedNode.class );
    DecodedNode decodedChildNodeMock1 = Mockito.mock( DecodedNode.class );
    DecodedNode decodedChildNodeMock2 = Mockito.mock( DecodedNode.class );

    String rootNodeName = "RootNode";
    String childNodeName = "ChildNode";

    String rootNodeValue = "RootNodeValue";
    String childNodeValue1 = "ChildNodeValue1";
    String childNodeValue2 = "ChildNodeValue2";

    String wrongKey = "WrongKey";

    @Before
    public void setup()
    {
        ResourcesModelManager mockedModelManager = Mockito.mock( ResourcesModelManager.class );
        raceScriptContextImpl = new ScriptContextImpl( null,
                                                       null,
                                                       null,
                                                       null,
                                                       null,
                                                       null,
                                                       null,
                                                       mockedModelManager,
                                                       null,
                                                       null,
                                                       null,
                                                       null,
                                                       null,
                                                       null );

        Mockito.when( decodedRootNodeMock.getName() ).thenReturn( rootNodeName );
        Mockito.when( decodedRootNodeMock.getValue() ).thenReturn( rootNodeValue );

        Mockito.when( decodedChildNodeMock1.getName() ).thenReturn( childNodeName );
        Mockito.when( decodedChildNodeMock1.getValue() ).thenReturn( childNodeValue1 );

        Mockito.when( decodedChildNodeMock2.getName() ).thenReturn( childNodeName );
        Mockito.when( decodedChildNodeMock2.getValue() ).thenReturn( childNodeValue2 );

        List<DecodedNode> childNodesList = asList( decodedChildNodeMock1, decodedChildNodeMock2 );
        Mockito.when( decodedRootNodeMock.getChildren() ).thenReturn( childNodesList );
    }

    @Test
    public void getFirstMatchingValueTest()
    {
        String firstMatchingValue = raceScriptContextImpl.getFirstValue( decodedRootNodeMock, wrongKey );
        Assert.assertNull( firstMatchingValue );

        firstMatchingValue = raceScriptContextImpl.getFirstValue( decodedRootNodeMock, rootNodeName );
        Assert.assertEquals( rootNodeValue, firstMatchingValue );

        firstMatchingValue = raceScriptContextImpl.getFirstValue( decodedRootNodeMock, childNodeName );
        Assert.assertEquals( childNodeValue1, firstMatchingValue );
    }

    @Test
    public void searchAllMatchingValuesForKeyTest()
    {
        List<String> allMatchedValues = raceScriptContextImpl.getValues( decodedRootNodeMock, wrongKey );
        Assert.assertTrue( allMatchedValues.isEmpty() );

        allMatchedValues = raceScriptContextImpl.getValues( decodedRootNodeMock, rootNodeName );
        Assert.assertEquals( 1, allMatchedValues.size() );
        Assert.assertTrue( allMatchedValues.contains( rootNodeValue ) );

        allMatchedValues = raceScriptContextImpl.getValues( decodedRootNodeMock, childNodeName );
        Assert.assertEquals( 2, allMatchedValues.size() );
        Assert.assertTrue( allMatchedValues.contains( childNodeValue1 ) );
        Assert.assertTrue( allMatchedValues.contains( childNodeValue2 ) );
    }

    @Test
    public void doesKeyExistsTest()
    {
        boolean doesKeyExist = raceScriptContextImpl.keyExists( decodedRootNodeMock, wrongKey );
        Assert.assertFalse( doesKeyExist );

        doesKeyExist = raceScriptContextImpl.keyExists( decodedRootNodeMock, rootNodeName );
        Assert.assertTrue( doesKeyExist );

        doesKeyExist = raceScriptContextImpl.keyExists( decodedRootNodeMock, childNodeName );
        Assert.assertTrue( doesKeyExist );
    }

    @Test
    public void getAmountOfKeysTest()
    {
        int amountOfKeys = raceScriptContextImpl.numberOfKeys( decodedRootNodeMock, wrongKey );
        Assert.assertEquals( 0, amountOfKeys );

        amountOfKeys = raceScriptContextImpl.numberOfKeys( decodedRootNodeMock, rootNodeName );
        Assert.assertEquals( 1, amountOfKeys );

        amountOfKeys = raceScriptContextImpl.numberOfKeys( decodedRootNodeMock, childNodeName );
        Assert.assertEquals( 2, amountOfKeys );
    }
}
