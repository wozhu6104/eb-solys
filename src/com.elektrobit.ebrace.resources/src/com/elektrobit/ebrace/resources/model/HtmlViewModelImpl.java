/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.resources.model;

import com.elektrobit.ebrace.core.interactor.api.resources.model.EditRight;
import com.elektrobit.ebrace.core.interactor.api.resources.model.ResourcesFolder;
import com.elektrobit.ebrace.core.interactor.api.resources.model.htmlview.HtmlViewModel;
import com.elektrobit.ebrace.resources.api.ResourceChangedNotifier;
import com.elektrobit.ebrace.resources.api.model.BaseResourceModel;

public class HtmlViewModelImpl extends BaseResourceModel implements HtmlViewModel
{
    private final String url;

    public HtmlViewModelImpl(String initialName, String url, ResourcesFolder parentFolder,
            ResourceChangedNotifier resourceChangedNotifier)
    {
        super( initialName, parentFolder, EditRight.EDITABLE, resourceChangedNotifier );
        this.url = url;
    }

    @Override
    public String getURL()
    {
        return url;
    }

}
