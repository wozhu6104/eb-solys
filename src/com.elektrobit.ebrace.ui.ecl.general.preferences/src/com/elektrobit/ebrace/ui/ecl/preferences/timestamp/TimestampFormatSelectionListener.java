/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.ui.ecl.preferences.timestamp;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;

public class TimestampFormatSelectionListener implements SelectionListener
{
    private final String pattern;
    private final TimeFormatPreferencesPage page;

    public TimestampFormatSelectionListener(String pattern, TimeFormatPreferencesPage page)
    {
        this.pattern = pattern;
        this.page = page;
    }

    @Override
    public void widgetSelected(SelectionEvent event)
    {
        page.isValid();
        page.updateApply();
        page.toggleEnabledCustomTimestampFormat();
        Button button = ((Button)event.widget);
        if (button.getSelection())
        {
            page.getContainer().updateButtons();

            if (button.getText().equalsIgnoreCase( TimestampFormatPreferencesConstants.CUSTOM_TIMESTAMP_FORMAT_LABEL ))
            {
                return;
            }
            page.setSelectedOption( pattern );
        }
    }

    @Override
    public void widgetDefaultSelected(SelectionEvent e)
    {
    }
}
