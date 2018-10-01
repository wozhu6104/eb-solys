/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.viewer.common.dialog;

import java.io.File;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.elektrobit.ebrace.viewer.common.ViewerCommonPlugin;

public class ExportEventsAsCSVDialog extends BaseSaveSettingsDialog
{
    private static final String EXPORT_AS = "Export As ...";
    private static final String EXPORT_THE_RUNTIME_EVENTS_TO_CSV = "Export the runtime events to CSV. ";
    private static final String PLEASE_SELECT_A_FILE = "Please select a file";
    private static final String LAST_FILE = "lastFile";
    private static final String DELIMITER2 = "delimiter";
    private FileFieldEditor editor;
    private CCombo delimiterCombo;
    private Composite content;
    private String newPath;
    String delimiter;
    ExportEventsAsCSVDialog dialog;

    public ExportEventsAsCSVDialog(Shell parentShell)
    {
        super( parentShell );
        dialog = this;
    }

    @Override
    protected Control createContents(Composite parent)
    {
        Composite container = new Composite( parent, SWT.BORDER );
        container.setLayout( new GridLayout() );
        container.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );
        Control result = super.createContents( container );
        setTitle( EXPORT_THE_RUNTIME_EVENTS_TO_CSV );
        setMessage( PLEASE_SELECT_A_FILE );
        getShell().setText( EXPORT_AS );

        content = new Composite( (Composite)getDialogArea(), SWT.NONE );
        content.setLayout( new GridLayout( 2, false ) );
        content.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ) );
        editor = new FileFieldEditor( "Select",
                                      "Select a file",
                                      false,
                                      FileFieldEditor.VALIDATE_ON_KEY_STROKE,
                                      content )
        {
            @Override
            protected void fireValueChanged(String property, Object oldValue, Object newValue)
            {
                if (newValue instanceof String)
                    newPath = (String)newValue;
                super.fireValueChanged( property, oldValue, newValue );
            }

            @Override
            protected boolean checkState()
            {
                boolean result = false;
                String msg = null;

                String path = getTextControl().getText();
                if (path != null)
                {
                    path = path.trim();
                }
                else
                {
                    path = "";//$NON-NLS-1$
                }
                if (path.length() == 0)
                {
                    if (!isEmptyStringAllowed())
                    {
                        msg = getErrorMessage();
                        result = false;
                    }
                }
                else
                {
                    File file = new File( path );
                    if (file != null)
                    {
                        if (file.getParentFile().exists())
                        {
                            result = true;
                        }
                    }
                }

                if (msg != null)
                { // error
                    showErrorMessage( msg );
                    result = false;
                }
                msg = getErrorMessage();
                if (msg != null)
                {
                    showErrorMessage( msg );
                }
                getButton( OK ).setEnabled( result );
                if (!result)
                {
                    dialog.setErrorMessage( "The selected directory does not exist." );
                }
                else
                {
                    dialog.setErrorMessage( null );
                    setMessage( PLEASE_SELECT_A_FILE );
                }
                return result;

            }

        };
        editor.setFileExtensions( new String[]{"*.csv", "*.*"} );
        editor.setEmptyStringAllowed( false );
        Label l = new Label( content, SWT.NONE );
        l.setText( "Delimiter" );
        delimiterCombo = new CCombo( content, SWT.BORDER | SWT.READ_ONLY );
        delimiterCombo.add( ";" );
        delimiterCombo.add( "," );
        delimiterCombo.addSelectionListener( new SelectionListener()
        {

            @Override
            public void widgetSelected(SelectionEvent e)
            {
                delimiter = delimiterCombo.getText();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e)
            {
                // do nothing.
            }
        } );
        initializeDialogSettings();
        delimiter = delimiterCombo.getText();
        getButton( OK ).setEnabled( editor.isValid() );
        return result;
    }

    public String getFilePath()
    {
        return newPath;
    }

    public String getCSVDelimiter()
    {
        return delimiter;
    }

    @Override
    public boolean close()
    {
        saveDialogSettings();
        return super.close();
    }

    private void initializeDialogSettings()
    {
        intializeDelimiter();
        initializeFilePath();
    }

    private void initializeFilePath()
    {
        IDialogSettings settings = ViewerCommonPlugin.getDefault().getDialogSettings();
        String lastPath = settings.get( LAST_FILE );
        if (lastPath != null && !lastPath.isEmpty())
        {
            editor.setStringValue( lastPath );
        }
    }

    private void intializeDelimiter()
    {
        IDialogSettings settings = ViewerCommonPlugin.getDefault().getDialogSettings();
        String delimiter = settings.get( DELIMITER2 );
        if (delimiter != null && !delimiter.isEmpty())
        {

            int index = 0;
            for (String d : delimiterCombo.getItems())
            {
                if (d.equals( delimiter ))
                {
                    break;
                }
                index++;
            }
            if (index < delimiterCombo.getItemCount())
            {
                delimiterCombo.select( index );
            }
            else
            {
                delimiterCombo.select( index );
            }
        }
        else
        {
            delimiterCombo.select( 0 );
        }
    }

    private void saveDialogSettings()
    {
        IDialogSettings settings = ViewerCommonPlugin.getDefault().getDialogSettings();
        settings.put( DELIMITER2, this.delimiter );
        settings.put( LAST_FILE, this.newPath );
    }
}
