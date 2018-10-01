/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebrace.core.interactor.preferences;

import com.elektrobit.ebrace.common.checks.RangeCheckUtils;
import com.elektrobit.ebrace.core.interactor.api.preferences.FileSizeLimitNotifyCallback;
import com.elektrobit.ebrace.core.interactor.api.preferences.FileSizeLimitNotifyUseCase;
import com.elektrobit.ebsolys.core.targetdata.api.importer.FileSizeLimitService;

public class FileSizeLimitNotifyUseCaseImpl implements FileSizeLimitNotifyUseCase
{
    public FileSizeLimitNotifyUseCaseImpl(FileSizeLimitService fileSizeLimitService,
            FileSizeLimitNotifyCallback callback)
    {
        RangeCheckUtils.assertReferenceParameterNotNull( "fileSizeLimitService", fileSizeLimitService );
        RangeCheckUtils.assertReferenceParameterNotNull( "callback", callback );

        long maxSolysFileSizeMB = fileSizeLimitService.getMaxSolysFileSizeMB();
        callback.onFileSizeLimitChanged( maxSolysFileSizeMB );
    }

    @Override
    public void unregister()
    {
    }
}
