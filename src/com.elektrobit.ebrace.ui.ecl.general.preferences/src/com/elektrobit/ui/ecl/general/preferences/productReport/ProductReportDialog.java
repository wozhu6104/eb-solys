/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ui.ecl.general.preferences.productReport;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class ProductReportDialog extends PreferencePage implements IWorkbenchPreferencePage
{
    private static final String REPORT_MESSAGE_LABEL = "Help us improving our product!";

    private Button deniedButton;
    private Button acceptButton;
    private Button notifyButton;

    @Override
    protected Control createContents(Composite parent)
    {
        Composite content = new Composite( parent, SWT.NONE );
        GridLayout layout = new GridLayout();
        GridData gdata = new GridData( SWT.BEGINNING, SWT.BEGINNING, false, false );
        layout.numColumns = 1;
        layout.verticalSpacing = 50;

        content.setLayoutData( gdata );
        content.setLayout( layout );

        Label textLabel = new Label( content, SWT.NONE );
        textLabel.setText( REPORT_MESSAGE_LABEL );
        textLabel.setLayoutData( new GridData() );

        makeRadioButtons( content );

        return content;
    }

    private void makeRadioButtons(Composite parent)
    {

        GridData gridSubject = new GridData( SWT.FILL, SWT.BEGINNING, true, true );

        RowLayout rowLayout = new RowLayout( SWT.VERTICAL );
        rowLayout.spacing = 10;

        Group group = new Group( parent, SWT.SHADOW_IN );
        group.setText( "Please select one of this options" );
        group.setLayout( rowLayout );
        group.setLayoutData( gridSubject );

        deniedButton = new Button( group, SWT.RADIO );
        deniedButton.setText( "Never send reports" );

        acceptButton = new Button( group, SWT.RADIO );
        acceptButton.setText( "Send reports automatically" );

        notifyButton = new Button( group, SWT.RADIO );
        notifyButton.setText( "Notify me about new reports" );
        notifyButton.setSelection( true );

    }

    @Override
    public void init(IWorkbench workbench)
    {
    }

    @Override
    public boolean performOk()
    {

        return super.performOk();
    }

    @Override
    protected void performApply()
    {
        super.performApply();
    }

    @Override
    protected void performDefaults()
    {
        notifyButton.setSelection( true );
        deniedButton.setSelection( false );
        acceptButton.setSelection( false );

        super.performDefaults();
    }

    @Override
    public boolean isValid()
    {
        return true;
    }

}
