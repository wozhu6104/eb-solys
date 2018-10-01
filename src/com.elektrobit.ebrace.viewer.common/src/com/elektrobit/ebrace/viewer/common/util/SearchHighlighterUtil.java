/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.common.util;

import java.util.List;

import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;

import com.elektrobit.ebrace.core.interactor.api.table.Position;

public class SearchHighlighterUtil
{
    public static StyleRange[] getStyleRanges(List<Position> positionList, Color colorToHighlight)
    {
        StyleRange[] styleRangeArr = new StyleRange[positionList.size()];
        int index = 0;
        for (Position p : positionList)
        {
            StyleRange myStyledRange = new StyleRange( p.getStart(), p.getLength(), null, colorToHighlight );
            styleRangeArr[index] = myStyledRange;
            index++;
        }
        return styleRangeArr;
    }
}
