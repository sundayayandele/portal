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
package org.onap.portalapp.portal.transport;

import static org.junit.Assert.*;
import java.util.Date;
import org.junit.Test;

public class CentralAppTest {

    CentralApp centralApp = new CentralApp.CentralAppBuilder().createCentralApp();

    private static final String TEST = "test";

    public CentralApp mockCentralApp() {
        CentralApp centralApp = new CentralApp.CentralAppBuilder().setId((long) 1).setCreated(null).setModified(null)
                .setCreatedId((long) 1).setModifiedId((long) 1).setRowNum((long) 1).setName(TEST).setImageUrl(TEST)
                .setDescription(TEST).setNotes(TEST).setUrl(TEST).setAlternateUrl(TEST).setRestEndpoint(TEST)
                .setMlAppName(TEST).setMlAppAdminId(TEST).setMotsId(TEST).setAppPassword(TEST).setOpen(TEST)
                .setEnabled(TEST).setThumbnail(null).setUsername(TEST).setUebKey(TEST).setUebSecret(TEST)
                .setUebTopicName(TEST).createCentralApp();
        return centralApp;
    }

    @Test
    public void centralAppTest() {
        CentralApp centralApp = mockCentralApp();

        CentralApp centralApp1 = buildCentralApp();

        assertEquals(centralApp.getId(), new Long(1));
        assertEquals(centralApp.getCreatedId(), new Long(1));
        assertEquals(centralApp.getModifiedId(), new Long(1));
        assertEquals(centralApp.getRowNum(), new Long(1));
        assertEquals(centralApp.getName(), TEST);
        assertEquals(centralApp.getImageUrl(), TEST);
        assertEquals(centralApp.getDescription(), TEST);
        assertEquals(centralApp.getNotes(), TEST);
        assertEquals(centralApp.getUrl(), TEST);
        assertEquals(centralApp.getAlternateUrl(), TEST);
        assertEquals(centralApp.getRestEndpoint(), TEST);
        assertEquals(centralApp.getMlAppName(), TEST);
        assertEquals(centralApp.getMlAppAdminId(), TEST);
        assertEquals(centralApp.getMotsId(), TEST);
        assertEquals(centralApp.getAppPassword(), TEST);
        assertEquals(centralApp.getOpen(), TEST);
        assertEquals(centralApp.getEnabled(), TEST);
        assertEquals(centralApp.getUsername(), TEST);
        assertEquals(centralApp.getUebKey(), TEST);
        assertEquals(centralApp.getUebSecret(), TEST);
        assertEquals(centralApp.getUebTopicName(), TEST);

        assertTrue(centralApp.equals(centralApp1));
        assertEquals(centralApp.hashCode(), centralApp1.hashCode());
    }

    private CentralApp buildCentralApp() {
        CentralApp centralApp = new CentralApp.CentralAppBuilder().createCentralApp();
        centralApp.setId(1l);
        centralApp.setCreatedId(1l);
        centralApp.setModifiedId(1l);
        centralApp.setRowNum(1l);
        centralApp.setName(TEST);
        centralApp.setImageUrl(TEST);
        centralApp.setDescription(TEST);
        centralApp.setNotes(TEST);
        centralApp.setUrl(TEST);
        centralApp.setAlternateUrl(TEST);
        centralApp.setRestEndpoint(TEST);
        centralApp.setMlAppAdminId(TEST);
        centralApp.setMlAppName(TEST);
        centralApp.setMotsId(TEST);
        centralApp.setAppPassword(TEST);
        centralApp.setOpen(TEST);
        centralApp.setEnabled(TEST);
        centralApp.setUsername(TEST);
        centralApp.setUebKey(TEST);
        centralApp.setUebSecret(TEST);
        centralApp.setUebTopicName(TEST);
        return centralApp;
    }

    @Test
    public void unt_IdTest() {
        Long defaultValue = 123L;
        centralApp.setId(defaultValue);
        assertEquals(defaultValue, centralApp.getId());
    }

    @Test
    public void unt_createdTest() {
        Date defaultValue = new Date();
        centralApp.setCreated(defaultValue);
        assertEquals(defaultValue, centralApp.getCreated());
    }

