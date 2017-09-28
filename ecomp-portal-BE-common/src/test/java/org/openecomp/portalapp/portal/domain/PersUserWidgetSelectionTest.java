/*-
 * ============LICENSE_START==========================================
 * ONAP Portal
 * ===================================================================
 * Copyright © 2017 AT&T Intellectual Property. All rights reserved.
 * ===================================================================
 *
 * Unless otherwise specified, all software contained herein is licensed
 * under the Apache License, Version 2.0 (the “License”);
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
 * under the Creative Commons License, Attribution 4.0 Intl. (the “License”);
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
 * ECOMP is a trademark and service mark of AT&T Intellectual Property.
 */
package org.openecomp.portalapp.portal.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openecomp.portalapp.portal.domain.PersUserWidgetSelection;

public class PersUserWidgetSelectionTest {

	public PersUserWidgetSelection mockPersUserWidgetSelection(){
		
		PersUserWidgetSelection persUserWidgetSelection = new PersUserWidgetSelection();
		
		persUserWidgetSelection.setUserId((long)1);
		persUserWidgetSelection.setWidgetId((long)1);
		persUserWidgetSelection.setStatusCode("test");
		
		return persUserWidgetSelection;
	}
	
	@Test
	public void persUserWidgetSelectionTest(){
		PersUserWidgetSelection persUserWidgetSelection = mockPersUserWidgetSelection();
		
		PersUserWidgetSelection persUserWidgetSelection1 = new PersUserWidgetSelection((long)1, (long)1, (long)1, "test");
		
		assertEquals(persUserWidgetSelection.getUserId(), new Long(1));
		assertEquals(persUserWidgetSelection.getWidgetId(), new Long(1));
		assertEquals(persUserWidgetSelection.getStatusCode(), "test");
		assertEquals(persUserWidgetSelection1,  new PersUserWidgetSelection((long)1, (long)1, (long)1, "test"));
		assertTrue(persUserWidgetSelection1.equals(new PersUserWidgetSelection((long)1, (long)1, (long)1,"test")));
		assertEquals(persUserWidgetSelection.hashCode(), persUserWidgetSelection1.hashCode());
	}
	
}