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
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.elektrobit.ebrace.core.interactor.api.table.Position;
import com.elektrobit.ebrace.core.interactor.api.table.RowFormatter;
import com.elektrobit.ebrace.core.interactor.api.tableinput.TableData;
import com.elektrobit.ebrace.core.interactor.tableinput.TableDataImpl;

public class FilterUtil
{
    private FilterUtil()
    {
    }

    public static TableData filter(List<?> input, String filterString, List<RowFormatter> rowFormatters)
    {
        List<String> notIgnoredWords = SplitSearchStringUtil.getNotIgnoredWordsList( filterString );
        List<String> ignoredWords = SplitSearchStringUtil.getIgnoredWordsList( filterString );
        if (ignoredWords.isEmpty() && notIgnoredWords.isEmpty())
        {
            return new TableDataImpl( input, Collections.<Object, Map<RowFormatter, List<Position>>> emptyMap() );
        }
        else
        {
            List<Object> result = new ArrayList<Object>();
            Map<Object, Map<RowFormatter, List<Position>>> highlighting = new LinkedHashMap<Object, Map<RowFormatter, List<Position>>>();
            for (Object item : input)
            {
                boolean addToResultList = true;
                Map<RowFormatter, List<Position>> searchPositionsByColumn = new HashMap<RowFormatter, List<Position>>();
                if (!notIgnoredWords.isEmpty())
                {
                    for (String word : notIgnoredWords)
                    {
                        List<Position> searchPositionsOfWord = new ArrayList<Position>();
                        for (RowFormatter rowFormatter : rowFormatters)
                        {
                            String text = rowFormatter.getText( item );
                            text = text.toLowerCase();
                            List<Position> positionsOfString = StringSearchUtil.getPositionsOfString( text, word );

                            if (!positionsOfString.isEmpty()
                                    && !StringSearchUtil.containsExcludedWord( text, ignoredWords ))
                            {
                                searchPositionsOfWord.addAll( positionsOfString );
                                if (searchPositionsByColumn.containsKey( rowFormatter ))
                                {
                                    searchPositionsByColumn.get( rowFormatter ).addAll( positionsOfString );
                                }
                                else
                                {
                                    searchPositionsByColumn.put( rowFormatter, positionsOfString );
                                }
                            }
                        }
                        if (searchPositionsOfWord.isEmpty())
                        {
                            addToResultList = false;
                            break;
                        }
                    }
                }
                else
                {
                    for (RowFormatter rowFormatter : rowFormatters)
                    {
                        String text = rowFormatter.getText( item );
                        text = text.toLowerCase();

                        if (StringSearchUtil.containsExcludedWord( text, ignoredWords ))
                        {
                            addToResultList = false;
                            break;
                        }
                    }
                }
                if (addToResultList)
                {
                    result.add( item );
                    highlighting.put( item, searchPositionsByColumn );
                }
            }
            return new TableDataImpl( result, highlighting );
        }
    }

    public static TableData search(List<?> input, String filterString, List<RowFormatter> rowFormatters)
    {
        TableData filterResult = filter( input, filterString, rowFormatters );
        return new TableDataImpl( input, filterResult.getSearchPositionMap() );
    }
}