    @Test
    public void unt_modifiedTest() {
        Date defaultValue = new Date();
        centralApp.setCreated(defaultValue);
        assertEquals(defaultValue, centralApp.getCreated());
    }

    @Test
    public void unt_craetedIdTest() {
        Long defaultValue = 123L;
        centralApp.setCreatedId(defaultValue);
        assertEquals(defaultValue, centralApp.getCreatedId());
    }

    @Test
    public void unt_modifiedIdTest() {
        Long defaultValue = 123L;
        centralApp.setModifiedId(defaultValue);
        assertEquals(defaultValue, centralApp.getModifiedId());
    }

    @Test
    public void unt_rowNumTest() {
        Long defaultValue = 123L;
        centralApp.setRowNum(defaultValue);
        assertEquals(defaultValue, centralApp.getRowNum());
    }

    @Test
    public void unt_nameTest() {
        String defaultValue = "test";
        centralApp.setName(defaultValue);
        assertEquals(defaultValue, centralApp.getName());
    }

    @Test
    public void unt_ImageUrlTest() {
        String defaultValue = "test";
        centralApp.setImageUrl(defaultValue);
        assertEquals(defaultValue, centralApp.getImageUrl());
    }

    @Test
    public void unt_descriptionTest() {
        String defaultValue = "test";
        centralApp.setDescription(defaultValue);
        assertEquals(defaultValue, centralApp.getDescription());
    }

    @Test
    public void unt_notesTest() {
        String defaultValue = "test";
        centralApp.setNotes(defaultValue);
        assertEquals(defaultValue, centralApp.getNotes());
    }

    @Test
    public void unt_urlTest() {
        String defaultValue = "testUrl";
        centralApp.setUrl(defaultValue);
        assertEquals(defaultValue, centralApp.getUrl());
    }

    @Test
    public void unt_alternateUrlTest() {
        String defaultValue = "testUrl";
        centralApp.setAlternateUrl(defaultValue);
        assertEquals(defaultValue, centralApp.getAlternateUrl());
    }

    @Test
    public void unt_restendpointTest() {
        String defaultValue = "testUrl";
        centralApp.setRestEndpoint(defaultValue);
        assertEquals(defaultValue, centralApp.getRestEndpoint());
    }

    @Test
    public void unt_mlAppNameTest() {
        String defaultValue = "testAppName";
        centralApp.setMlAppName(defaultValue);
        assertEquals(defaultValue, centralApp.getMlAppName());
    }

    @Test
    public void unt_mlAppAdminIdTest() {
        String defaultValue = "testAppAdminId";
        centralApp.setMlAppAdminId(defaultValue);
        assertEquals(defaultValue, centralApp.getMlAppAdminId());
    }

    @Test
    public void unt_motsIdIdTest() {
        String defaultValue = "testmotsid";
        centralApp.setMotsId(defaultValue);
        assertEquals(defaultValue, centralApp.getMotsId());
    }

    @Test
    public void unt_appPasswordTest() {
        String defaultValue = "TestAppPassword";
        centralApp.setAppPassword(defaultValue);
        assertEquals(defaultValue, centralApp.getAppPassword());
    }

    @Test
    public void unt_openTest() {
        String defaultValue = "Testopen";
        centralApp.setOpen(defaultValue);
        assertEquals(defaultValue, centralApp.getOpen());
    }

    @Test
    public void unt_enabledTest() {
        String defaultValue = "Testenable";
        centralApp.setEnabled(defaultValue);
        assertEquals(defaultValue, centralApp.getEnabled());
    }

    @Test
    public void unt_thumbnailTest() {
        byte[] defaultValue = { 1, 2, 3 };
        centralApp.setThumbnail(defaultValue);
        assertEquals(defaultValue, centralApp.getThumbnail());
    }

    @Test
    public void unt_userNameTest() {
        String defaultValue = "Testusername";
        centralApp.setUsername(defaultValue);
        assertEquals(defaultValue, centralApp.getUsername());
    }

