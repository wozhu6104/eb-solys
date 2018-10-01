/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.ui.ecl.file;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.actions.CompoundContributionItem;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;
import org.eclipse.ui.menus.IWorkbenchContribution;
import org.eclipse.ui.services.IServiceLocator;

import com.elektrobit.ebrace.viewer.preferences.util.PersistentLoadedOfflineFilesPreferences;

public class RecentFilesDynamicMenu extends CompoundContributionItem implements IWorkbenchContribution

{
    private static final String EMPTY_LIST_COMMAND_ID = "com.elektrobit.ebrace.targetadapter.communicator.command.emptyFilesList";
    private static final String OPEN_RECENT_FILE_COMMAND_ID = "com.elektrobitebrace.targetadapter.tracemessageplayer.openrecentlogfiles";
    private static final String COMMAND_PARAMETER_ID = "com.elektrobit.ebrace.ui.ecl.file.commandParameter";
    private static final String EMPTY_LIST_LABEL = "(empty)";
    private static final String CLEAR_ITEM_LABEL = "Clear the list";

    private IServiceLocator mServiceLocator;

    public RecentFilesDynamicMenu()
    {
    }

    @Override
    protected IContributionItem[] getContributionItems()
    {
        List<IContributionItem> menuItemsList = new ArrayList<IContributionItem>();
        String[] splittedLoadedFilesString = PersistentLoadedOfflineFilesPreferences.getValueForLoadedFilesString();
        if (splittedLoadedFilesString.length != 0)
        {
            if (!splittedLoadedFilesString[0].equals( "" ))
            {
                for (int i = 0; i < splittedLoadedFilesString.length; i++)
                {
                    menuItemsList.add( createSubmenuEntry( splittedLoadedFilesString[i] ) );
                }
                menuItemsList.add( new Separator() );
                menuItemsList.add( getEntryToClearSubmenu() );
            }
            else
            {
                menuItemsList.add( new EmptyContributionItem( mServiceLocator ) );
            }
        }
        else
        {
            menuItemsList.add( new EmptyContributionItem( mServiceLocator ) );
        }
        IContributionItem[] menuItems = new IContributionItem[menuItemsList.size()];
        menuItems = menuItemsList.toArray( menuItems );

        return menuItems;
    }

    private CommandContributionItem createSubmenuEntry(String path)
    {
        String[] splittedPathFile = splitString( path, "\\\\" );
        CommandContributionItemParameter contributionParameter = new CommandContributionItemParameter( mServiceLocator,
                                                                                                       null,
                                                                                                       OPEN_RECENT_FILE_COMMAND_ID,
                                                                                                       CommandContributionItem.STYLE_PUSH );

        contributionParameter.label = getFileNameFromPath( splittedPathFile );
        contributionParameter.visibleEnabled = true;
        contributionParameter.parameters = new HashMap<String, String>();
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put( COMMAND_PARAMETER_ID, path );
        contributionParameter.parameters = parameters;

        return new CommandContributionItem( contributionParameter );
    }

    private String getFileNameFromPath(String[] path)
    {
        return path[path.length - 1];
    }

    private IContributionItem getEntryToClearSubmenu()
    {
        CommandContributionItemParameter contributionParameter = new CommandContributionItemParameter( mServiceLocator,
                                                                                                       null,
                                                                                                       EMPTY_LIST_COMMAND_ID,
                                                                                                       CommandContributionItem.STYLE_PUSH );
        contributionParameter.label = CLEAR_ITEM_LABEL;
        contributionParameter.visibleEnabled = true;
        return new CommandContributionItem( contributionParameter );
    }

    @Override
    public void initialize(final IServiceLocator serviceLocator)
    {
        mServiceLocator = serviceLocator;
    }

    private String[] splitString(String str, String regex)
    {
        return str.split( regex );
    }

    private static CommandContributionItemParameter getContributionParameterForEmptyItem(IServiceLocator serviceLocator)
    {
        CommandContributionItemParameter contributionParameters = new CommandContributionItemParameter( serviceLocator,
                                                                                                        null,
                                                                                                        EMPTY_LIST_COMMAND_ID,
                                                                                                        CommandContributionItem.STYLE_PUSH );
        contributionParameters.label = EMPTY_LIST_LABEL;
        contributionParameters.visibleEnabled = false;
        return contributionParameters;
    }

    public static String getCommandParamterId()
    {
        return COMMAND_PARAMETER_ID;
    }

    class EmptyContributionItem extends CommandContributionItem
    {
        public EmptyContributionItem(IServiceLocator serviceLocator)
        {
            super( getContributionParameterForEmptyItem( serviceLocator ) );
        }

        @Override
        public boolean isEnabled()
        {
            return false;
        }
    }
}
