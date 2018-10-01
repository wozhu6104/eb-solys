/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.common.swt;

public class SearchNextPreviousProvider
{
    private int indexToHighlight = 0;
    private int limit = 0;

    public void next()
    {
        if (indexToHighlight == limit)
        {
            indexToHighlight = 0;
        }
        else
        {
            indexToHighlight++;
        }
    }

    public void previous()
    {
        if (indexToHighlight == 0)
        {
            indexToHighlight = limit;
        }
        else
        {
            indexToHighlight--;
        }
    }

    public int getHighlightIndex()
    {
        return indexToHighlight;
    }

    public void setLimit(int _limit)
    {
        indexToHighlight = 0;
        limit = _limit;
    }

    public void setIndexToHighlight(int _indexToHighlight)
    {
        indexToHighlight = _indexToHighlight;
    }
}
