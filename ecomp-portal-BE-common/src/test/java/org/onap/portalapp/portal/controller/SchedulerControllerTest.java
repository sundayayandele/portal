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
package org.onap.portalapp.portal.controller;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.formula.functions.T;
import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.onap.portalapp.portal.core.MockEPUser;
import org.onap.portalapp.portal.domain.EPUser;
import org.onap.portalapp.portal.framework.MockitoTestSuite;
import org.onap.portalapp.portal.scheduler.SchedulerProperties;
import org.onap.portalapp.portal.scheduler.SchedulerRestInterface;
import org.onap.portalapp.portal.scheduler.restobjects.RestObject;
import org.onap.portalapp.portal.service.AdminRolesService;
import org.onap.portalapp.util.EPUserUtils;
import org.onap.portalsdk.core.util.SystemProperties;
import org.onap.portalsdk.core.web.support.UserUtils;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RunWith(PowerMockRunner.class)
@PrepareForTest({UserUtils.class,SystemProperties.class,SchedulerProperties.class,EPUserUtils.class})
public class SchedulerControllerTest {

	@Mock
	SchedulerRestInterface schedulerRestInterface;
	
	@Mock
	AdminRolesService adminRolesService;

	@InjectMocks
	SchedulerController schedulerController;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	MockEPUser mockUser = new MockEPUser();
	MockitoTestSuite mockitoTestSuite = new MockitoTestSuite();

	HttpServletRequest mockedRequest = mockitoTestSuite.getMockedRequest();
	HttpServletResponse mockedResponse = mockitoTestSuite.getMockedResponse();
	NullPointerException nullPointerException = new NullPointerException();

	@Test
	public void  getTimeSlotsTest() throws Exception{
		JSONObject jsonObject =Mockito.mock(JSONObject.class);
		Mockito.when(jsonObject.get("scheduleId")).thenReturn("12");
		Set<String> functions = new HashSet<>();
		functions.add("/get_time_slots/*");
		Mockito.when(mockedRequest.getRequestURI()).thenReturn("/portalApi/get_time_slots/1");
		Mockito.when(adminRolesService.getAllAppsFunctionsOfUser(Matchers.anyString())).thenReturn(functions);
        PowerMockito.mockStatic(SystemProperties.class);
        PowerMockito.mockStatic(EPUserUtils.class);
        EPUser user = new EPUser();
        user.setId((long) 1);
        Mockito.when(EPUserUtils.matchRoleFunctions(Matchers.anyString(), Matchers.anySet())).thenReturn(true);
        Mockito.when(EPUserUtils.getUserSession(mockedRequest)).thenReturn(user);
		schedulerController.getTimeSlots(mockedRequest, "12");
		
	}
	
	@Test
	public void  getTimeSlotsTestWithException1() throws Exception{
		JSONObject jsonObject =Mockito.mock(JSONObject.class);
		Mockito.when(jsonObject.get("scheduleId")).thenReturn("12");
		Set<String> functions = new HashSet<>();
		functions.add("/get_time_slots/*");
		Mockito.when(mockedRequest.getRequestURI()).thenReturn("/portalApi/get_time_slots/1");
		Mockito.when(adminRolesService.getAllAppsFunctionsOfUser(Matchers.anyString())).thenReturn(functions);
        PowerMockito.mockStatic(SystemProperties.class);
        PowerMockito.mockStatic(EPUserUtils.class);
        EPUser user = new EPUser();
        user.setId((long) 1);
        Mockito.when(EPUserUtils.matchRoleFunctions(Matchers.anyString(), Matchers.anySet())).thenReturn(true);
        Mockito.when(EPUserUtils.getUserSession(mockedRequest)).thenReturn(user);
		RestObject<T> restObject=new RestObject<>();
		Mockito.doThrow(new NullPointerException()).when(schedulerRestInterface).Get(Matchers.any(),Matchers.any(),Matchers.any(),Matchers.any());
		schedulerController.getTimeSlots(mockedRequest, "12");
		
	}
	
	
	@Test
	public void  getTimeSlotsTestWithexception() throws Exception{
		JSONObject jsonObject =Mockito.mock(JSONObject.class);
		Mockito.when(jsonObject.get("scheduleId")).thenReturn("12");
		Set<String> functions = new HashSet<>();
		functions.add("/get_time_slots/*");
		Mockito.when(mockedRequest.getRequestURI()).thenReturn("/portalApi/get_time_slots/1");
		Mockito.when(adminRolesService.getAllAppsFunctionsOfUser(Matchers.anyString())).thenReturn(functions);
        PowerMockito.mockStatic(SystemProperties.class);
        PowerMockito.mockStatic(EPUserUtils.class);
        EPUser user = new EPUser();
        user.setId((long) 1);
        Mockito.when(EPUserUtils.getUserSession(mockedRequest)).thenReturn(user);
		schedulerController.getTimeSlots(mockedRequest, null);
		
	}
	
