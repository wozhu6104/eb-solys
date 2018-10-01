/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.preferences.util;

import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Composite;

public class MinMaxFieldEditor extends FieldEditor implements IPropertyChangeListener
{

    private static final String DEFAULT_MIN_KEY = "";
    private static final String DEFAULT_MAX_KEY = "";

    private IntegerFieldEditor minEditor;
    private IntegerFieldEditor maxEditor;

    private boolean isValid;
    private String errorMessage;
    private final String minText;
    private final String maxText;
    private final String minKey;
    private final String maxKey;

    public MinMaxFieldEditor(Composite parent, String minText, String maxText, String minKey, String maxKey)
    {
        this.minText = minText;
        this.maxText = maxText;
        this.minKey = minKey;
        this.maxKey = maxKey;
        createControl( parent );
    }

    public MinMaxFieldEditor(Composite parent, String minText, String maxText)
    {
        this( parent, minText, maxText, DEFAULT_MIN_KEY, DEFAULT_MAX_KEY );
    }

    @Override
    protected void doLoad()
    {
        minEditor.load();
        maxEditor.load();
    }

    @Override
    protected void doLoadDefault()
    {
        minEditor.loadDefault();
        maxEditor.loadDefault();
    }

    @Override
    protected void doStore()
    {
        minEditor.store();
        maxEditor.store();
    }

    @Override
    protected void doFillIntoGrid(Composite parent, int numColumns)
    {
        minEditor = new IntegerFieldEditor( minKey, minText, parent );
        minEditor.setPropertyChangeListener( this );
        maxEditor = new IntegerFieldEditor( maxKey, maxText, parent );
        maxEditor.setPropertyChangeListener( this );
    }

    @Override
    public void setPreferenceStore(IPreferenceStore store)
    {
        minEditor.setPreferenceStore( store );
        maxEditor.setPreferenceStore( store );

        super.setPreferenceStore( store );
    }

    @Override
    public int getNumberOfControls()
    {
        return 2;
    }

    public void setMinValue(int minValue)
    {
        minEditor.setStringValue( String.valueOf( minValue ) );
    }

    public void setMaxValue(int maxValue)
    {
        maxEditor.setStringValue( String.valueOf( maxValue ) );
    }

    public int getMinValue()
    {
        return minEditor.getIntValue();
    }

    public int getMaxValue()
    {
        return maxEditor.getIntValue();
    }

    public void setErrorMessage(String errorMessage)
    {
        this.errorMessage = errorMessage;
    }

    @Override
    public void setPage(DialogPage dialogPage)
    {
        minEditor.setPage( dialogPage );
        maxEditor.setPage( dialogPage );
        super.setPage( dialogPage );
    }

    @Override
    public boolean isValid()
    {
        return isValid;
    }

    @Override
    protected void refreshValidState()
    {
        isValid = checkState();
    }

    private boolean checkState()
    {
        return isBothEditorsValid() && isMinLessThanMax();
    }

    private boolean isMinLessThanMax()
    {
        return getMinValue() < getMaxValue();
    }

    private boolean isBothEditorsValid()
    {
        return minEditor.isValid() && maxEditor.isValid();
    }

    @Override
    public void propertyChange(PropertyChangeEvent event)
    {
        if (event.getProperty().equals( FieldEditor.VALUE ))
        {
            verfyValues();
        }
    }

    private void verfyValues()
    {
        if (isBothEditorsValid())
        {
            if (!isMinLessThanMax())
            {
                showErrorMessage( errorMessage );
                ((PreferencePage)getPage()).setValid( false );
            }
            else
            {
                clearErrorMessage();
                ((PreferencePage)getPage()).setValid( true );
            }
        }
        else
        {
            ((PreferencePage)getPage()).setValid( false );
        }
    }

    @Override
    protected void adjustForNumColumns(int numColumns)
    {
    }

}
