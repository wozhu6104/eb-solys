/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.core.interactor.tableinput;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.elektrobit.ebrace.core.interactor.api.table.Position;
import com.elektrobit.ebrace.core.interactor.tableinput.filter.StringSearchUtil;

public class StringSearchUtilTest
{

    private static final String TEST_TEXT = "This is my event! With -some .special ::: chars";
    private static final String TEST_EXPRESSION_PLAIN_POSITIVE = "event!";
    private static final String TEST_EXPRESSION_PLAIN_NEGATIVE = "events";
    private static final String TEST_EXPRESSION_REGEX_POSITIVE = "-some\\s\\.special";
    private static final String TEST_EXPRESSION_REGEX_NEGATIVE = ":{4}";

    @Test
    public void searchWithRegExReturnsResult()
    {
        assertEquals( "-some .special",
                      positionsToStrings( StringSearchUtil
                              .getPositionsOfString( TEST_TEXT, TEST_EXPRESSION_REGEX_POSITIVE ) ).get( 0 ) );
    }

    @Test
    public void searchWithRegExReturnsNoResult()
    {
        assertEquals( 0, StringSearchUtil.getPositionsOfString( TEST_TEXT, TEST_EXPRESSION_REGEX_NEGATIVE ).size() );
    }

    private List<String> positionsToStrings(List<Position> results)
    {
        List<String> textResults = new ArrayList<String>();
        for (Position result : results)
        {
            textResults.add( TEST_TEXT.substring( result.getStart(), result.getStart() + result.getLength() ) );
        }
        return textResults;
    }

}