    @Test
    public void unt_uebKeyTest() {
        String defaultValue = "Testuebkey";
        centralApp.setUebKey(defaultValue);
        assertEquals(defaultValue, centralApp.getUebKey());
    }

    @Test
    public void unt_uebSecreteTest() {
        String defaultValue = "Testuebscrete";
        centralApp.setUebSecret(defaultValue);
        assertEquals(defaultValue, centralApp.getUebSecret());
    }

    @Test
    public void unt_uebTopicNameTest() {
        String defaultValue = "Testuebtopicname";
        centralApp.setUebTopicName(defaultValue);
        assertEquals(defaultValue, centralApp.getUebTopicName());
    }

    @Test
    public void unt_hashCodeWithNullTest() {
        CentralApp centralApp = new CentralApp.CentralAppBuilder().setId(null).setCreated(null).setModified(null)
                .setCreatedId(null).setModifiedId(null).setRowNum(null).setName(null).setImageUrl(null)
                .setDescription(null).setNotes(null).setUrl(null).setAlternateUrl(null).setRestEndpoint(null)
                .setMlAppName(null).setMlAppAdminId(null).setMotsId(null).setAppPassword(null).setOpen(null)
                .setEnabled(null).setThumbnail(null).setUsername(null).setUebKey(null).setUebSecret(null)
                .setUebTopicName(null).createCentralApp();

        CentralApp centralApp1 = new CentralApp.CentralAppBuilder().setId(null).setCreated(null).setModified(null)
                .setCreatedId(null).setModifiedId(null).setRowNum(null).setName(null).setImageUrl(null)
                .setDescription(null).setNotes(null).setUrl(null).setAlternateUrl(null).setRestEndpoint(null)
                .setMlAppName(null).setMlAppAdminId(null).setMotsId(null).setAppPassword(null).setOpen(null)
                .setEnabled(null).setThumbnail(null).setUsername(null).setUebKey(null).setUebSecret(null)
                .setUebTopicName(null).createCentralApp();

        assertEquals(centralApp.hashCode(), centralApp1.hashCode());
        assertTrue(centralApp.equals(centralApp1));

    }

    @Test
    public void unt_hashCodeTest() {
        CentralApp centralApp = new CentralApp.CentralAppBuilder().setId((long) 1).setCreated(null).setModified(null)
                .setCreatedId((long) 1).setModifiedId((long) 1).setRowNum((long) 1).setName("test").setImageUrl("test")
                .setDescription("test").setNotes("test").setUrl("test").setAlternateUrl("test").setRestEndpoint("test")
                .setMlAppName("test").setMlAppAdminId("test").setMotsId("test").setAppPassword("test").setOpen("test")
                .setEnabled("test").setThumbnail(null).setUsername("test").setUebKey("test").setUebSecret("test")
                .setUebTopicName("test").createCentralApp();

        CentralApp centralApp1 = new CentralApp.CentralAppBuilder().setId((long) 1).setCreated(null).setModified(null)
                .setCreatedId((long) 1).setModifiedId((long) 1).setRowNum((long) 1).setName("test").setImageUrl("test")
                .setDescription("test").setNotes("test").setUrl("test").setAlternateUrl("test").setRestEndpoint("test")
                .setMlAppName("test").setMlAppAdminId("test").setMotsId("test").setAppPassword("test").setOpen("test")
                .setEnabled("test").setThumbnail(null).setUsername("test").setUebKey("test").setUebSecret("test")
                .setUebTopicName("test").createCentralApp();

        assertEquals(centralApp.hashCode(), centralApp1.hashCode());
        assertTrue(centralApp.equals(centralApp1));

    }

