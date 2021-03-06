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
package org.onap.portalapp.portal.service;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.SessionFactory;
import org.onap.portalapp.portal.domain.EPUser;
import org.onap.portalapp.portal.domain.EPUserNotification;
import org.onap.portalapp.portal.domain.EcompAppRole;
import org.onap.portalapp.portal.logging.aop.EPMetricsLog;
import org.onap.portalapp.portal.transport.EpNotificationItem;
import org.onap.portalapp.portal.transport.EpNotificationItemVO;
import org.onap.portalapp.portal.transport.EpRoleNotificationItem;
import org.onap.portalsdk.core.logging.logic.EELFLoggerDelegate;
import org.onap.portalsdk.core.service.DataAccessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Service;

@Service("userNotificationService")
@org.springframework.context.annotation.Configuration
@EnableAspectJAutoProxy
@EPMetricsLog
public class UserNotificationServiceImpl implements UserNotificationService {
	EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(FunctionalMenuServiceImpl.class);

	@Autowired
	private DataAccessService dataAccessService;
	@Autowired
	private SessionFactory sessionFactory;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.onap.portalapp.portal.service.UserNotificationService#
	 * getNotifications(java.lang.Long)
	 */
	@Override
	public List<EpNotificationItem> getNotifications(Long userId) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("user_id", userId.toString());
		@SuppressWarnings("unchecked")
		List<EpNotificationItem> notificationList = dataAccessService.executeNamedQuery("getNotifications", params,
				null);
		// set the roles to null for pure retrieval of all notifications
		for (EpNotificationItem item : notificationList) {
			item.setRoles(null);
		}
		return notificationList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.onap.portalapp.portal.service.UserNotificationService#
	 * getNotificationHistoryVO(java.lang.Long)
	 */
	@Override
	public List<EpNotificationItemVO> getNotificationHistoryVO(Long userId) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("user_id", userId.toString());
		@SuppressWarnings("unchecked")
		List<EpNotificationItemVO> notificationList = dataAccessService.executeNamedQuery("getNotificationHistoryVO",
				params, null);
		return notificationList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.onap.portalapp.portal.service.UserNotificationService#
	 * getAdminNotificationVOS()
	 */
	@Override
	public List<EpNotificationItemVO> getAdminNotificationVOS(Long userId) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("user_id", userId.toString());
		@SuppressWarnings("unchecked")
		List<EpNotificationItemVO> notificationList = dataAccessService
				.executeNamedQuery("getAdminNotificationHistoryVO", params, null);
		return notificationList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.onap.portalapp.portal.service.UserNotificationService#
	 * getNotificationRoles(java.lang.Long)
	 */
	@Override
	public List<EpRoleNotificationItem> getNotificationRoles(Long notificationId) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("notificationId", Long.toString(notificationId));
		@SuppressWarnings("unchecked")
		List<EpRoleNotificationItem> roleNotifList = dataAccessService.executeNamedQuery("getNotificationRoles", params,
				null);
		return roleNotifList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.onap.portalapp.portal.service.UserNotificationService#
	 * getAppRoleList()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<EcompAppRole> getAppRoleList() {
		List<EcompAppRole> appRoleList = (List<EcompAppRole>) dataAccessService
				.executeNamedQuery("getEpNotificationAppRoles", null, null);
		return appRoleList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.onap.portalapp.portal.service.UserNotificationService#
	 * setNotificationsRead(java.lang.Long, int)
	 */
	@Override
	public void setNotificationRead(Long notificationId, int userId) {
		EPUserNotification userNotification = new EPUserNotification();
		userNotification.setNotificationId(notificationId);
		userNotification.setUpdateTime(new Date());
		userNotification.setViewed("Y");
		userNotification.setUserId((long) userId);
		getDataAccessService().saveDomainObject(userNotification, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.onap.portalapp.portal.service.UserNotificationService#
	 * saveNotification(org.onap.portalapp.portal.transport.
	 * EpNotificationItem)
	 */
	@Override
	public String saveNotification(EpNotificationItem notificationItem) throws Exception {

		// gather the roles
		if (notificationItem.getRoleIds() != null && !notificationItem.getIsForAllRoles().equals("Y")) {
			if (notificationItem.getRoles() == null) {
				Set<EpRoleNotificationItem> roleSet = new HashSet<EpRoleNotificationItem>();
				notificationItem.setRoles(roleSet);
			}
			for (Long roleId : notificationItem.getRoleIds()) {
				EpRoleNotificationItem roleItem = new EpRoleNotificationItem();
				roleItem.setNotificationId(notificationItem.getNotificationId());
				roleItem.setRoleId(roleId.intValue());
				notificationItem.getRoles().add(roleItem);
			}
		}

		// for updates fetch roles and then save
		if (notificationItem.getNotificationId() != null) {
			EpNotificationItem updateNotificationItem = (EpNotificationItem) getDataAccessService()
					.getDomainObject(EpNotificationItem.class, notificationItem.getNotificationId(), null);
			if(updateNotificationItem != null) {
				notificationItem.setRoles(updateNotificationItem.getRoles());
			}
		}
		if (notificationItem.msgSource == null) {
			notificationItem.setMsgSource("EP");
		}
		getDataAccessService().saveDomainObject(notificationItem, null);
		return "";

	}

	@Override
	public List<EPUser> getUsersByOrgIds(List<String> OrgIds) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("OrgIds", OrgIds);
		@SuppressWarnings("unchecked")
		List<EPUser> userList = dataAccessService.executeNamedQuery("getUsersByOrgIdsNotifications", params, null);
		return userList;
	}

	@Override
	public List<String> getMessageRecipients(Long notificationId) {
		Map<String, String> params = new HashMap<>();
		params.put("notificationId", Long.toString(notificationId));
		@SuppressWarnings("unchecked")
		List<String> activeUsers = dataAccessService.executeNamedQuery("messageRecipients", params, null);
		return activeUsers;
	}

	public DataAccessService getDataAccessService() {
		return dataAccessService;
	}

	public void setDataAccessService(DataAccessService dataAccessService) {
		this.dataAccessService = dataAccessService;
	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Override
	public void deleteNotificationsFromEpNotificationTable() {
		Map<String, String> params = new HashMap<String, String>();
		dataAccessService.executeNamedQuery("deleteNotificationsFromEpNotificationTable", params, null);
	}

	@Override
	public void deleteNotificationsFromEpUserNotificationTable() {
		Map<String, String> params = new HashMap<String, String>();
		dataAccessService.executeNamedQuery("deleteNotificationsFromEpUserNotificationTable", params, null);

	}

	@Override
	public void deleteNotificationsFromEpRoleNotificationTable() {
		Map<String, String> params = new HashMap<String, String>();
		dataAccessService.executeNamedQuery("deleteNotificationsFromEpRoleNotificationTable", params, null);
	}

}
