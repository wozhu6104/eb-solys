/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.ui.ecl.preferences.script;

import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;

import com.elektrobit.ebrace.core.interactor.api.common.UseCaseFactoryInstance;
import com.elektrobit.ebrace.core.interactor.api.script.ScriptFolderPathInteractionUseCase;
import com.elektrobit.ebrace.core.interactor.api.script.ScriptFolderPathNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.script.ScriptFolderPathNotifyUseCase;

public class ScriptSettingsPreferencePage extends PreferencePage
        implements
            IWorkbenchPreferencePage,
            ScriptFolderPathNotifyCallback
{

    private static final int RESTART_EXECUTION_DELAY_MS = 1000;
    private Composite parentComposite;
    private ScriptFolderPathNotifyUseCase scriptFolderPathNotifyUseCase;
    private Text pathText;
    private String currentPath;
    private ScriptFolderPathInteractionUseCase scriptFolderPathInteractionUseCase;

    @Override
    public void init(IWorkbench workbench)
    {
    }

    @Override
    protected Control createContents(Composite parent)
    {
        createParentComposite( parent );
        createPageContent();

        scriptFolderPathNotifyUseCase = UseCaseFactoryInstance.get().makeScriptFolderPathNotifyUseCase( this );
        scriptFolderPathInteractionUseCase = UseCaseFactoryInstance.get().makeScriptFolderPathInteractionUseCase();

        return parentComposite;
    }

    private void createParentComposite(Composite parent)
    {
        parentComposite = new Composite( parent, SWT.NONE );
        GridLayout gridLayout = GridLayoutFactory.fillDefaults().create();
        gridLayout.numColumns = 2;
        gridLayout.marginTop = 10;
        parentComposite.setLayout( gridLayout );
    }

    private void createPageContent()
    {
        Label description = new Label( parentComposite, SWT.NONE );
        description
                .setText( "You can select an external folder to be used as the script folder. \nThe default script folder is located inside the installation folder." );
        GridData descriptionLabelData = new GridData( GridData.FILL_HORIZONTAL );
        descriptionLabelData.horizontalSpan = 2;
        description.setLayoutData( descriptionLabelData );

        pathText = new Text( parentComposite, SWT.BORDER );
        GridData pathTextData = new GridData( GridData.FILL_HORIZONTAL );
        pathText.setLayoutData( pathTextData );
        pathText.addModifyListener( (event) -> onPathTextFieldChanged() );

        Button button = new Button( parentComposite, SWT.PUSH );
        button.setText( "Browse..." );
        button.addSelectionListener( new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                onBrowseButtonClicked();
            }
        } );

        Label restartHintLabel = new Label( parentComposite, SWT.NONE );
        restartHintLabel.setText( "Note: EB solys will restart after changing the path." );
    }

    private void onPathTextFieldChanged()
    {
        updateConfirmButtons();
    }

    private void updateConfirmButtons()
    {
        updateApplyButton();
        getContainer().updateButtons();
    }

    private void onBrowseButtonClicked()
    {
        openPathDialog();
    }

    private void openPathDialog()
    {
        Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        DirectoryDialog directoryDialog = new DirectoryDialog( shell );
        directoryDialog.setText( "Script Folder Directory" );
        directoryDialog.setMessage( "Select a directory" );
        directoryDialog.setFilterPath( pathText.getText() );
        String selectedDirectory = directoryDialog.open();
        if (selectedDirectory != null)
        {
            pathText.setText( selectedDirectory );
        }
    }

    @Override
    public void dispose()
    {
        scriptFolderPathNotifyUseCase.unregister();
        super.dispose();
    }

    @Override
    public boolean performOk()
    {
        String newPath = pathText.getText();
        if (!newPath.equals( currentPath ))
        {
            boolean confirmed = confirmRestart();
            if (!confirmed)
            {
                return false;
            }
            scriptFolderPathInteractionUseCase.setScriptFolderPath( newPath );

            Display.getCurrent().timerExec( RESTART_EXECUTION_DELAY_MS, () -> restartApplication() );
        }
        return true;
    }

    private boolean confirmRestart()
    {
        Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        MessageBox messageBox = new MessageBox( shell, SWT.ICON_INFORMATION | SWT.OK | SWT.CANCEL );
        messageBox.setText( "Restart Needed" );
        messageBox.setMessage( "EB solys will restart now." );
        int pressedButton = messageBox.open();
        return pressedButton == SWT.OK;
    }

    private void restartApplication()
    {
        PlatformUI.getWorkbench().restart();
    }

    @Override
    public boolean isValid()
    {
        String enteredPath = pathText.getText();
        return isValidPath( enteredPath );
    }

    private boolean isValidPath(String enteredPath)
    {
        return scriptFolderPathInteractionUseCase.isScriptFolderPathValid( enteredPath );
    }

    @Override
    protected void performDefaults()
    {
        String defaultPath = scriptFolderPathInteractionUseCase.getScriptFolderDefaultPath();
        pathText.setText( defaultPath );
    }

    @Override
    public void onScriptSourceFolderPathChanged(String newPath)
    {
        currentPath = newPath;
        if (!pathText.getText().equals( newPath ))
        {
            pathText.setText( newPath );
        }
    }
}
