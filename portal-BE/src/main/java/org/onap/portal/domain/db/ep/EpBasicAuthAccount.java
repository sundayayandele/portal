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

package org.onap.portal.domain.db.ep;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.SafeHtml;

/*
CREATE TABLE `ep_basic_auth_account` (
        `id` int(11) NOT NULL AUTO_INCREMENT,
        `ext_app_name` varchar(50) NOT NULL,
        `username` varchar(50) NOT NULL,
        `password` varchar(50) DEFAULT NULL,
        `active_yn` char(1) NOT NULL DEFAULT 'Y',
        PRIMARY KEY (`id`)
        )
*/

@Table(name = "ep_basic_auth_account")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class EpBasicAuthAccount {
       @Id
       @GeneratedValue(strategy = GenerationType.AUTO)
       @Column(name = "id", length = 11, nullable = false, columnDefinition = "int(11) AUTO_INCREMENT")
       @Digits(integer = 11, fraction = 0)
       private Long id;
       @Column(name = "ext_app_name", length = 50, nullable = false)
       @Size(max = 50)
       @SafeHtml
       @NotNull
       private String extAppName;
       @Column(name = "username", length = 50, nullable = false)
       @Size(max = 50)
       @SafeHtml
       @NotNull
       private String username;
       @Column(name = "password", length = 50)
       @Size(max = 50)
       @SafeHtml
       private String password;
       @Column(name = "active_yn", length = 1, nullable = false, columnDefinition = "char(1) NOT NULL default 'Y'")
       @Pattern(regexp = "[YNyn]")
       @Size(max = 1)
       @NotNull
       @SafeHtml
       private String activeYn;
       @OneToMany(
               targetEntity = EpEndpointsBasicAuthAccount.class,
               mappedBy = "accountId",
               cascade = CascadeType.ALL,
               fetch = FetchType.LAZY
       )
       private List<EpEndpointsBasicAuthAccount> epEndpointsBasicAuthAccounts = new ArrayList<>();
}
