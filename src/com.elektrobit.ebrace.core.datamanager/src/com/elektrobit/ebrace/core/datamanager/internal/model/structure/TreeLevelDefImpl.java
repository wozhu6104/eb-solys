/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.datamanager.internal.model.structure;

import java.io.File;
import java.io.Serializable;
import java.net.URI;

import com.elektrobit.ebrace.common.utils.FileHelper;
import com.elektrobit.ebrace.core.datamanager.PluginConstants;
import com.elektrobit.ebsolys.core.targetdata.api.structure.TreeLevelDef;

public class TreeLevelDefImpl implements TreeLevelDef, Serializable
{
    private static final long serialVersionUID = -3292289638458099057L;

    private volatile int m_hashCode;
    private final String m_name;
    private final String m_description;
    private final String pathToIcon;

    /**
     * Standard c'tor.
     * 
     * @param name
     *            Name of the {@link TreeLevelDef}
     * @param description
     *            Description of the {@link TreeLevelDef}
     * @param imageDescriptor
     *            ImageDescriptor of the {@link TreeLevelDef}
     */
    public TreeLevelDefImpl(String name, String description, String pathToIcon)
    {
        m_name = name;
        m_description = description;

        if (pathToIcon == null)
        {
            URI uri = FileHelper.locateFileInBundle( PluginConstants.PLUGIN_ID, "icons/structure_tree_level_item.png" );
            File file = new File( uri );
            this.pathToIcon = file.getPath();
        }
        else
        {
            this.pathToIcon = pathToIcon;
        }
    }

    /**
     * Returns the name of the {@link TreeLevelDef}.
     */
    @Override
    public String getName()
    {
        return m_name;
    }

    /**
     * Returns the description of the {@link TreeLevelDef}.
     */
    @Override
    public String getDescription()
    {
        return m_description;
    }

    /**
     * Returns the path of the icon of the {@link TreeLevelDef}.
     */
    @Override
    public String getIconPath()
    {
        return pathToIcon;
    }

    @Override
    public String toString()
    {
        return "TreeLevel: [" + getName() + ", " + getDescription() + "]";
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }

        if (!(o instanceof TreeLevelDefImpl))
        {
            return false;
        }

        TreeLevelDefImpl compareTreeLevel = (TreeLevelDefImpl)o;

        return getName().equals( compareTreeLevel.getName() );
    }

    @Override
    public int hashCode()
    {
        int result = m_hashCode;
        if (result == 0)
        {
            result = 17;
            result = 31 * result + m_name.hashCode();
        }
        return result;
    }
}
