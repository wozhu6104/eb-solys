/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package test.com.elektrobit.ebrace.core.interactor.reset;

import org.junit.Test;
import org.mockito.Mockito;

import com.elektrobit.ebrace.core.interactor.api.reset.ClearAllDataInteractionCallback;
import com.elektrobit.ebrace.core.interactor.api.reset.ClearAllDataInteractionUseCase;
import com.elektrobit.ebrace.core.interactor.reset.ClearAllDataInteractionUseCaseImpl;
import com.elektrobit.ebsolys.core.targetdata.api.reset.ResetNotifier;

public class ResetUseCaseTest
{
    @Test
    public void isCallbackNotifiedAboutReset() throws Exception
    {
        ClearAllDataInteractionCallback callback = Mockito.mock( ClearAllDataInteractionCallback.class );
        ResetNotifier resetNotifier = Mockito.mock( ResetNotifier.class );

        ClearAllDataInteractionUseCase resetInteractionUseCase = new ClearAllDataInteractionUseCaseImpl( callback,
                                                                                                         resetNotifier );
        resetInteractionUseCase.reset();

        Mockito.verify( resetNotifier ).performReset();
        Mockito.verify( callback ).onResetDone();
    }
}
