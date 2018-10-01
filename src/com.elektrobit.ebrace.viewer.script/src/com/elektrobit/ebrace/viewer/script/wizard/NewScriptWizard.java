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

import org.eclipse.core.resources.IResource;
import org.eclipse.swt.graphics.Image;
import org.eclipse.xtend.ide.wizards.AbstractNewXtendElementWizard;
import org.eclipse.xtext.ui.IImageHelper;

import com.elektrobit.ebrace.core.interactor.api.script.ScriptConstants;
import com.elektrobit.ebrace.viewer.script.ViewerScriptPlugin;
import com.elektrobit.ebrace.viewer.script.wizard.ScriptSourceGenerator.ScriptContext;

@SuppressWarnings("restriction")
public class NewScriptWizard extends AbstractNewXtendElementWizard
{
    private final NewScriptWizardNamePage namePage;
    private final NewScriptWizardContextPage contextPage;

    public NewScriptWizard(NewScriptWizardNamePage page)
    {
        super( new IImageHelper.NullImageHelper(), page, ScriptConstants.TITLE );
        this.namePage = page;
        this.contextPage = new NewScriptWizardContextPage( "" );
    }

    @Override
    public void addPages()
    {
        addPage( namePage );
        addPage( contextPage );
    }

    @Override
    public boolean canFinish()
    {
        return namePage.canFlipToNextPage();
    }

    @Override
    public Image getDefaultPageImage()
    {
        return ViewerScriptPlugin.getDefault().getImage( "script_new_script_wizard", "png" );
    }

    public String getScriptContent()
    {
        String scriptName = namePage.getTypeName();
        ScriptContext scriptContext = contextPage.getSelectionContext();
        boolean beforeMethod = contextPage.isBeforeMethodSelected();
        boolean afterMethod = contextPage.isAfterMethodSelected();
        boolean injectedParameter = contextPage.isInjectedParameterSelected();

        return ScriptSourceGenerator
                .generateClassContent( scriptName, scriptContext, beforeMethod, afterMethod, injectedParameter );
    }

    @Override
    protected void selectAndReveal(IResource newResource)
    {
    }

    @Override
    public boolean needsPreviousAndNextButtons()
    {
        return true;
    }
}
