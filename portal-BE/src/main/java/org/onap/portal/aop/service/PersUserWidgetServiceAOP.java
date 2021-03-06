/*
 * ============LICENSE_START==========================================
 * ONAP Portal
 * ===================================================================
 * Copyright (C) 2019 AT&T Intellectual Property. All rights reserved.
 * ===================================================================
 * Modifications Copyright (c) 2019 Samsung
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

package org.onap.portal.aop.service;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.onap.portal.domain.db.fn.FnUser;
import org.onap.portal.domain.dto.transport.WidgetCatalogPersonalization;
import org.onap.portal.validation.DataValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class PersUserWidgetServiceAOP {

       private static final Logger LOGGER = LoggerFactory.getLogger(PersUserWidgetServiceAOP.class);

       private final DataValidator dataValidator;

       @Autowired
       public PersUserWidgetServiceAOP(DataValidator dataValidator) {
              this.dataValidator = dataValidator;
       }

       @Before("execution(* org.onap.portal.service.PersUserWidgetService.setPersUserAppValue(..)) && args(userId, personalization)")
       public void setPersUserAppValue(final long userId, final WidgetCatalogPersonalization personalization) {
              if (!dataValidator.isValid(personalization)) {
                     LOGGER.error("IllegalArgumentException for user " + userId);
                     throw new IllegalArgumentException(dataValidator.getConstraintViolationsString(personalization));
              }
       }
}
