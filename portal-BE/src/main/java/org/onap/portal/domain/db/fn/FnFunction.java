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
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.SafeHtml;

/*
CREATE TABLE `fn_function` (
        `function_cd` varchar(30) NOT NULL,
        `function_name` varchar(50) NOT NULL,
        PRIMARY KEY (`function_cd`)
        )
*/

@Table(name = "fn_function")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
public class FnFunction implements Serializable {
       @Id
       @Column(name = "function_cd", nullable = false)
       private String functionCd;
       @Column(name = "function_name", length = 50, nullable = false)
       @Size(max = 50)
       @SafeHtml
       @NotNull
       private String functionName;
       private String code;
       private String name;
       private String type;
       private String action;
       @OneToMany(
               targetEntity = FnRestrictedUrl.class,
               mappedBy = "functionCd",
               cascade = CascadeType.MERGE,
               fetch = FetchType.LAZY
       )
       private Set<FnRestrictedUrl> fnRestrictedUrls;
       @OneToMany(
               targetEntity = FnRoleFunction.class,
               mappedBy = "functionCd",
               cascade = CascadeType.MERGE,
               fetch = FetchType.LAZY
       )
       private Set<FnRoleFunction> fnRoleFunctions;
       @OneToMany(
               targetEntity = FnTab.class,
               mappedBy = "functionCd",
               cascade = CascadeType.MERGE,
               fetch = FetchType.LAZY
       )
       private Set<FnTab> fnTabs;
}
