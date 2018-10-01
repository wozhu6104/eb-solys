/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.script.wizard;

import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

import com.elektrobit.ebrace.core.interactor.api.script.ScriptConstants;
import com.elektrobit.ebrace.viewer.script.wizard.ScriptSourceGenerator.ScriptContext;

/*
 * In this wizard page, we have a workaround to make the radio buttons work.
 * The problem is, that the radio buttons are not all in the same composite.
 * But SWT requires them to be in the same composite to work automatically.
 * 
 * The workaround is: 
 * - Init the main composite with SWT.NO_RADIO_GROUP
 * - Have a selection listener for every button and set the selection to false 
 * 
 *
 */
public class NewScriptWizardContextPage extends WizardPage
{
    private static final String DIALOG_DESCRIPTION = "Please select a context in which the script will be executed. All scripts have access to all available runtime data as well as the full EB solys API.";

    private static final String LABEL_GLOBAL_CONTEXT = "Access all EB solys runtime data without preselection.\n\n";
    private static final String LABEL_FILTER_CONTEXT = "Filter table entries with extended logical flexibility. Filter scripts will be available in the search field above tables.\n\n";
    private static final String LABEL_PRESELECTION_CONTEXT = "Process data from a preselected data set. Preselection scripts can be executed from the context menu of the related GUI representation of the chosen item.\n\n";
    private static final String LABEL_CALLBACK_CONTEXT = "Process incoming events continuously. Callback scripts can be run with a live connection or before loading a file.\n\n";

    private static final String BUTTON_GLOBAL_CONTEXT = "Global context";
    private static final String BUTTON_FILTER_CONTEXT = "Filter Context";
    private static final String BUTTON_PRESELECTION_CONTEXT = "Preselection context";
    private static final String BUTTON_CALLBACK_CONTEXT = "Callback context";

    private static final String CHECKBOX_BEFORE_METHOD = "Generate @Before prepare method. This will be executed as first.";
    private static final String CHECKBOX_AFTER_METHOD = "Generate @After cleanup method. This will be executed after script is done.";
    private static final String CHECKBOX_COMMANDLINE_PARAMETER = "Generate variable that can be set via an argument from command line (for Auto mode)";

    private static final String[] COMBOBOX_PRESELECTION_ITEMS = new String[]{ScriptConstants.PRESELECTION_CHANNEL_LABEL,
            ScriptConstants.PRESELECTION_CHANNELS_LABEL, ScriptConstants.PRESELECTION_RUNTIME_EVENT_LABEL,
            ScriptConstants.PRESELECTION_EVENTS_LABEL, ScriptConstants.PRESELECTION_TIMEMARKER_LABEL,
            ScriptConstants.PRESELECTION_TIMEMAKRKERS_LABEL};

    private Button globalCtxBtn, preselectionCtxBtn, callbackCtxBtn, filterContextBtn;
    private Button beforeMethodCheckbox, afterMethodCheckbox, injectedParameterCheckbox;
    private CCombo preselectionCombo;

    private Font boldFont;

    protected NewScriptWizardContextPage(String pageName)
    {
        super( pageName );
        setTitle( ScriptConstants.TITLE );
        setDescription( DIALOG_DESCRIPTION );
    }

    private Composite composite;

    @Override
    public void createControl(Composite parent)
    {
        createAndLayoutComposite( parent );

        createGlobalContextButton();
        createPreselectionContextButton();
        createCallbackContextButton();
        createFilterContextButton();

        createBeforeAfterParameterCheckboxes();

        setControl( composite );
        setPageComplete( true );
    }

    private void createAndLayoutComposite(Composite parent)
    {
        this.composite = new Composite( parent, SWT.NONE | SWT.NO_RADIO_GROUP ); // WORKAROUND
        GridLayout layout = new GridLayout( 1, false );
        composite.setLayout( layout );
    }