	@Test
	public void postCreateNewVNFChangeTest() throws Exception{
		//String testJsonData="{\"domain\":\"ChangeManagement\",\"scheduleName\":\"VnfUpgrade/DWF\",\"userId\":\"su7376\",\"domainData\":[{\"WorkflowName\":\"HEAT Stack Software Update for vNFs\",\"CallbackUrl\":\"http://127.0.0.1:8989/scheduler/v1/loopbacktest/vid\",\"CallbackData\":\"testing\"}],\"schedulingInfo\":{\"normalDurationInSeconds\":60,\"additionalDurationInSeconds\":60,\"concurrencyLimit\":60,\"policyId\":\"SNIRO_CM_1707.Config_MS_Demo_TimeLimitAndVerticalTopology_zone_localTime.1.xml\",\"vnfDetails\":[{\"groupId\":\"group1\",\"node\":[\"satmo415vbc\",\"satmo455vbc\"]}]}}";
		JSONObject jsonObject =Mockito.mock(JSONObject.class);
		Mockito.when(jsonObject.get("scheduleId")).thenReturn("12");
		Set<String> functions = new HashSet<>();
		functions.add("post_create_new_vnf_change");
		Mockito.when(mockedRequest.getRequestURI()).thenReturn("/portalApi/post_create_new_vnf_change");
		Mockito.when(adminRolesService.getAllAppsFunctionsOfUser(Matchers.anyString())).thenReturn(functions);
        PowerMockito.mockStatic(SystemProperties.class);
        PowerMockito.mockStatic(EPUserUtils.class);
        EPUser user = new EPUser();
        user.setId((long) 1);
        Mockito.when(EPUserUtils.matchRoleFunctions(Matchers.anyString(), Matchers.anySet())).thenReturn(true);
        Mockito.when(EPUserUtils.getUserSession(mockedRequest)).thenReturn(user);
		schedulerController.postCreateNewVNFChange(mockedRequest, jsonObject);
	}
	
