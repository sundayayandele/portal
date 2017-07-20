/*-
 * ================================================================================
 * ECOMP Portal
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ================================================================================
 */
package org.openecomp.portalapp.portal.service;

import java.util.List;

import org.openecomp.portalapp.portal.domain.EPUser;
import org.openecomp.portalapp.portal.transport.FieldsValidator;
import org.openecomp.portalapp.portal.transport.OnboardingWidget;

public interface WidgetService {

	List<OnboardingWidget> getOnboardingWidgets(EPUser user, boolean managed);

	FieldsValidator setOnboardingWidget(EPUser user, OnboardingWidget onboardingWidget);

	FieldsValidator deleteOnboardingWidget(EPUser user, Long onboardingWidgetId);

}