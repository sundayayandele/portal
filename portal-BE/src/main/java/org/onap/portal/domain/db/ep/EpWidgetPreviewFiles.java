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

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.Digits;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*
CREATE TABLE `ep_widget_preview_files` (
        `preview_id` int(11) NOT NULL AUTO_INCREMENT,
        `html_file` longblob DEFAULT NULL,
        `css_file` longblob DEFAULT NULL,
        `javascript_file` longblob DEFAULT NULL,
        `framework_file` longblob DEFAULT NULL,
        PRIMARY KEY (`preview_id`)
        )
*/

@Table(name = "ep_widget_preview_files")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class EpWidgetPreviewFiles implements Serializable {
       @Id
       @SequenceGenerator(name="portal_generator", sequenceName = "portal_generator", initialValue = 1000)
       @GeneratedValue(strategy = GenerationType.AUTO)
       @Column(name = "preview_id", length = 11, nullable = false, columnDefinition = "int(11) AUTO_INCREMENT")
       @Digits(integer = 11, fraction = 0)
       private Integer previewId;
       @Column(name = "html_file", columnDefinition = "longblob DEFAULT NULL")
       private byte[] htmlFile;
       @Column(name = "css_file", columnDefinition = "longblob DEFAULT NULL")
       private byte[] cssFile;
       @Column(name = "javascript_file", columnDefinition = "longblob DEFAULT NULL")
       private byte[] javascriptFile;
       @Column(name = "framework_file", columnDefinition = "longblob DEFAULT NULL")
       private byte[] frameworkFile;
}