    @Test
    public void unt_hashCodeTestWithNull() {
        CentralApp centralApp = new CentralApp.CentralAppBuilder().setId((long) 1).setCreated(null).setModified(null)
                .setCreatedId((long) 1).setModifiedId((long) 1).setRowNum((long) 1).setName("test").setImageUrl("test")
                .setDescription("test").setNotes("test").setUrl("test").setAlternateUrl("test").setRestEndpoint("test")
                .setMlAppName("test").setMlAppAdminId("test").setMotsId("test").setAppPassword("test").setOpen("test")
                .setEnabled("test").setThumbnail(null).setUsername("test").setUebKey("test").setUebSecret("test")
                .setUebTopicName("test").createCentralApp();
        CentralApp centralApp1 = new CentralApp.CentralAppBuilder().setId((long) 1).setCreated(null).setModified(null)
                .setCreatedId((long) 1).setModifiedId((long) 1).setRowNum((long) 1).setName("test").setImageUrl("test")
                .setDescription("test").setNotes("test").setUrl("test").setAlternateUrl("test").setRestEndpoint("test")
                .setMlAppName("test").setMlAppAdminId("test").setMotsId("test").setAppPassword("test").setOpen("test")
                .setEnabled("test").setThumbnail(null).setUsername("test").setUebKey("test").setUebSecret("test")
                .setUebTopicName("test").createCentralApp();
        assertEquals(centralApp.hashCode(), centralApp1.hashCode());
        assertFalse(centralApp.equals(null));

    }

    @Test
    public void unt_hashCodeTestWithNull1() {
        CentralApp centralApp = new CentralApp.CentralAppBuilder().setId(null).setCreated(null).setModified(null)
                .setCreatedId(null).setModifiedId(null).setRowNum(null).setName(null).setImageUrl(null)
                .setDescription(null).setNotes(null).setUrl(null).setAlternateUrl(null).setRestEndpoint(null)
                .setMlAppName(null).setMlAppAdminId(null).setMotsId(null).setAppPassword(null).setOpen(null)
                .setEnabled(null).setThumbnail(null).setUsername(null).setUebKey(null).setUebSecret(null)
                .setUebTopicName(null).createCentralApp();
        CentralApp centralApp1 = new CentralApp.CentralAppBuilder().setId((long) 1).setCreated(null).setModified(null)
                .setCreatedId((long) 1).setModifiedId((long) 1).setRowNum((long) 1).setName("test").setImageUrl("test")
                .setDescription("test").setNotes("test").setUrl("test").setAlternateUrl("test").setRestEndpoint("test")
                .setMlAppName("test").setMlAppAdminId("test").setMotsId("test").setAppPassword("test").setOpen("test")
                .setEnabled("test").setThumbnail(null).setUsername("test").setUebKey("test").setUebSecret("test")
                .setUebTopicName("test").createCentralApp();
        assertFalse(centralApp.equals(centralApp1));

    }

    @Test
    public void unt_hashCodeTestWithalternateURL() {
        CentralApp centralApp = new CentralApp.CentralAppBuilder().setId(null).setCreated(null).setModified(null)
                .setCreatedId(null).setModifiedId(null).setRowNum(null).setName(null).setImageUrl(null)
                .setDescription(null).setNotes(null).setUrl(null).setAlternateUrl("test1").setRestEndpoint(null)
                .setMlAppName(null).setMlAppAdminId(null).setMotsId(null).setAppPassword(null).setOpen(null)
                .setEnabled(null).setThumbnail(null).setUsername(null).setUebKey(null).setUebSecret(null)
                .setUebTopicName(null).createCentralApp();
        CentralApp centralApp1 = new CentralApp.CentralAppBuilder().setId((long) 1).setCreated(null).setModified(null)
                .setCreatedId((long) 1).setModifiedId((long) 1).setRowNum((long) 1).setName("test").setImageUrl("test")
                .setDescription("test").setNotes("test").setUrl("test").setAlternateUrl("test").setRestEndpoint("test")
                .setMlAppName("test").setMlAppAdminId("test").setMotsId("test").setAppPassword("test").setOpen("test")
                .setEnabled("test").setThumbnail(null).setUsername("test").setUebKey("test").setUebSecret("test")
                .setUebTopicName("test").createCentralApp();
        assertFalse(centralApp.equals(centralApp1));

    }

