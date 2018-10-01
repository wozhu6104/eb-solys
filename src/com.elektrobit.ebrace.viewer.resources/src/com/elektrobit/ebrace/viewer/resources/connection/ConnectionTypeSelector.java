/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.resources.connection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import com.elektrobit.ebrace.core.interactor.api.resources.model.connection.ConnectionType;

public class ConnectionTypeSelector
{
    private final List<ConnectionType> allConnectionTypes;
    private Map<ConnectionType, Button> typeToButtonMap;
    private final SelectionListener selectionListener;

    public ConnectionTypeSelector(List<ConnectionType> allConnectionTypes, SelectionListener listener)
    {
        this.allConnectionTypes = allConnectionTypes;
        this.selectionListener = listener;
    }

    public List<Button> initializeMap(Composite parent)
    {
        typeToButtonMap = new HashMap<ConnectionType, Button>();

        for (ConnectionType type : allConnectionTypes)
        {
            typeToButtonMap.put( type, createRadioButton( parent, type.getName(), 1 ) );
        }
        return new ArrayList<Button>( typeToButtonMap.values() );
    }

    private Button createRadioButton(Composite parent, String text, int horizontalSpan)
    {
        Button radioButton = new Button( parent, SWT.RADIO );
        radioButton.setText( text );
        radioButton.addSelectionListener( selectionListener );
        return radioButton;
    }

    public void selectByType(ConnectionType selectedConnectionType)
    {
        if (selectedConnectionType != null)
        {
            for (Entry<ConnectionType, Button> set : typeToButtonMap.entrySet())
            {
                if (set.getKey().getName().equals( selectedConnectionType.getName() ))
                {
                    set.getValue().setSelection( true );
                }
            }
        }
        else
        {
            selectDefault();
        }
    }

    public ConnectionType selectedType()
    {
        ConnectionType selected = null;
        for (Entry<ConnectionType, Button> set : typeToButtonMap.entrySet())
        {
            if (set.getValue().getSelection())
            {
                return set.getKey();
            }
        }
        return selected;
    }

    public ConnectionType selectDefault()
    {
        Optional<ConnectionType> type = typeToButtonMap.keySet().stream().findFirst();
        Button button = typeToButtonMap.get( type.get() );
        button.setSelection( true );
        return type.get();
    }
}
