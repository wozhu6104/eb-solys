/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.interactor.tableinput.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.elektrobit.ebrace.core.interactor.api.table.Position;

public class StringSearchUtil
{
    public static List<Position> getPositionsOfString(String text, String searchWord)
    {
        List<Position> positionList = new ArrayList<Position>();
        Pattern p = Pattern.compile( searchWord, Pattern.LITERAL );
        Matcher m = p.matcher( text.toLowerCase() );
        while (m.find())
        {
            Position position = new Position( m.start(), searchWord.length() );
            positionList.add( position );
        }
        return positionList;
    }

    public static boolean containsExcludedWord(String text, List<String> excludedFilteredWords)
    {
        for (String word : excludedFilteredWords)
        {
            if (text.contains( word ))
                return true;
        }
        return false;
    }
}