	@Test
	public void  postCreateNewVNFChangeTestWithException1() throws Exception{
		JSONObject jsonObject =Mockito.mock(JSONObject.class);
		RestObject<T> restObject=new RestObject<>();
		Mockito.when(jsonObject.get("scheduleId")).thenReturn("12");
		Set<String> functions = new HashSet<>();
		functions.add("post_create_new_vnf_change");
		Mockito.when(mockedRequest.getRequestURI()).thenReturn("/portalApi/post_create_new_vnf_change");
		Mockito.when(adminRolesService.getAllAppsFunctionsOfUser(Matchers.anyString())).thenReturn(functions);
        PowerMockito.mockStatic(SystemProperties.class);
        PowerMockito.mockStatic(EPUserUtils.class);
        EPUser user = new EPUser();
        user.setId((long) 1);
        Mockito.when(EPUserUtils.matchRoleFunctions(Matchers.anyString(), Matchers.anySet())).thenReturn(true);
        Mockito.when(EPUserUtils.getUserSession(mockedRequest)).thenReturn(user);
		Mockito.doThrow(new NullPointerException()).when(schedulerRestInterface).Post(Matchers.any(),Matchers.any(),Matchers.any(),Matchers.any());
		schedulerController.postCreateNewVNFChange(mockedRequest, jsonObject);
		
	}
	
	
	@Test
	public void postCreateNewVNFChangeTestWithException() throws Exception{
		//String testJsonData="{\"domain\":\"ChangeManagement\",\"scheduleName\":\"VnfUpgrade/DWF\",\"userId\":\"su7376\",\"domainData\":[{\"WorkflowName\":\"HEAT Stack Software Update for vNFs\",\"CallbackUrl\":\"http://127.0.0.1:8989/scheduler/v1/loopbacktest/vid\",\"CallbackData\":\"testing\"}],\"schedulingInfo\":{\"normalDurationInSeconds\":60,\"additionalDurationInSeconds\":60,\"concurrencyLimit\":60,\"policyId\":\"SNIRO_CM_1707.Config_MS_Demo_TimeLimitAndVerticalTopology_zone_localTime.1.xml\",\"vnfDetails\":[{\"groupId\":\"group1\",\"node\":[\"satmo415vbc\",\"satmo455vbc\"]}]}}";
		JSONObject jsonObject =Mockito.mock(JSONObject.class);
		Mockito.when(jsonObject.get("scheduleId")).thenReturn("12");
		Set<String> functions = new HashSet<>();
		functions.add("post_create_new_vnf_change");
		Mockito.when(mockedRequest.getRequestURI()).thenReturn("/portalApi/post_create_new_vnf_change");
		Mockito.when(adminRolesService.getAllAppsFunctionsOfUser(Matchers.anyString())).thenReturn(functions);
        PowerMockito.mockStatic(SystemProperties.class);
        PowerMockito.mockStatic(EPUserUtils.class);
        EPUser user = new EPUser();
        user.setId((long) 1);
        Mockito.when(EPUserUtils.getUserSession(mockedRequest)).thenReturn(user);
		schedulerController.postCreateNewVNFChange(mockedRequest, null);
	}
	
	@Test
	public void postSubmitVnfChangeTimeslotsTest() throws Exception{
		JSONObject jsonObject =Mockito.mock(JSONObject.class);
		Mockito.when(jsonObject.get("scheduleId")).thenReturn("12");
		Set<String> functions = new HashSet<>();
		functions.add("submit_vnf_change_timeslots");
		Mockito.when(mockedRequest.getRequestURI()).thenReturn("/portalApi/submit_vnf_change_timeslots");
		Mockito.when(adminRolesService.getAllAppsFunctionsOfUser(Matchers.anyString())).thenReturn(functions);
        PowerMockito.mockStatic(SystemProperties.class);
        PowerMockito.mockStatic(EPUserUtils.class);
        Mockito.when(EPUserUtils.matchRoleFunctions(Matchers.anyString(), Matchers.anySet())).thenReturn(true);
        EPUser user = new EPUser();
        user.setId((long) 1);
        Mockito.when(EPUserUtils.getUserSession(mockedRequest)).thenReturn(user);
        PowerMockito.mockStatic(SystemProperties.class);
		PowerMockito.when(SystemProperties.getProperty(SchedulerProperties.SCHEDULER_SUBMIT_NEW_VNF_CHANGE)).thenReturn("/v1/ChangeManagement/schedules/{scheduleId}/approvals");
		schedulerController.postSubmitVnfChangeTimeslots(mockedRequest, jsonObject);
	}
	
	@Test
	public void  postSubmitVnfChangeTimeslotsTestWithException1() throws Exception{
		JSONObject jsonObject =Mockito.mock(JSONObject.class);
		Mockito.when(jsonObject.get("scheduleId")).thenReturn("12");
		Set<String> functions = new HashSet<>();
		functions.add("submit_vnf_change_timeslots");
		Mockito.when(mockedRequest.getRequestURI()).thenReturn("/portalApi/submit_vnf_change_timeslots");
		Mockito.when(adminRolesService.getAllAppsFunctionsOfUser(Matchers.anyString())).thenReturn(functions);
        PowerMockito.mockStatic(SystemProperties.class);
        PowerMockito.mockStatic(EPUserUtils.class);
        Mockito.when(EPUserUtils.matchRoleFunctions(Matchers.anyString(), Matchers.anySet())).thenReturn(true);
        EPUser user = new EPUser();
        user.setId((long) 1);
        Mockito.when(EPUserUtils.getUserSession(mockedRequest)).thenReturn(user);
		PowerMockito.when(SystemProperties.getProperty(SchedulerProperties.SCHEDULER_SUBMIT_NEW_VNF_CHANGE)).thenReturn("/v1/ChangeManagement/schedules/{scheduleId}/approvals");
		ResponseEntity<String> res =	schedulerController.postSubmitVnfChangeTimeslots(mockedRequest, null);		
	}
	