    @Test
    public void unt_hashCodeTestWithpassword() {
        CentralApp centralApp = new CentralApp.CentralAppBuilder().setId(null).setCreated(null).setModified(null)
                .setCreatedId(null).setModifiedId(null).setRowNum(null).setName(null).setImageUrl(null)
                .setDescription(null).setNotes(null).setUrl(null).setAlternateUrl("test").setRestEndpoint(null)
                .setMlAppName(null).setMlAppAdminId(null).setMotsId(null).setAppPassword("testPass").setOpen(null)
                .setEnabled(null).setThumbnail(null).setUsername(null).setUebKey(null).setUebSecret(null)
                .setUebTopicName(null).createCentralApp();
        CentralApp centralApp1 = new CentralApp.CentralAppBuilder().setId((long) 1).setCreated(null).setModified(null)
                .setCreatedId((long) 1).setModifiedId((long) 1).setRowNum((long) 1).setName("test").setImageUrl("test")
                .setDescription("test").setNotes("test").setUrl("test").setAlternateUrl("test").setRestEndpoint("test")
                .setMlAppName("test").setMlAppAdminId("test").setMotsId("test").setAppPassword("test").setOpen("test")
                .setEnabled("test").setThumbnail(null).setUsername("test").setUebKey("test").setUebSecret("test")
                .setUebTopicName("test").createCentralApp();
        assertFalse(centralApp.equals(centralApp1));

    }

    @Test
    public void unt_hashCodeTestWithcreateId() {
        CentralApp centralApp = new CentralApp.CentralAppBuilder().setId(null).setCreated(null).setModified(null)
                .setCreatedId(123L).setModifiedId(null).setRowNum(null).setName(null).setImageUrl(null)
                .setDescription(null).setNotes(null).setUrl(null).setAlternateUrl("test").setRestEndpoint(null)
                .setMlAppName(null).setMlAppAdminId(null).setMotsId(null).setAppPassword("test").setOpen(null)
                .setEnabled(null).setThumbnail(null).setUsername(null).setUebKey(null).setUebSecret(null)
                .setUebTopicName(null).createCentralApp();
        CentralApp centralApp1 = new CentralApp.CentralAppBuilder().setId((long) 1).setCreated(null).setModified(null)
                .setCreatedId((long) 1).setModifiedId((long) 1).setRowNum((long) 1).setName("test").setImageUrl("test")
                .setDescription("test").setNotes("test").setUrl("test").setAlternateUrl("test").setRestEndpoint("test")
                .setMlAppName("test").setMlAppAdminId("test").setMotsId("test").setAppPassword("test").setOpen("test")
                .setEnabled("test").setThumbnail(null).setUsername("test").setUebKey("test").setUebSecret("test")
                .setUebTopicName("test").createCentralApp();
        assertFalse(centralApp.equals(centralApp1));

    }

    @Test
    public void unt_hashCodeTestWithcreateId1() {
        CentralApp centralApp = new CentralApp.CentralAppBuilder().setId(12L).setCreated(null).setModified(null)
                .setCreatedId(123L).setModifiedId(123L).setRowNum(123L).setName("test1").setImageUrl("test1")
                .setDescription("test1").setNotes("test1").setUrl("test1").setAlternateUrl("test")
                .setRestEndpoint("tests1").setMlAppName("test1").setMlAppAdminId("test1").setMotsId(null)
                .setAppPassword("test").setOpen(null).setEnabled(null).setThumbnail(null).setUsername(null)
                .setUebKey(null).setUebSecret(null).setUebTopicName(null).createCentralApp();
        CentralApp centralApp1 = new CentralApp.CentralAppBuilder().setId((long) 1).setCreated(null).setModified(null)
                .setCreatedId((long) 1).setModifiedId((long) 1).setRowNum((long) 1).setName("test").setImageUrl("test")
                .setDescription("test").setNotes("test").setUrl("test").setAlternateUrl("test").setRestEndpoint("test")
                .setMlAppName("test").setMlAppAdminId("test").setMotsId("test").setAppPassword("test").setOpen("test")
                .setEnabled("test").setThumbnail(null).setUsername("test").setUebKey("test").setUebSecret("test")
                .setUebTopicName("test").createCentralApp();
        assertFalse(centralApp.equals(centralApp1));
    }
}
