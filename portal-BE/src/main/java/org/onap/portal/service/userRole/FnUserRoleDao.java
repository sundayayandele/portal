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

package org.onap.portal.service.userRole;

import java.util.List;
import java.util.Optional;
import org.onap.portal.domain.db.fn.FnUserRole;
import org.onap.portal.domain.dto.ecomp.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
interface FnUserRoleDao extends JpaRepository<FnUserRole, Long> {

       @Query
       Optional<List<FnUserRole>> getAdminUserRoles(final @Param("userId") Long userId, final @Param("roleId") Long roleId, final @Param("appId") Long appId);

       @Query
       List<UserRole> isSuperAdmin(final @Param("orgUserId") String orgUserId, final @Param("roleId") Long roleId, final @Param("appId") Long appId);

       @Query
       List<FnUserRole> retrieveByAppIdAndUserId(final @Param("appId") Long appId, final @Param("userId") String userId);

       @Query
       List<FnUserRole> retrieveByAppIdAndRoleId(final @Param("appId") Long appId, final @Param("roleId") Long roleId);

       @Query
       List<FnUserRole> retrieveByUserIdAndRoleId(final @Param("userId") Long userId, final @Param("roleId") Long roleId);
}