    private void createGlobalContextButton()
    {
        globalCtxBtn = new Button( composite, SWT.RADIO );
        globalCtxBtn.setText( BUTTON_GLOBAL_CONTEXT );
        globalCtxBtn.setSelection( true );
        globalCtxBtn.setLayoutData( getTwoColumnGridData() );
        globalCtxBtn.addSelectionListener( getGlobalBtnListener() );
        initBoldFont( globalCtxBtn.getFont() );
        globalCtxBtn.setFont( boldFont );
        Label globalCtxLabel = new Label( composite, SWT.WRAP );
        globalCtxLabel.setText( LABEL_GLOBAL_CONTEXT );
        globalCtxLabel.setLayoutData( getTwoColumnGridData() );
    }

    private void createFilterContextButton()
    {
        filterContextBtn = new Button( composite, SWT.RADIO );
        filterContextBtn.setText( BUTTON_FILTER_CONTEXT );
        filterContextBtn.setLayoutData( getTwoColumnGridData() );
        filterContextBtn.addSelectionListener( getFilterButtonListener() );
        initBoldFont( filterContextBtn.getFont() );
        filterContextBtn.setFont( boldFont );
        Label filterContextLabel = new Label( composite, SWT.WRAP );
        filterContextLabel.setText( LABEL_FILTER_CONTEXT );
        filterContextLabel.setLayoutData( getTwoColumnGridData() );
    }

    private void createPreselectionContextButton()
    {
        Composite preselectionComposite = new Composite( composite, SWT.NONE );
        GridLayout prelesectionCellLayout = new GridLayout( 2, false );
        prelesectionCellLayout.marginLeft = -5;
        preselectionComposite.setLayout( prelesectionCellLayout );
        GridData gridData = getTwoColumnGridData();
        gridData.horizontalAlignment = SWT.LEFT;
        preselectionComposite.setLayoutData( gridData );

        preselectionCtxBtn = new Button( preselectionComposite, SWT.RADIO );
        preselectionCtxBtn.setText( BUTTON_PRESELECTION_CONTEXT );
        preselectionCtxBtn.addSelectionListener( getPreselectionBtnListener() );
        preselectionCtxBtn.setFont( boldFont );

        createPreselectionContextCombo( preselectionComposite );

        Label preselectionCtxLabel = new Label( composite, SWT.WRAP );
        preselectionCtxLabel.setText( LABEL_PRESELECTION_CONTEXT );
        preselectionCtxLabel.setLayoutData( getTwoColumnGridData() );
    }

