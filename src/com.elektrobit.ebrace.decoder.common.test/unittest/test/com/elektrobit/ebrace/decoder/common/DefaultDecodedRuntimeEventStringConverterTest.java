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

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.elektrobit.ebsolys.core.targetdata.api.decoder.DecodedNode;
import com.elektrobit.ebsolys.core.targetdata.api.decoder.DecodedTree;
import com.elektrobit.ebsolys.decoder.common.converter.impl.DefaultDecodedRuntimeEventStringConverterImpl;

import junit.framework.Assert;

public class DefaultDecodedRuntimeEventStringConverterTest
{
    private DefaultDecodedRuntimeEventStringConverterImpl converter = null;
    private final String testStr_01 = "{\"src\":\"unknown\",\"target\":\"RouteGuidance\",\"name\":\"SO_AliveRequest\",\"params\":{}}";
    private final String testStr_02 = "{\"src\":\"SRCore\",\"target\":\"ISRHmi\",\"name\":\"SRS_ResultsAvailable\",\"params\":{\"nBestList\":[\"please London Johnson private\",\"London Johnson private\",\"please London Thompson private\",\"London Thompson private\",\"please Lily Taylor private\",\"Lily Taylor private\"],\"ConfidenceList\":[4590,4532,3984,3874,3821,3667],\"TagList\":[\"1\",\"21\",\"50\"],\"AttrList\":[\"@=0\"]}}";
    private final String testStr_03 = "{\"src\":\"SRCore\",\"target\":\"ISRHmi\",\"name\":\"SRS_ResultsAvailable\",\"params\":[\"please London Johnson private\",[\"London Johnson private\",[\"please London Thompson private\", {\"key\":\"value\"}]]]}";
    private DecodedTree decodedTree_01 = null;
    private DecodedTree decodedTree_02 = null;
    private DecodedTree decodedTree_03 = null;

    private final int paramsIndex = 3;

    @Before
    public void initializeConverter()
    {
        converter = new DefaultDecodedRuntimeEventStringConverterImpl();
        decodedTree_01 = converter.convertFromString( testStr_01 );
        decodedTree_02 = converter.convertFromString( testStr_02 );
        decodedTree_03 = converter.convertFromString( testStr_03 );
    }

    @Test
    public void rootNodeIsFake()
    {
        Assert.assertEquals( "FAKE_ROOT", decodedTree_01.getRootNode().getName() );
    }

    @Test
    public void hasFourChildren()
    {
        Assert.assertEquals( 4, decodedTree_01.getRootNode().getChildren().size() );
    }

    @Test
    public void validateChildAtIndexZero()
    {
        validateChildHelper( 0, "src", "unknown" );
    }

    @Test
    public void validateChildAtIndexOne()
    {
        validateChildHelper( 1, "target", "RouteGuidance" );
    }

    @Test
    public void validateChildAtIndexTwo()
    {
        validateChildHelper( 2, "name", "SO_AliveRequest" );
    }

    @Test
    public void validateChildAtIndexThree()
    {
        validateChildHelper( paramsIndex, "params", null );
    }

    public void validateChildHelper(final int childIndex, final String expectedKeyStr, final String expectedValueStr)
    {
        DecodedNode decodedNode = decodedTree_01.getRootNode().getChildren().get( childIndex );
        Assert.assertEquals( expectedKeyStr, decodedNode.getName() );
        Assert.assertEquals( expectedValueStr, decodedNode.getValue() );
    }

    @Test
    public void validateArrayNrOfChildren()
    {
        DecodedNode decodedNode = decodedTree_02.getRootNode().getChildren().get( paramsIndex );
        Assert.assertEquals( 4, decodedNode.getChildren().size() );
        Assert.assertEquals( 6, decodedNode.getChildren().get( 0 ).getChildren().size() );
    }

    @Test
    public void validateArrayValuesOfChildren()
    {
        List<DecodedNode> decodedNodeArrayChildren = decodedTree_02.getRootNode().getChildren().get( paramsIndex )
                .getChildren().get( 0 ).getChildren();
        List<String> expectedArryChildrenValues = Arrays.asList( "please London Johnson private",
                                                                 "London Johnson private",
                                                                 "please London Thompson private",
                                                                 "London Thompson private",
                                                                 "please Lily Taylor private",
                                                                 "Lily Taylor private" );
        Assert.assertEquals( expectedArryChildrenValues.size(), decodedNodeArrayChildren.size() );
        for (int i = 0; i < expectedArryChildrenValues.size(); i++)
        {
            Assert.assertEquals( expectedArryChildrenValues.get( i ), decodedNodeArrayChildren.get( i ).getValue() );
        }
    }

    @Test
    public void validateNestedArraysFirstLevel()
    {
        List<DecodedNode> decodedNodeArrayChildren = decodedTree_03.getRootNode().getChildren().get( paramsIndex )
                .getChildren();
        Assert.assertEquals( 2, decodedNodeArrayChildren.size() );
        Assert.assertEquals( "<0>", decodedNodeArrayChildren.get( 0 ).getName() );
        Assert.assertEquals( "please London Johnson private", decodedNodeArrayChildren.get( 0 ).getValue() );
    }

    @Test
    public void validateNestedArraysSecondLevel()
    {
        List<DecodedNode> decodedNodeArrayChildren = decodedTree_03.getRootNode().getChildren().get( paramsIndex )
                .getChildren().get( 1 ).getChildren();
        Assert.assertEquals( 2, decodedNodeArrayChildren.size() );
        Assert.assertEquals( "<0>", decodedNodeArrayChildren.get( 0 ).getName() );
        Assert.assertEquals( "London Johnson private", decodedNodeArrayChildren.get( 0 ).getValue() );
    }

    @Test
    public void validateNestedArraysThirdLevel()
    {
        List<DecodedNode> decodedNodeArrayChildren = decodedTree_03.getRootNode().getChildren().get( paramsIndex )
                .getChildren().get( 1 ).getChildren().get( 1 ).getChildren();
        Assert.assertEquals( 2, decodedNodeArrayChildren.size() );
        Assert.assertEquals( "<0>", decodedNodeArrayChildren.get( 0 ).getName() );
        Assert.assertEquals( "please London Thompson private", decodedNodeArrayChildren.get( 0 ).getValue() );
        Assert.assertEquals( "key", decodedNodeArrayChildren.get( 1 ).getChildren().get( 0 ).getName() );
        Assert.assertEquals( "value", decodedNodeArrayChildren.get( 1 ).getChildren().get( 0 ).getValue() );
    }
}
