/*-
 * ============LICENSE_START==========================================
 * ONAP Portal
 * ===================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
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
package org.onap.portalapp.portal.transport;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.onap.portalapp.portal.transport.CentralV2Role;
import org.onap.portalapp.portal.transport.CentralV2User;
import org.onap.portalapp.portal.transport.CentralV2UserApp;

public class CentralUserTest {

	public CentralV2User mockCentralUser(){
		Set<CentralV2UserApp> userApps = new HashSet<CentralV2UserApp>();
		Set<CentralV2Role> pseudoRoles = new HashSet<CentralV2Role>();
		CentralV2User centralV2User = new CentralV2User((long)1, null, null, (long)1, (long)1, (long)1, (long)1,
				(long)1, "test", "test", "test", "test", "test",
				"test", "test", (long)1, "test", "test", "test",
				"test", "test", "test", "test", "test", "test", "test",
				"test", "test", "test", "test",
				"test", "test", "test", "test", "test",
				"test", "test", "test", "test", "test",
				"test", "test", "test", "test", null,
				false, false, (long)1, (long)1, false, "test", userApps, pseudoRoles);
		
		return centralV2User;
	}
	
	@Test
	public void centralRoleTest(){
		CentralV2User centralV2User = mockCentralUser();
		
		Set<CentralV2UserApp> userApps = new HashSet<CentralV2UserApp>();
		Set<CentralV2Role> pseudoRoles = new HashSet<CentralV2Role>();
		CentralV2User centralV2User1 = new CentralV2User((long)1, null, null, (long)1, (long)1, (long)1, (long)1,
				(long)1, "test", "test", "test", "test", "test",
				"test", "test", (long)1, "test", "test", "test",
				"test", "test", "test", "test", "test", "test", "test",
				"test", "test", "test", "test",
				"test", "test", "test", "test", "test",
				"test", "test", "test", "test", "test",
				"test", "test", "test", "test", null,
				false, false, (long)1, (long)1, false, "test", userApps, pseudoRoles);
		
		
		assertEquals(centralV2User, centralV2User1);
		assertEquals(centralV2User.hashCode(), centralV2User1.hashCode());
		assertTrue(centralV2User.equals(centralV2User1));
	}
}
