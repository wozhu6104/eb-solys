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

import org.eclipse.swt.graphics.Color;

public interface ColorPreferences
{
    final int ANALYSIS_TIMESPAN_FILL_ALPHA = 15;
    final Color ANALYSIS_TIMESPAN_COLOR_RULER = new Color( null, 0, 0, 0 );
    final Color ANALYSIS_TIMESPAN_COLOR_RULER_WITHOUT_ALPHA = new Color( null, 240, 240, 240 );
    final Color TIMEMARKER_CELL_HIGHLIGHTED_BG_COLOR = new Color( null, 0, 235, 0 );
    final Color SEARCH_CELL_HIGHLIGHTED_BG_COLOR = new Color( null, 255, 255, 0 );
    final Color SELECTED_SEARCH_CELL_HIGHLIGHTED_BG_COLOR = new Color( null, 255, 220, 0 );
    final Color SEARCH_CELL_HIGHLIGHTED_BG_COLOR_RULER = new Color( null, 255, 194, 50 );
    final Color TIMEMARKER_COLOR = new Color( null, 0, 235, 0 );
    final Color TAGGED_EVENT_COLOR = new Color( null, 255, 0, 0 );
}