	@Test
	public void postSubmitVnfChangeTimeslotsTestWithException() throws Exception{
		JSONObject jsonObject =Mockito.mock(JSONObject.class);
		Mockito.when(jsonObject.get("scheduleId")).thenReturn("12");
		Set<String> functions = new HashSet<>();
		functions.add("submit_vnf_change_timeslots");
		Mockito.when(mockedRequest.getRequestURI()).thenReturn("/portalApi/submit_vnf_change_timeslots");
		Mockito.when(adminRolesService.getAllAppsFunctionsOfUser(Matchers.anyString())).thenReturn(functions);
        PowerMockito.mockStatic(SystemProperties.class);
        PowerMockito.mockStatic(EPUserUtils.class);
        EPUser user = new EPUser();
        user.setId((long) 1);
        Mockito.when(EPUserUtils.getUserSession(mockedRequest)).thenReturn(user);
		PowerMockito.when(SystemProperties.getProperty(SchedulerProperties.SCHEDULER_SUBMIT_NEW_VNF_CHANGE)).thenReturn("/v1/ChangeManagement/schedules/{scheduleId}/approvals");
		ResponseEntity<String> res =	schedulerController.postSubmitVnfChangeTimeslots(mockedRequest, null);
		assertEquals(res.getStatusCode(), HttpStatus.UNAUTHORIZED);
	}
	
	
	@Test
	public void getSchedulerConstantTestWithException() throws Exception{
		JSONObject jsonObject =Mockito.mock(JSONObject.class);
		Mockito.when(jsonObject.get("scheduleId")).thenReturn("12");
		Set<String> functions = new HashSet<>();
		functions.add("get_scheduler_constant");
		Mockito.when(mockedRequest.getRequestURI()).thenReturn("/portalApi/get_scheduler_constant");
		Mockito.when(adminRolesService.getAllAppsFunctionsOfUser(Matchers.anyString())).thenReturn(functions);
        PowerMockito.mockStatic(SystemProperties.class);
        PowerMockito.mockStatic(EPUserUtils.class);
        EPUser user = new EPUser();
        user.setId((long) 1);
        Mockito.when(EPUserUtils.getUserSession(mockedRequest)).thenReturn(user);
        PowerMockito.mockStatic(SystemProperties.class);
		PowerMockito.when(SystemProperties.getProperty(SchedulerProperties.SCHEDULER_CALLBACK_URL)).thenReturn("mockedRequest");
		schedulerController.getSchedulerConstant(mockedRequest, mockedResponse);
	}
	
	@Test
	public void getSchedulerConstantTest() throws Exception{
		JSONObject jsonObject =Mockito.mock(JSONObject.class);
		Mockito.when(jsonObject.get("scheduleId")).thenReturn("12");
		Set<String> functions = new HashSet<>();
		functions.add("submit_vnf_change_timeslots");
		Mockito.when(mockedRequest.getRequestURI()).thenReturn("/portalApi/submit_vnf_change_timeslots");
		Mockito.when(adminRolesService.getAllAppsFunctionsOfUser(Matchers.anyString())).thenReturn(functions);
        PowerMockito.mockStatic(SystemProperties.class);
        PowerMockito.mockStatic(EPUserUtils.class);
        Mockito.when(EPUserUtils.matchRoleFunctions(Matchers.anyString(), Matchers.anySet())).thenReturn(true);
        EPUser user = new EPUser();
        user.setId((long) 1);
        Mockito.when(EPUserUtils.getUserSession(mockedRequest)).thenReturn(user);
        PowerMockito.mockStatic(SystemProperties.class);
		PowerMockito.when(SystemProperties.getProperty(SchedulerProperties.SCHEDULER_CALLBACK_URL)).thenReturn("callbackUrl");
		schedulerController.getSchedulerConstant(mockedRequest, mockedResponse);
	}
	
}