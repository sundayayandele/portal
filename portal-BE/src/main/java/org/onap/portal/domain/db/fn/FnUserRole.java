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

package org.onap.portal.domain.db.fn;

import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.Valid;
import javax.validation.constraints.Digits;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.onap.portal.domain.dto.ecomp.UserRole;

/*
CREATE TABLE `fn_user_role` (
        `user_id` int(10) NOT NULL,
        `role_id` int(10) NOT NULL,
        `priority` decimal(4,0) DEFAULT NULL,
        `app_id` int(11) NOT NULL DEFAULT 2,
        PRIMARY KEY (`user_id`,`role_id`,`app_id`),
        KEY `fn_user_role_role_id` (`role_id`) USING BTREE,
        KEY `fn_user_role_user_id` (`user_id`) USING BTREE,
        KEY `fk_fn_user__ref_178_fn_app_idx` (`app_id`),
        CONSTRAINT `fk_fn_user__ref_172_fn_user` FOREIGN KEY (`user_id`) REFERENCES `fn_user` (`user_id`),
        CONSTRAINT `fk_fn_user__ref_175_fn_role` FOREIGN KEY (`role_id`) REFERENCES `fn_role` (`role_id`),
        CONSTRAINT `fk_fn_user__ref_178_fn_app` FOREIGN KEY (`app_id`) REFERENCES `fn_app` (`app_id`)
        )
*/

@NamedNativeQueries({
    @NamedNativeQuery(
        name = "FnUserRole.retrieveUserRoleOnUserIdAndRoleIdAndAppId",
        query = "FROM FnUserRole where userId= :userId"
            + " and role_id= :roleId"
            + " and app_id= :appId"),
    @NamedNativeQuery(
        name = "FnUserRole.retrieveCachedAppRolesForUser",
        query = "FROM FnUserRole where userId= :userId"
            + " and userId= :userId"
            + " and app_id= :appId"),
    @NamedNativeQuery(
        name = "FnUserRole.isSuperAdmin",
        query = "SELECT"
            + "  userId.id as userId,"
            + "  userId.org_user_id as orgUserId,"
            + "  userrole.ROLE_ID as roleId,"
            + "  userrole.APP_ID as appId"
            + " FROM"
            + "  fn_user_role userrole"
            + "  INNER JOIN fn_user user ON user.USER_ID = userrole.USER_ID"
            + " WHERE"
            + "  user.org_user_id = :orgUserId"
            + "  AND userrole.ROLE_ID =:roleId"
            + "  AND userrole.APP_ID =:appId",
        resultSetMapping = "UserRole",
        resultClass = UserRole.class
    )
})

@SqlResultSetMapping(
    name = "UserRole",
    classes = {
        @ConstructorResult(
            targetClass = UserRole.class,
            columns = {
                @ColumnResult(name = "userId", type = Long.class),
                @ColumnResult(name = "orgUserId", type = String.class),
                @ColumnResult(name = "roleId", type = Long.class),
                @ColumnResult(name = "appId", type = Long.class)
            }
        )
    }
)

@NamedQueries({
    @NamedQuery(
        name = "FnUserRole.getAdminUserRoles",
        query = "FROM FnUserRole "
            + " WHERE  userId.id = :userId "
            + " AND roleId.id = :roleId "
            + " AND fnAppId.id = :appId"),
    @NamedQuery(
        name = "FnUserRole.retrieveByAppIdAndUserId",
        query = "from FnUserRole where fnAppId.id =:appId and userId.id =:userId"
    ),
    @NamedQuery(
        name = "FnUserRole.retrieveByAppIdAndRoleId",
        query = "from FnUserRole where fnAppId.id =:appId and roleId.id =:roleId"
    ),
    @NamedQuery(
        name = "FnUserRole.retrieveByUserIdAndRoleId",
        query = "from FnUserRole where userId.id =:userId and roleId.id =:roleId"
    )
})

@Table(
    name = "fn_user_role",
    indexes = {
        @Index(name = "fn_user_role_role_id", columnList = "role_id"),
        @Index(name = "fn_user_role_user_id", columnList = "user_id"),
        @Index(name = "fk_fn_user__ref_178_fn_app_idx", columnList = "fn_App_Id")},
    uniqueConstraints = {
        @UniqueConstraint(name = "fn_user_role_id", columnNames = {"role_id", "user_id", "fn_App_Id"})
    })
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
public class FnUserRole implements Serializable {
  @Id
  @SequenceGenerator(name = "portal_generator", sequenceName = "portal_generator", initialValue = 1000)
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id", columnDefinition = "int(11) auto_increment")
  private Long id;
  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
  @JoinColumn(name = "user_id", columnDefinition = "bigint")
  @Valid
  private FnUser userId;
  @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
  @JoinColumn(name = "role_id", columnDefinition = "bigint")
  @Valid
  private FnRole roleId;
  @Column(name = "priority", length = 4, columnDefinition = "decimal(4,0) DEFAULT NULL")
  @Digits(integer = 4, fraction = 0)
  private Long priority;
  @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
  @JoinColumn(name = "fn_app_id", columnDefinition = "bigint")
  @Valid
  private FnApp fnAppId;
}