    private void createPreselectionContextCombo(Composite composite)
    {
        preselectionCombo = new CCombo( composite, SWT.BORDER );
        preselectionCombo.setItems( COMBOBOX_PRESELECTION_ITEMS );
        preselectionCombo.select( 0 );
        preselectionCombo.setEditable( false );
        preselectionCombo.setBackground( Display.getCurrent().getSystemColor( SWT.COLOR_WHITE ) );
        preselectionCombo.addSelectionListener( new SelectionListener()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                preselectionCtxBtn.setSelection( true );
                globalCtxBtn.setSelection( false );
                callbackCtxBtn.setSelection( false );
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e)
            {
            }
        } );
    }

    private void createCallbackContextButton()
    {
        callbackCtxBtn = new Button( composite, SWT.RADIO );
        callbackCtxBtn.setText( BUTTON_CALLBACK_CONTEXT );
        callbackCtxBtn.setLayoutData( getTwoColumnGridData() );
        callbackCtxBtn.setFont( boldFont );
        callbackCtxBtn.addSelectionListener( getCallbackBtnListener() );
        Label callbackCtxLabel = new Label( composite, SWT.WRAP );
        callbackCtxLabel.setText( LABEL_CALLBACK_CONTEXT );
        callbackCtxLabel.setLayoutData( getTwoColumnGridData() );
    }

    private GridData getTwoColumnGridData()
    {
        GridData twoColumnGridData = new GridData();
        twoColumnGridData.horizontalSpan = 1;
        twoColumnGridData.widthHint = 550;
        return twoColumnGridData;
    }

    private void initBoldFont(Font font)
    {
        FontDescriptor boldDescriptor = FontDescriptor.createFrom( globalCtxBtn.getFont() ).setStyle( SWT.BOLD );
        boldFont = boldDescriptor.createFont( globalCtxBtn.getDisplay() );
    }

    private SelectionListener getGlobalBtnListener()
    {
        return new SelectionListener()
        {

            @Override
            public void widgetSelected(SelectionEvent e)
            {
                preselectionCtxBtn.setSelection( false );
                callbackCtxBtn.setSelection( false );
                filterContextBtn.setSelection( false );
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e)
            {
            }
        };
    }

    private SelectionListener getFilterButtonListener()
    {
        return new SelectionListener()
        {

            @Override
            public void widgetSelected(SelectionEvent e)
            {
                // WORKAROUND
                globalCtxBtn.setSelection( false );
                callbackCtxBtn.setSelection( false );
                preselectionCtxBtn.setSelection( false );

            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e)
            {
            }
        };

    }

    private SelectionListener getPreselectionBtnListener()
    {
        return new SelectionListener()
        {

            @Override
            public void widgetSelected(SelectionEvent e)
            {
                // WORKAROUND
                globalCtxBtn.setSelection( false );
                filterContextBtn.setSelection( false );
                callbackCtxBtn.setSelection( false );
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e)
            {
            }
        };
    }

    private SelectionListener getCallbackBtnListener()
    {
        return new SelectionListener()
        {

            @Override
            public void widgetSelected(SelectionEvent e)
            {
                // WORKAROUND
                globalCtxBtn.setSelection( false );
                filterContextBtn.setSelection( false );
                preselectionCtxBtn.setSelection( false );
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e)
            {
            }
        };
    }

    private void createBeforeAfterParameterCheckboxes()
    {
        beforeMethodCheckbox = new Button( composite, SWT.CHECK );
        beforeMethodCheckbox.setText( CHECKBOX_BEFORE_METHOD );
        beforeMethodCheckbox.setLayoutData( getTwoColumnGridData() );

        afterMethodCheckbox = new Button( composite, SWT.CHECK );
        afterMethodCheckbox.setText( CHECKBOX_AFTER_METHOD );
        afterMethodCheckbox.setLayoutData( getTwoColumnGridData() );

        injectedParameterCheckbox = new Button( composite, SWT.CHECK );
        injectedParameterCheckbox.setText( CHECKBOX_COMMANDLINE_PARAMETER );
        injectedParameterCheckbox.setLayoutData( getTwoColumnGridData() );
    }

    public boolean canFinish()
    {
        return true;
    }

    public ScriptContext getSelectionContext()
    {
        if (globalCtxBtn.getSelection())
        {
            return ScriptContext.GLOBAL_CONTEXT;
        }
        if (preselectionCtxBtn.getSelection())
        {
            int index = preselectionCombo.getSelectionIndex();
            String selection = preselectionCombo.getItem( index );

            if (ScriptConstants.PRESELECTION_CHANNEL_LABEL.equals( selection ))
            {
                return ScriptContext.CHANNEL_CONTEXT;
            }
            if (ScriptConstants.PRESELECTION_EVENTS_LABEL.equals( selection ))
            {
                return ScriptContext.EVENTLIST_CONTEXT;
            }
            if (ScriptConstants.PRESELECTION_TIMEMARKER_LABEL.equals( selection ))
            {
                return ScriptContext.TIMEMARKER_CONTEXT;
            }
            if (ScriptConstants.PRESELECTION_CHANNELS_LABEL.equals( selection ))
            {
                return ScriptContext.CHANNELS_CONTEXT;
            }
            if (ScriptConstants.PRESELECTION_RUNTIME_EVENT_LABEL.equals( selection ))
            {
                return ScriptContext.RUNTIMEEVNET_CONTEXT;
            }
            if (ScriptConstants.PRESELECTION_TIMEMAKRKERS_LABEL.equals( selection ))
            {
                return ScriptContext.TIMEMARKERS_CONTEXT;
            }
        }
        if (callbackCtxBtn.getSelection())
        {
            return ScriptContext.CALLBACK_CONTEXT;
        }

        if (filterContextBtn.getSelection())
        {
            return ScriptContext.FILTER_CONTEXT;
        }

        return null;
    }

    public boolean isBeforeMethodSelected()
    {
        return beforeMethodCheckbox.getSelection();
    }

    public boolean isAfterMethodSelected()
    {
        return afterMethodCheckbox.getSelection();
    }

    public boolean isInjectedParameterSelected()
    {
        return injectedParameterCheckbox.getSelection();
    }
}
