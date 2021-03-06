/*-
 * ============LICENSE_START==========================================
 * ONAP Portal
 * ===================================================================
 * Copyright (C) 2017-2018 AT&T Intellectual Property. All rights reserved.
 * ===================================================================
 *
 * Unless otherwise specified, all software contained herein is licensed
 * under the Apache License, Version 2.0 (the "License");
 * you may not use this software except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *             http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Unless otherwise specified, all documentation contained herein is licensed
 * under the Creative Commons License, Attribution 4.0 Intl. (the "License");
 * you may not use this documentation except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *             https://creativecommons.org/licenses/by/4.0/
 *
 * Unless required by applicable law or agreed to in writing, documentation
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ============LICENSE_END============================================
 *
 * 
 */
package org.onap.portalapp.portal.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.hibernate.SessionFactory;
import org.onap.portalapp.portal.domain.CentralV2RoleFunction;
import org.onap.portalapp.portal.domain.EPUser;
import org.onap.portalapp.portal.exceptions.RoleFunctionException;
import org.onap.portalapp.portal.utils.EcompPortalUtils;
import org.onap.portalapp.util.EPUserUtils;
import org.onap.portalsdk.core.domain.RoleFunction;
import org.onap.portalsdk.core.logging.logic.EELFLoggerDelegate;
import org.onap.portalsdk.core.service.DataAccessService;
import org.onap.portalsdk.core.util.SystemProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;


@Transactional
public class EPRoleFunctionServiceCentralizedImpl implements EPRoleFunctionService{

	private static EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(EPRoleFunctionServiceCentralizedImpl.class);

	@Autowired
	private DataAccessService dataAccessService;
	
	@Autowired
	private  SessionFactory sessionFactory;
	
	@Autowired
	private  ExternalAccessRolesService externalAccessRolesService;
	
	@SuppressWarnings("unchecked")
	@Override
	public List<RoleFunction> getRoleFunctions() {
		List<CentralV2RoleFunction> getRoleFuncList = null;
		List<RoleFunction> getRoleFuncListOfPortal = new ArrayList<>();
		final Map<String, Long> params = new HashMap<>();
		params.put("appId", (long) 1);
		//Sync all functions from external system into ONAP portal DB
		getRoleFuncList = dataAccessService.executeNamedQuery("getAllRoleFunctions", params, null);
		for(CentralV2RoleFunction roleFunction : getRoleFuncList)
		{
			RoleFunction roleFun = new RoleFunction();
			String code = EcompPortalUtils.getFunctionCode(roleFunction.getCode());
			roleFun.setCode(code);
			roleFun.setName(roleFunction.getName());
			getRoleFuncListOfPortal.add(roleFun);
		}
		return getRoleFuncListOfPortal;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Set getRoleFunctions(HttpServletRequest request, EPUser user) throws RoleFunctionException {
		HttpSession session = request.getSession();
		String userId = user.getId().toString();
		final Map<String, String> params = new HashMap<>();
		params.put("userId", userId);
		List getRoleFuncListOfPortal = dataAccessService.executeNamedQuery("getRoleFunctionsOfUser", params, null);
		Set<String> getRoleFuncListOfPortalSet = new HashSet<>(getRoleFuncListOfPortal);
		Set<String> roleFunSet = new HashSet<>();
		roleFunSet = getRoleFuncListOfPortalSet.stream().filter(x -> x.contains("|")).collect(Collectors.toSet());
		if (roleFunSet.size() > 0)
			for (String roleFunction : roleFunSet) {
				String roleFun = EcompPortalUtils.getFunctionCode(roleFunction);
				getRoleFuncListOfPortalSet.remove(roleFunction);
				getRoleFuncListOfPortalSet.add(roleFun);
			}

		Set<String> finalRoleFunctionSet = new HashSet<>();
		for (String roleFn : getRoleFuncListOfPortalSet) {
			finalRoleFunctionSet.add(EPUserUtils.decodeFunctionCode(roleFn));
		}
		session.setAttribute(SystemProperties.getProperty(SystemProperties.ROLE_FUNCTIONS_ATTRIBUTE_NAME),
				finalRoleFunctionSet);
		logger.debug(EELFLoggerDelegate.debugLogger,
				"getRoleFunctions: RoleFunctions Of User" + finalRoleFunctionSet);
		return finalRoleFunctionSet;
	}
}
