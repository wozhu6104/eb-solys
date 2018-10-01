/*******************************************************************************
 * Copyright (C) 2018 Elektrobit Automotive GmbH
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package com.elektrobit.ebsolys.core.targetdata.api.adapter;

import java.util.List;

import org.osgi.framework.ServiceRegistration;

import lombok.Data;

@Data
public class DynamicTargetAdaptorResult
{
    private final TargetAdapter adaptor;
    private final List<ServiceRegistration<?>> registrations;
}
