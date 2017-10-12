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

package org.openecomp.portalapp.portal.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.cxf.transport.http.HTTPException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openecomp.portalapp.externalsystemapproval.model.ExternalSystemRoleApproval;
import org.openecomp.portalapp.externalsystemapproval.model.ExternalSystemUser;
import org.openecomp.portalapp.portal.domain.EPApp;
import org.openecomp.portalapp.portal.domain.EPRole;
import org.openecomp.portalapp.portal.domain.EPUser;
import org.openecomp.portalapp.portal.domain.EPUserApp;
import org.openecomp.portalapp.portal.domain.EPUserAppCatalogRoles;
import org.openecomp.portalapp.portal.domain.EPUserAppRoles;
import org.openecomp.portalapp.portal.domain.EPUserAppRolesRequest;
import org.openecomp.portalapp.portal.domain.EPUserAppRolesRequestDetail;
import org.openecomp.portalapp.portal.domain.ExternalSystemAccess;
import org.openecomp.portalapp.portal.logging.aop.EPMetricsLog;
import org.openecomp.portalapp.portal.logging.format.EPAppMessagesEnum;
import org.openecomp.portalapp.portal.logging.logic.EPLogUtil;
import org.openecomp.portalapp.portal.transport.AppWithRolesForUser;
import org.openecomp.portalapp.portal.transport.CentralRole;
import org.openecomp.portalapp.portal.transport.EPUserAppCurrentRoles;
import org.openecomp.portalapp.portal.transport.EcompUserAppRoles;
import org.openecomp.portalapp.portal.transport.ExternalAccessUser;
import org.openecomp.portalapp.portal.transport.ExternalAccessUserRoleDetail;
import org.openecomp.portalapp.portal.transport.ExternalRequestFieldsValidator;
import org.openecomp.portalapp.portal.transport.ExternalRoleDescription;
import org.openecomp.portalapp.portal.transport.FieldsValidator;
import org.openecomp.portalapp.portal.transport.FunctionalMenuItem;
import org.openecomp.portalapp.portal.transport.FunctionalMenuRole;
import org.openecomp.portalapp.portal.transport.RemoteRole;
import org.openecomp.portalapp.portal.transport.RemoteUserWithRoles;
import org.openecomp.portalapp.portal.transport.RoleInAppForUser;
import org.openecomp.portalapp.portal.transport.RolesInAppForUser;
import org.openecomp.portalapp.portal.transport.UserApplicationRoles;
import org.openecomp.portalapp.portal.utils.EPCommonSystemProperties;
import org.openecomp.portalapp.portal.utils.EcompPortalUtils;
import org.openecomp.portalapp.portal.utils.PortalConstants;
import org.openecomp.portalsdk.core.domain.Role;
import org.openecomp.portalsdk.core.logging.logic.EELFLoggerDelegate;
import org.openecomp.portalsdk.core.restful.domain.EcompRole;
import org.openecomp.portalsdk.core.service.DataAccessService;
import org.openecomp.portalsdk.core.service.RoleService;
import org.openecomp.portalsdk.core.util.SystemProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

@EPMetricsLog
public class UserRolesCommonServiceImpl {

	private static EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(UserRolesCommonServiceImpl.class);

	private static final Object syncRests = new Object();

	@Autowired
	private DataAccessService dataAccessService;
	@Autowired
	private SessionFactory sessionFactory;
	@Autowired
	private SearchService searchService;
	@Autowired
	private EPAppService appsService;
	@Autowired
	private ApplicationsRestClientService applicationsRestClientService;
	@Autowired
	private EPRoleService epRoleService;
	@Autowired
	private RoleService roleService;

	@Autowired
	private ExternalAccessRolesService externalAccessRolesService;

	RestTemplate template = new RestTemplate();

	/**
	 * 
	 * @param ecompRoles
	 * @return HashMap<Long, EcompRole>
	 */
	private static HashMap<Long, EcompRole> hashMapFromEcompRoles(EcompRole[] ecompRoles) {
		HashMap<Long, EcompRole> result = new HashMap<Long, EcompRole>();
		if (ecompRoles != null) {
			for (int i = 0; i < ecompRoles.length; i++) {
				if (ecompRoles[i].getId() != null) {
					result.put(ecompRoles[i].getId(), ecompRoles[i]);
				}
			}
		}
		return result;
	}

	/**
	 * 
	 * @param userId
	 */
	protected void createLocalUserIfNecessary(String userId) {
		if (StringUtils.isEmpty(userId)) {
			logger.error(EELFLoggerDelegate.errorLogger, "createLocalUserIfNecessary : empty userId!");
			return;
		}
		Session localSession = null;
		Transaction transaction = null;
		try {
			localSession = sessionFactory.openSession();
			transaction = localSession.beginTransaction();
			@SuppressWarnings("unchecked")
			List<EPUser> userList = localSession
					.createQuery("from " + EPUser.class.getName() + " where orgUserId='" + userId + "'").list();
			if (userList.size() == 0) {
				EPUser client = searchService.searchUserByUserId(userId);
				if (client == null) {
					String msg = "createLocalUserIfNecessary: cannot create user " + userId
							+ ", because not found in phonebook";
					logger.error(EELFLoggerDelegate.errorLogger, msg);
				} else {
					client.setLoginId(userId);
					client.setActive(true);
					localSession.save(client);
				}
			}
			transaction.commit();
		} catch (Exception e) {
			EPLogUtil.logEcompError(logger, EPAppMessagesEnum.BeDaoSystemError, e);
			EcompPortalUtils.rollbackTransaction(transaction, "searchOrCreateUser rollback, exception = " + e);
		} finally {
			EcompPortalUtils.closeLocalSession(localSession, "searchOrCreateUser");
		}
	}

	/**
	 * This method return nothing and remove roles before adding any roles for an
	 * app
	 * 
	 * @param userRole
	 * @param appId
	 * @param localSession
	 * @param userAppRoles
	 * @param newUserAppRolesMap
	 */
	private static void syncUserRolesExtension(EPUserApp userRole, Long appId, Session localSession,
			EcompRole[] userAppRoles, HashMap<Long, EcompRole> newUserAppRolesMap) {

		Long userAppRoleId = 0L;
		if (appId == PortalConstants.PORTAL_APP_ID) { // local app
			userAppRoleId = userRole.getRoleId();
		} else { // remote app
			userAppRoleId = userRole.getAppRoleId();
		}

		if (!newUserAppRolesMap.containsKey(userAppRoleId)) {
			localSession.delete(userRole);
		} else {
			newUserAppRolesMap.remove(userAppRoleId);
		}
	}

	/**
	 * Checks whether the role is inactive
	 * 
	 * @param epRole
	 * @throws Exception
	 *             if role is inactive, throws exception
	 */
	private void checkIfRoleInactive(EPRole epRole) throws Exception {
		if (!epRole.getActive()) {
			throw new Exception(epRole.getName() + " role is unavailable");
		}
	}

	/**
	 * 
	 * @param sessionFactory
	 * @param userId
	 * @param appId
	 * @param userAppRoles
	 * @param extRequestValue
	 *            set to false if request is from users page otherwise true
	 * @throws Exception
	 */
	protected void syncUserRoles(SessionFactory sessionFactory, String userId, Long appId, EcompRole[] userAppRoles,
			Boolean extRequestValue, String reqType) throws Exception {
		boolean result = false;
		Session localSession = null;
		Transaction transaction = null;
		String roleActive = null;
		final Map<String, String> userAppParams = new HashMap<>();
		final Map<String, String> appParams = new HashMap<>();
		HashMap<Long, EcompRole> newUserAppRolesMap = hashMapFromEcompRoles(userAppRoles);

		try {
			localSession = sessionFactory.openSession();
			transaction = localSession.beginTransaction();
			@SuppressWarnings("unchecked")
			List<EPUser> userList = localSession
					.createQuery("from " + EPUser.class.getName() + " where orgUserId='" + userId + "'").list();
			if (userList.size() > 0) {
				EPUser client = userList.get(0);
				roleActive = ("DELETE".equals(reqType)) ? "" : " and role.active = 'Y'";
				@SuppressWarnings("unchecked")
				List<EPUserApp> userRoles = localSession.createQuery("from " + EPUserApp.class.getName()
						+ " where app.id=" + appId + roleActive + " and userId=" + client.getId()).list();

				if ("DELETE".equals(reqType)) {
					for (EPUserApp userAppRoleList : userRoles) {
						userAppParams.put("roleName", String.valueOf(userAppRoleList.getRole().getName()));
						userAppParams.put("appId", String.valueOf(appId));
						appParams.put("appRoleName", userAppRoleList.getRole().getName());
						@SuppressWarnings("unchecked")
						List<EPRole> rolesList = (!userAppRoleList.getRole().getName()
								.equals(PortalConstants.ADMIN_ROLE))
										? (List<EPRole>) dataAccessService.executeNamedQuery("getAppRoles",
												userAppParams, null)
										: (List<EPRole>) dataAccessService.executeNamedQuery("getPortalAppRoles",
												appParams, null);
						if (rolesList.size() > 0 || !rolesList.isEmpty()) {
							checkIfRoleInactive(rolesList.get(0));
						}
					}
				}

				for (EPUserApp userRole : userRoles) {
					if (!userRole.getRoleId().equals(PortalConstants.ACCOUNT_ADMIN_ROLE_ID)
							&& userRole.getRoleId() != PortalConstants.SYS_ADMIN_ROLE_ID && !extRequestValue) {
						syncUserRolesExtension(userRole, appId, localSession, userAppRoles, newUserAppRolesMap);
					} else if (extRequestValue
							&& ("PUT".equals(reqType) || "POST".equals(reqType) || "DELETE".equals(reqType))) {
						syncUserRolesExtension(userRole, appId, localSession, userAppRoles, newUserAppRolesMap);
					} else if (extRequestValue && !userRole.getRoleId().equals(PortalConstants.ACCOUNT_ADMIN_ROLE_ID)) {
						syncUserRolesExtension(userRole, appId, localSession, userAppRoles, newUserAppRolesMap);
					}
				}
				Collection<EcompRole> newRolesToAdd = newUserAppRolesMap.values();
				if (newRolesToAdd.size() > 0) {
					EPApp app = (EPApp) localSession.get(EPApp.class, appId);

					HashMap<Long, EPRole> rolesMap = new HashMap<Long, EPRole>();
					if (appId.equals(PortalConstants.PORTAL_APP_ID)) { // local app
						String appIdValue = "";
						if (!extRequestValue) {
							appIdValue = "and id != " + PortalConstants.SYS_ADMIN_ROLE_ID;
						}
						@SuppressWarnings("unchecked")
						List<EPRole> roles = localSession
								.createQuery("from " + EPRole.class.getName() + " where appId is null " + appIdValue)
								.list();
						for (EPRole role : roles) {
							role.setAppId(1L);
							rolesMap.put(role.getId(), role);
						}
					} else { // remote app
						@SuppressWarnings("unchecked")
						List<EPRole> roles = localSession
								.createQuery("from " + EPRole.class.getName() + " where appId=" + appId).list();
						for (EPRole role : roles) {
							if (!extRequestValue && app.getCentralAuth()) {
								rolesMap.put(role.getId(), role);
							} else {
								rolesMap.put(role.getAppRoleId(), role);
							}
						}
					}

					EPRole role = null;
					for (EcompRole userRole : newRolesToAdd) {
						EPUserApp userApp = new EPUserApp();
						if (("PUT".equals(reqType) || "POST".equals(reqType))
								&& userRole.getName().equals(PortalConstants.ADMIN_ROLE)) {
							role = (EPRole) localSession.get(EPRole.class,
									new Long(PortalConstants.ACCOUNT_ADMIN_ROLE_ID));
							userApp.setRole(role);
						} else if ((userRole.getId().equals(PortalConstants.ACCOUNT_ADMIN_ROLE_ID))
								&& !extRequestValue) {
							continue;
						} else if ((userRole.getId().equals(PortalConstants.SYS_ADMIN_ROLE_ID))
								&& app.getId().equals(PortalConstants.PORTAL_APP_ID) && !extRequestValue) {
							continue;
						} else {
							userApp.setRole(rolesMap.get(userRole.getId()));
						}

						userApp.setUserId(client.getId());
						userApp.setApp(app);
						localSession.save(userApp);
						localSession.flush();
					}

					if (appId == PortalConstants.PORTAL_APP_ID) {
						/*
						 * for local app -- hack - always make sure fn_role table's app_id is null and
						 * not 1 for primary app in this case being ecomp portal app; reason: hibernate
						 * is rightly setting this to 1 while persisting to fn_role as per the mapping
						 * but SDK role management code expects the app_id to be null as there is no
						 * concept of App_id in SDK
						 */
						localSession.flush();
						SQLQuery sqlQuery = localSession
								.createSQLQuery("update fn_role set app_id = null where app_id = 1 ");
						sqlQuery.executeUpdate();

					}
				}
			}
			transaction.commit();
			result = true;
		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.errorLogger, "syncUserRoles failed", e);
			EPLogUtil.logEcompError(logger, EPAppMessagesEnum.BeDaoSystemError, e);
			EcompPortalUtils.rollbackTransaction(transaction,
					"Exception occurred in syncUserRoles, Details: " + e.toString());
			if ("DELETE".equals(reqType)) {
				throw new Exception(e.getMessage());
			}
		} finally {
			localSession.close();
			if (!result && !"DELETE".equals(reqType)) {
				throw new Exception(
						"Exception occurred in syncUserRoles while closing database session for app: '" + appId + "'.");
			}
		}
	}

	/**
	 * Called when getting the list of roles for the user
	 * 
	 * @param appRoles
	 * @param userAppRoles
	 * @return List<RoleInAppForUser>
	 */
	protected List<RoleInAppForUser> constructRolesInAppForUserGet(EcompRole[] appRoles, EcompRole[] userAppRoles) {
		List<RoleInAppForUser> rolesInAppForUser = new ArrayList<RoleInAppForUser>();

		Set<Long> userAppRolesMap = new HashSet<Long>();
		if (userAppRoles != null) {
			for (EcompRole ecompRole : userAppRoles) {
				userAppRolesMap.add(ecompRole.getId());
			}
		} else {
			logger.error(EELFLoggerDelegate.errorLogger,
					"constructRolesInAppForUserGet has received userAppRoles list empty");
		}

		if (appRoles != null) {
			for (EcompRole ecompRole : appRoles) {
				RoleInAppForUser roleForUser = new RoleInAppForUser(ecompRole.getId(), ecompRole.getName());
				roleForUser.isApplied = userAppRolesMap.contains(ecompRole.getId());
				rolesInAppForUser.add(roleForUser);
			}
		} else {
			logger.error(EELFLoggerDelegate.errorLogger, "constructRolesInAppForUser has received appRoles list empty");
		}
		return rolesInAppForUser;
	}

	/**
	 * Called when getting the list of roles for the user
	 * 
	 * @param appRoles
	 * @param userAppRoles
	 * @param extRequestValue
	 *            set to false if request is from users page otherwise true
	 * @return List<RoleInAppForUser>
	 */
	protected List<RoleInAppForUser> constructRolesInAppForUserGet(List<Role> appRoles, EPRole[] userAppRoles,
			Boolean extRequestValue) {
		List<RoleInAppForUser> rolesInAppForUser = new ArrayList<RoleInAppForUser>();

		Set<Long> userAppRolesMap = new HashSet<Long>();
		if (userAppRoles != null) {
			for (EPRole ecompRole : userAppRoles) {
				userAppRolesMap.add(ecompRole.getId());
			}
		} else {
			logger.error(EELFLoggerDelegate.errorLogger,
					"constructRolesInAppForUserGet has received userAppRoles list empty.");
		}

		if (appRoles != null) {
			for (Role ecompRole : appRoles) {
				if (ecompRole.getId().equals(PortalConstants.ACCOUNT_ADMIN_ROLE_ID) && !extRequestValue)
					continue;
				RoleInAppForUser roleForUser = new RoleInAppForUser(ecompRole.getId(), ecompRole.getName());
				roleForUser.isApplied = userAppRolesMap.contains(ecompRole.getId());
				rolesInAppForUser.add(roleForUser);
			}
		} else {
			logger.error(EELFLoggerDelegate.errorLogger,
					"constructRolesInAppForUser has received appRoles list empty.");
		}
		return rolesInAppForUser;
	}

	/**
	 * copies of methods in GetAppsWithUserRoleState
	 * 
	 * @param sessionFactory
	 * @param appId
	 * @param appRoles
	 * @throws Exception
	 */
	protected void syncAppRoles(SessionFactory sessionFactory, Long appId, EcompRole[] appRoles) throws Exception {
		logger.debug(EELFLoggerDelegate.debugLogger, "entering syncAppRoles for appId: " + appId);
		HashMap<Long, EcompRole> newRolesMap = hashMapFromEcompRoles(appRoles);
		Session localSession = null;
		Transaction transaction = null;

		try {
			localSession = sessionFactory.openSession();
			transaction = localSession.beginTransaction();
			// Attention! All roles from remote application supposed to be
			// active!
			@SuppressWarnings("unchecked")
			List<EPRole> currentAppRoles = localSession
					.createQuery("from " + EPRole.class.getName() + " where appId=" + appId).list();
			List<EPRole> obsoleteRoles = new ArrayList<EPRole>();
			for (int i = 0; i < currentAppRoles.size(); i++) {
				EPRole oldAppRole = currentAppRoles.get(i);
				if (oldAppRole.getAppRoleId() != null) {
					EcompRole role = null;
					role = newRolesMap.get(oldAppRole.getAppRoleId());
					if (role != null) {
						if (!(role.getName() == null || oldAppRole.getName().equals(role.getName()))) {
							oldAppRole.setName(role.getName());
							localSession.update(oldAppRole);
						}
						newRolesMap.remove(oldAppRole.getAppRoleId());
					} else {
						obsoleteRoles.add(oldAppRole);
					}
				} else {
					obsoleteRoles.add(oldAppRole);
				}
			}
			Collection<EcompRole> newRolesToAdd = newRolesMap.values();
			if (obsoleteRoles.size() > 0) {
				logger.debug(EELFLoggerDelegate.debugLogger, "syncAppRoles: we have obsolete roles to delete");
				for (EPRole role : obsoleteRoles) {
					logger.debug(EELFLoggerDelegate.debugLogger, "syncAppRoles: obsolete role: " + role.toString());
					Long roleId = role.getId();
					// delete obsolete roles here
					// Must delete all records with foreign key constraints on
					// fn_role:
					// fn_user_role, fn_role_composite, fn_role_function,
					// fn_user_pseudo_role, fn_menu_functional_roles.
					// And for fn_menu_functional, if no other roles for that
					// menu item, remove the url.

					// Delete from fn_user_role
					@SuppressWarnings("unchecked")
					List<EPUserApp> userRoles = localSession.createQuery(
							"from " + EPUserApp.class.getName() + " where app.id=" + appId + " and role_id=" + roleId)
							.list();

					logger.debug(EELFLoggerDelegate.debugLogger,
							"syncAppRoles: number of userRoles to delete: " + userRoles.size());
					for (EPUserApp userRole : userRoles) {
						logger.debug(EELFLoggerDelegate.debugLogger,
								"syncAppRoles: about to delete userRole: " + userRole.toString());
						localSession.delete(userRole);
						logger.debug(EELFLoggerDelegate.debugLogger,
								"syncAppRoles: finished deleting userRole: " + userRole.toString());
					}

					// Delete from fn_menu_functional_roles
					@SuppressWarnings("unchecked")
					List<FunctionalMenuRole> funcMenuRoles = localSession
							.createQuery("from " + FunctionalMenuRole.class.getName() + " where roleId=" + roleId)
							.list();
					int numMenuRoles = funcMenuRoles.size();
					logger.debug(EELFLoggerDelegate.debugLogger,
							"syncAppRoles: number of funcMenuRoles for roleId: " + roleId + ": " + numMenuRoles);
					for (FunctionalMenuRole funcMenuRole : funcMenuRoles) {
						Long menuId = funcMenuRole.menuId;
						// If this is the only role for this menu item, then the
						// app and roles will be gone,
						// so must null out the url too, to be consistent
						@SuppressWarnings("unchecked")
						List<FunctionalMenuRole> funcMenuRoles2 = localSession
								.createQuery("from " + FunctionalMenuRole.class.getName() + " where menuId=" + menuId)
								.list();
						int numMenuRoles2 = funcMenuRoles2.size();
						logger.debug(EELFLoggerDelegate.debugLogger,
								"syncAppRoles: number of funcMenuRoles for menuId: " + menuId + ": " + numMenuRoles2);
						localSession.delete(funcMenuRole);
						if (numMenuRoles2 == 1) {
							// If this is the only role for this menu item, then
							// the app and roles will be gone,
							// so must null out the url too, to be consistent
							logger.debug(EELFLoggerDelegate.debugLogger,
									"syncAppRoles: There is exactly 1 menu item for this role, so emptying the url");
							@SuppressWarnings("unchecked")
							List<FunctionalMenuItem> funcMenuItems = localSession
									.createQuery(
											"from " + FunctionalMenuItem.class.getName() + " where menuId=" + menuId)
									.list();
							if (funcMenuItems.size() > 0) {
								logger.debug(EELFLoggerDelegate.debugLogger, "got the menu item");
								FunctionalMenuItem funcMenuItem = funcMenuItems.get(0);
								funcMenuItem.url = "";
								localSession.update(funcMenuItem);
							}
						}
					}
					externalAccessRolesService.deleteRoleDependencyRecords(localSession, roleId, appId);
					logger.debug(EELFLoggerDelegate.debugLogger,
							"syncAppRoles: about to delete the role: " + role.toString());
					localSession.delete(role);
					localSession.flush();
					logger.debug(EELFLoggerDelegate.debugLogger, "syncAppRoles: deleted the role");
				}
			}
			for (EcompRole role : newRolesToAdd) {
				logger.debug(EELFLoggerDelegate.debugLogger,
						"syncAppRoles: about to add missing role: " + role.toString());
				EPRole newRole = new EPRole();
				// Attention! All roles from remote application supposed to be
				// active!
				newRole.setActive(true);
				newRole.setName(role.getName());
				newRole.setAppId(appId);
				newRole.setAppRoleId(role.getId());
				localSession.save(newRole);
			}
			logger.debug(EELFLoggerDelegate.debugLogger, "syncAppRoles: about to commit the transaction");
			transaction.commit();
			logger.debug(EELFLoggerDelegate.debugLogger, "syncAppRoles: committed the transaction");
		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.errorLogger, "syncAppRoles failed", e);
			EPLogUtil.logEcompError(logger, EPAppMessagesEnum.BeDaoSystemError, e);
			EcompPortalUtils.rollbackTransaction(transaction,
					"Exception occurred in syncAppRoles, Details: " + e.toString());
			throw new Exception(e);
		} finally {
			localSession.close();
		}
	}

	/**
	 * Called when updating the list of roles for the user
	 * 
	 * @param userId
	 * @param appId
	 * @param userRolesInRemoteApp
	 * @return RolesInAppForUser
	 */
	protected RolesInAppForUser constructRolesInAppForUserUpdate(String userId, Long appId,
			Set<EcompRole> userRolesInRemoteApp) {
		RolesInAppForUser result;
		result = new RolesInAppForUser();
		result.appId = appId;
		result.orgUserId = userId;
		for (EcompRole role : userRolesInRemoteApp) {
			RoleInAppForUser roleInAppForUser = new RoleInAppForUser();
			roleInAppForUser.roleId = role.getId();
			roleInAppForUser.roleName = role.getName();
			roleInAppForUser.isApplied = new Boolean(true);
			result.roles.add(roleInAppForUser);
		}
		return result;
	}

	/**
	 * 
	 * @param roleInAppForUserList
	 * @return boolean
	 */
	protected boolean remoteUserShouldBeCreated(List<RoleInAppForUser> roleInAppForUserList) {
		for (RoleInAppForUser roleInAppForUser : roleInAppForUserList) {
			if (roleInAppForUser.isApplied.booleanValue()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Builds JSON and posts it to a remote application to update user roles.
	 * 
	 * @param roleInAppForUserList
	 * @param mapper
	 * @param applicationsRestClientService
	 * @param appId
	 * @param userId
	 * @return Set of roles as sent; NOT the response from the app.
	 * @throws JsonProcessingException
	 * @throws HTTPException
	 */
	protected Set<EcompRole> postUsersRolesToRemoteApp(List<RoleInAppForUser> roleInAppForUserList, ObjectMapper mapper,
			ApplicationsRestClientService applicationsRestClientService, Long appId, String userId)
			throws JsonProcessingException, HTTPException {
		Set<EcompRole> updatedUserRolesinRemote = constructUsersRemoteAppRoles(roleInAppForUserList);
		Set<EcompRole> updateUserRolesInEcomp = constructUsersEcompRoles(roleInAppForUserList);
		String userRolesAsString = mapper.writeValueAsString(updatedUserRolesinRemote);
		applicationsRestClientService.post(EcompRole.class, appId, userRolesAsString,
				String.format("/user/%s/roles", userId));
		// TODO: We should add code that verifies that the post operation did
		// succeed. Because the SDK may still return 200 OK with an html page
		// even when it fails!
		return updateUserRolesInEcomp;
	}

	/**
	 * 
	 * @param roleInAppForUserList
	 * @return Set<EcompRole>
	 */
	protected Set<EcompRole> constructUsersEcompRoles(List<RoleInAppForUser> roleInAppForUserList) {
		Set<EcompRole> existingUserRoles = new TreeSet<EcompRole>();
		for (RoleInAppForUser roleInAppForUser : roleInAppForUserList) {
			if (roleInAppForUser.isApplied) {
				EcompRole ecompRole = new EcompRole();
				ecompRole.setId(roleInAppForUser.roleId);
				ecompRole.setName(roleInAppForUser.roleName);
				existingUserRoles.add(ecompRole);
			}
		}
		return existingUserRoles;
	}

	/**
	 * Constructs user app roles excluding Account Administrator role
	 * 
	 * @param roleInAppForUserList
	 * @return List of roles with Role name, Role Id
	 */
	protected Set<EcompRole> constructUsersRemoteAppRoles(List<RoleInAppForUser> roleInAppForUserList) {
		Set<EcompRole> existingUserRoles = new TreeSet<EcompRole>();
		for (RoleInAppForUser roleInAppForUser : roleInAppForUserList) {
			if (roleInAppForUser.isApplied
					&& !roleInAppForUser.getRoleId().equals(PortalConstants.ACCOUNT_ADMIN_ROLE_ID)) {
				EcompRole ecompRole = new EcompRole();
				ecompRole.setId(roleInAppForUser.roleId);
				ecompRole.setName(roleInAppForUser.roleName);
				existingUserRoles.add(ecompRole);
			}
		}
		return existingUserRoles;
	}

	/**
	 * This is for a single app
	 * 
	 * @param rolesInAppForUser
	 * @param externalSystemRequest
	 *            set to false if requests from Users page otherwise true
	 * @return true on success, false otherwise
	 */
	protected boolean applyChangesInUserRolesForAppToEcompDB(RolesInAppForUser rolesInAppForUser,
			boolean externalSystemRequest, String reqType) throws Exception {
		boolean result = false;
		String userId = rolesInAppForUser.orgUserId;
		Long appId = rolesInAppForUser.appId;
		synchronized (syncRests) {
			if (rolesInAppForUser != null) {
				createLocalUserIfNecessary(userId);
			}

			if (rolesInAppForUser != null) {
				EcompRole[] userAppRoles = new EcompRole[rolesInAppForUser.roles.stream().distinct()
						.collect(Collectors.toList()).size()];
				for (int i = 0; i < rolesInAppForUser.roles.stream().distinct().collect(Collectors.toList())
						.size(); i++) {
					RoleInAppForUser roleInAppForUser = rolesInAppForUser.roles.get(i);
					EcompRole role = new EcompRole();
					role.setId(roleInAppForUser.roleId);
					role.setName(roleInAppForUser.roleName);
					userAppRoles[i] = role;
				}
				try {
					syncUserRoles(sessionFactory, userId, appId, userAppRoles, externalSystemRequest, reqType);
					result = true;
				} catch (Exception e) {
					logger.error(EELFLoggerDelegate.errorLogger,
							"applyChangesInUserRolesForAppToEcompDB: failed to syncUserRoles for orgUserId " + userId,
							e);
					if ("DELETE".equals(reqType)) {
						throw new Exception(e.getMessage());
					}
				}
			}
		}
		return result;
	}

	/**
	 * 
	 * @param appId
	 * @param remoteUser
	 * @return UserApplicationRoles
	 */
	protected UserApplicationRoles convertToUserApplicationRoles(Long appId, RemoteUserWithRoles remoteUser) {
		UserApplicationRoles userWithRemoteAppRoles = new UserApplicationRoles();
		userWithRemoteAppRoles.setAppId(appId);
		userWithRemoteAppRoles.setOrgUserId(remoteUser.getOrgUserId());
		userWithRemoteAppRoles.setFirstName(remoteUser.getFirstName());
		userWithRemoteAppRoles.setLastName(remoteUser.getLastName());
		userWithRemoteAppRoles.setRoles(remoteUser.getRoles());
		return userWithRemoteAppRoles;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openecomp.portalapp.portal.service.UserRolesService#
	 * importRolesFromRemoteApplication(java.lang.Long)
	 */
	public List<EPRole> importRolesFromRemoteApplication(Long appId) throws HTTPException {
		EPRole[] appRolesFull = applicationsRestClientService.get(EPRole[].class, appId, "/rolesFull");
		List<EPRole> rolesList = Arrays.asList(appRolesFull);
		for (EPRole externalAppRole : rolesList) {

			// Try to find an existing extern role for the app in the local
			// ecomp DB. If so, then use its id to update the existing external
			// application role record.
			Long externAppId = externalAppRole.getId();
			EPRole existingAppRole = epRoleService.getRole(appId, externAppId);
			if (existingAppRole != null) {
				logger.debug(EELFLoggerDelegate.debugLogger,
						String.format("ecomp role already exists for app=%s; appRoleId=%s. No need to import this one.",
								appId, externAppId));
				continue;
			}
			// persistExternalRoleInEcompDb(externalAppRole, appId,
			// roleService);
		}

		return rolesList;
	}

	/**
	 * It adds new user for remote application
	 * 
	 * @param roleInAppForUserList
	 * @param remoteAppUser
	 * @param userId
	 * @param app
	 * @param mapper
	 * @param searchService
	 * @param applicationsRestClientService
	 * @return
	 * @throws Exception
	 */
	private EPUser addRemoteUser(List<RoleInAppForUser> roleInAppForUserList, String userId, EPApp app,
			ObjectMapper mapper, SearchService searchService,
			ApplicationsRestClientService applicationsRestClientService) throws Exception {
		EPUser addRemoteUser = null;
		if (remoteUserShouldBeCreated(roleInAppForUserList)) {

			createNewUserOnRemoteApp(userId, app, applicationsRestClientService, searchService, mapper,
					isAppUpgradeVersion(app));
			// If we succeed, we know that the new user was
			// persisted on remote app.
			addRemoteUser = getUserFromApp(userId, app, applicationsRestClientService);
			if (addRemoteUser == null) {
				logger.error(EELFLoggerDelegate.errorLogger,
						"Failed to persist new user: " + userId + " in remote app. appId = " + app.getId());
				// return null;
			}
		}
		return addRemoteUser;
	}

	/**
	 * It checks whether the remote user exists or not if exits returns user object
	 * else null
	 * 
	 * @param userId
	 * @param app
	 * @param applicationsRestClientService
	 * @return
	 * @throws HTTPException
	 */
	private EPUser checkIfRemoteUserExits(String userId, EPApp app,
			ApplicationsRestClientService applicationsRestClientService) throws HTTPException {
		EPUser checkRemoteUser = null;
		try {
			checkRemoteUser = getUserFromApp(userId, app, applicationsRestClientService);
		} catch (HTTPException e) {
			// Some apps are returning 400 if user is not found.
			if (e.getResponseCode() == 400) {
				logger.debug(EELFLoggerDelegate.debugLogger,
						"setAppWithUserRoleStateForUser: getuserFromApp threw exception with response code 400; continuing",
						e);
			} else if (e.getResponseCode() == 404) {
				logger.debug(EELFLoggerDelegate.debugLogger,
						"setAppWithUserRoleStateForUser: getuserFromApp threw exception with response code 404; continuing",
						e);
			} else {
				// Other response code, let it come thru.
				throw e;
			}
		}
		return checkRemoteUser;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openecomp.portalapp.portal.service.UserRolesService#
	 * setAppWithUserRoleStateForUser(org.openecomp.portalapp.portal.domain. EPUser,
	 * org.openecomp.portalapp.portal.transport.AppWithRolesForUser)
	 */
	public boolean setAppWithUserRoleStateForUser(EPUser user, AppWithRolesForUser newAppRolesForUser) {
		boolean result = false;
		boolean epRequestValue = false;
		String userId = "";
		if (newAppRolesForUser != null && newAppRolesForUser.orgUserId != null) {
			userId = newAppRolesForUser.orgUserId.trim();
		}
		Long appId = newAppRolesForUser.appId;
		List<RoleInAppForUser> roleInAppForUserList = newAppRolesForUser.appRoles;
		if (userId.length() > 0) {
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

			try {
				EPApp app = appsService.getApp(appId);
				applyChangesToUserAppRolesForMyLoginsRequest(user, appId);

				// if centralized app
				if (app.getCentralAuth()) {
					// We should add If user does not exist in remote application
					if (!app.getId().equals(PortalConstants.PORTAL_APP_ID)) {
						EPUser remoteAppUser = null;
						remoteAppUser = checkIfRemoteUserExits(userId, app, applicationsRestClientService);

						if (remoteAppUser == null) {
							addRemoteUser(roleInAppForUserList, userId, app, mapper, searchService,
									applicationsRestClientService);
						}
					}

					Set<EcompRole> userRolesInLocalApp = postUsersRolesToLocalApp(roleInAppForUserList, mapper,
							applicationsRestClientService, appId, userId);
					RolesInAppForUser rolesInAppForUser = constructRolesInAppForUserUpdate(userId, appId,
							userRolesInLocalApp);
					List<RoleInAppForUser> roleAppUserList = rolesInAppForUser.roles;
					// Apply changes in external Access system
					updateUserRolesInExternalSystem(app, rolesInAppForUser.orgUserId, roleAppUserList, epRequestValue);
					result = applyChangesInUserRolesForAppToEcompDB(rolesInAppForUser, epRequestValue, "Portal");
				}
				// In case if portal is not centralized then follow existing approach
				else if (!app.getCentralAuth() && app.getId().equals(PortalConstants.PORTAL_APP_ID)) {
					Set<EcompRole> userRolesInLocalApp = postUsersRolesToLocalApp(roleInAppForUserList, mapper,
							applicationsRestClientService, appId, userId);
					RolesInAppForUser rolesInAppForUser = constructRolesInAppForUserUpdate(userId, appId,
							userRolesInLocalApp);
					result = applyChangesInUserRolesForAppToEcompDB(rolesInAppForUser, epRequestValue, "Portal");
				} else {// remote app
					EPUser remoteAppUser = null;
					if (!app.getCentralAuth() && !app.getId().equals(PortalConstants.PORTAL_APP_ID)) {

						remoteAppUser = checkIfRemoteUserExits(userId, app, applicationsRestClientService);

						if (remoteAppUser == null) {
							remoteAppUser = addRemoteUser(roleInAppForUserList, userId, app, mapper, searchService,
									applicationsRestClientService);
						}
						if (remoteAppUser != null) {
							Set<EcompRole> userRolesInRemoteApp = postUsersRolesToRemoteApp(roleInAppForUserList,
									mapper, applicationsRestClientService, appId, userId);
							RolesInAppForUser rolesInAppForUser = constructRolesInAppForUserUpdate(userId, appId,
									userRolesInRemoteApp);
							result = applyChangesInUserRolesForAppToEcompDB(rolesInAppForUser, epRequestValue, null);

							// If no roles remain, request app to set user inactive.
							if (userRolesInRemoteApp.size() == 0) {
								logger.debug(EELFLoggerDelegate.debugLogger,
										"setAppWithUserRoleStateForUser: no roles in app {}, set user {} to inactive",
										app, userId);
								remoteAppUser.setActive(false);
								postUserToRemoteApp(userId, user, app, applicationsRestClientService);
							}
						}
					}
				}
			} catch (Exception e) {
				String message = String.format("Failed to create user or update user roles for User %s, AppId %s",
						userId, Long.toString(appId));
				logger.error(EELFLoggerDelegate.errorLogger, message, e);
				result = false;
			}

		}
		return result;
	}

	/**
	 * It adds user roles in External system and also make data consistent in both
	 * local and in External System
	 * 
	 * @param app
	 *            details
	 * @param orgUserId
	 * @param roleInAppUser
	 *            Contains list of active roles
	 */
	@SuppressWarnings("unchecked")
	private void updateUserRolesInExternalSystem(EPApp app, String orgUserId, List<RoleInAppForUser> roleInAppUser,
			boolean isPortalRequest) {
		try {
			// check if user exists
			final Map<String, String> userParams = new HashMap<>();
			userParams.put("orgUserIdValue", orgUserId);
			List<EPUser> userInfo = checkIfUserExists(userParams);
			if (userInfo.isEmpty()) {
				createLocalUserIfNecessary(orgUserId);
			}
			String name = "";
			if (EPCommonSystemProperties
					.containsProperty(EPCommonSystemProperties.EXTERNAL_CENTRAL_ACCESS_USER_DOMAIN)) {
				name = orgUserId
						+ SystemProperties.getProperty(EPCommonSystemProperties.EXTERNAL_CENTRAL_ACCESS_USER_DOMAIN);
			}
			ObjectMapper mapper = new ObjectMapper();
			HttpHeaders headers = EcompPortalUtils.base64encodeKeyForAAFBasicAuth();
			HttpEntity<String> getUserRolesEntity = new HttpEntity<>(headers);
			logger.debug(EELFLoggerDelegate.debugLogger, "Connecting to external system to get current user roles");
			ResponseEntity<String> getResponse = template
					.exchange(SystemProperties.getProperty(EPCommonSystemProperties.EXTERNAL_CENTRAL_ACCESS_URL)
							+ "roles/user/" + name, HttpMethod.GET, getUserRolesEntity, String.class);
			if (getResponse.getStatusCode().value() == 200) {
				logger.debug(EELFLoggerDelegate.debugLogger,
						"updateUserRolesInExternalSystem: Finished GET user roles from external system and received user roles {}",
						getResponse.getBody());

			}
			List<ExternalAccessUserRoleDetail> userRoleDetailList = new ArrayList<>();
			String res = getResponse.getBody();
			JSONObject jsonObj = null;
			JSONArray extRoles = null;
			if (!res.equals("{}")) {
				jsonObj = new JSONObject(res);
				extRoles = jsonObj.getJSONArray("role");
			}
			ExternalAccessUserRoleDetail userRoleDetail = null;
			if (extRoles != null) {
				for (int i = 0; i < extRoles.length(); i++) {
					if (extRoles.getJSONObject(i).getString("name").startsWith(app.getNameSpace() + ".")
							&& !extRoles.getJSONObject(i).getString("name").equals(app.getNameSpace() + ".admin")
							&& !extRoles.getJSONObject(i).getString("name").equals(app.getNameSpace() + ".owner")) {
						ObjectMapper descMapper = new ObjectMapper();
						if (extRoles.getJSONObject(i).has("description")) {
							ExternalRoleDescription desc = descMapper.readValue(
									extRoles.getJSONObject(i).getString("description"), ExternalRoleDescription.class);
							userRoleDetail = new ExternalAccessUserRoleDetail(
									extRoles.getJSONObject(i).getString("name"), desc);
							userRoleDetailList.add(userRoleDetail);
						} else {
							userRoleDetail = new ExternalAccessUserRoleDetail(
									extRoles.getJSONObject(i).getString("name"), null);
							userRoleDetailList.add(userRoleDetail);
						}

					}
				}
			}
			// If request coming from portal not from external role approval system then we
			// have to check if user already
			// have account admin or system admin as GUI will not send these roles
			if (!isPortalRequest) {
				final Map<String, String> loginIdParams = new HashMap<>();
				loginIdParams.put("orgUserIdValue", orgUserId);
				EPUser user = (EPUser) dataAccessService.executeNamedQuery("epUserAppId", loginIdParams, null).get(0);
				final Map<String, Long> params = new HashMap<>();
				params.put("appId", app.getId());
				params.put("userId", user.getId());
				List<EcompUserAppRoles> userAppList = dataAccessService.executeNamedQuery("getUserAppExistingRoles",
						params, null);
				if (!roleInAppUser.isEmpty()) {
					for (EcompUserAppRoles userApp : userAppList) {
						if (userApp.getRoleId().equals(PortalConstants.SYS_ADMIN_ROLE_ID)
								|| userApp.getRoleId().equals(PortalConstants.ACCOUNT_ADMIN_ROLE_ID)) {
							RoleInAppForUser addSpecialRole = new RoleInAppForUser();
							addSpecialRole.setIsApplied(true);
							addSpecialRole.setRoleId(userApp.getRoleId());
							addSpecialRole.setRoleName(userApp.getRoleName());
							roleInAppUser.add(addSpecialRole);
						}
					}
				}
			}
			List<RoleInAppForUser> roleInAppUserNonDupls = roleInAppUser.stream().distinct()
					.collect(Collectors.toList());
			final Map<String, RoleInAppForUser> currentUserRolesToUpdate = new HashMap<>();
			for (RoleInAppForUser roleInAppUserNew : roleInAppUser) {
				currentUserRolesToUpdate.put(roleInAppUserNew.getRoleName(), roleInAppUserNew);
			}
			final Map<String, ExternalAccessUserRoleDetail> currentUserRolesInExternalSystem = new HashMap<>();
			for (ExternalAccessUserRoleDetail extAccessUserRole : userRoleDetailList) {
				currentUserRolesInExternalSystem.put(extAccessUserRole.getName(), extAccessUserRole);
			}
			// Check if roles does not exists in local but still there in External Central
			// Auth System delete them all
			for (ExternalAccessUserRoleDetail userRole : userRoleDetailList) {
				if (!(currentUserRolesToUpdate
						.containsKey(userRole.getName().substring(app.getNameSpace().length() + 1).replaceAll("_", " "))
						|| currentUserRolesToUpdate
								.containsKey(userRole.getName().substring(app.getNameSpace().length() + 1)))) {
					HttpEntity<String> entity = new HttpEntity<>(headers);
					logger.debug(EELFLoggerDelegate.debugLogger,
							"updateUserRolesInExternalSystem: Connecting to external system to DELETE user role {}",
							userRole);
					ResponseEntity<String> deleteResponse = template.exchange(
							SystemProperties.getProperty(EPCommonSystemProperties.EXTERNAL_CENTRAL_ACCESS_URL)
									+ "userRole/" + name + "/" + userRole.getName(),
							HttpMethod.DELETE, entity, String.class);
					logger.debug(EELFLoggerDelegate.debugLogger,
							"updateUserRolesInExternalSystem: Finished DELETE operation in external system for user role {} and the response is {}",
							userRole, deleteResponse.getBody());
				}
			}
			// Check if user roles does not exists in External Central Auth System add them
			// all
			for (RoleInAppForUser addUserRole : roleInAppUserNonDupls) {
				if (!(currentUserRolesInExternalSystem
						.containsKey(app.getNameSpace() + "." + addUserRole.getRoleName().replaceAll(" ", "_")))) {
					ExternalAccessUser extUser = new ExternalAccessUser(name,
							app.getNameSpace() + "." + addUserRole.getRoleName().replaceAll(" ", "_"));
					String formattedUserRole = mapper.writeValueAsString(extUser);
					HttpEntity<String> entity = new HttpEntity<>(formattedUserRole, headers);
					logger.debug(EELFLoggerDelegate.debugLogger,
							"updateUserRolesInExternalSystem: Connecting to external system and adding user role",
							addUserRole.getRoleName());
					ResponseEntity<String> addResponse = template
							.exchange(SystemProperties.getProperty(EPCommonSystemProperties.EXTERNAL_CENTRAL_ACCESS_URL)
									+ "userRole", HttpMethod.POST, entity, String.class);
					logger.debug(EELFLoggerDelegate.debugLogger,
							"updateUserRolesInExternalSystem: Finished adding user role in external system {} and added user role {}",
							getResponse.getBody(), addUserRole.getRoleName());
					if (addResponse.getStatusCode().value() != 201 && addResponse.getStatusCode().value() != 404) {
						logger.debug(EELFLoggerDelegate.debugLogger,
								"Finished POST operation in external system but unable to save user role",
								getResponse.getBody(), addUserRole.getRoleName());
						throw new Exception(addResponse.getBody());
					}
				}
			}
		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.errorLogger,
					"updateUserRolesInExternalSystem: Failed to add user role for application {} due to {}",
					app.getId(), e);
		}

	}

	/**
	 * 
	 * @param userId
	 * @param app
	 * @param applicationsRestClientService
	 * @param searchService
	 * @param mapper
	 * @throws Exception
	 */
	protected void createNewUserOnRemoteApp(String userId, EPApp app,
			ApplicationsRestClientService applicationsRestClientService, SearchService searchService,
			ObjectMapper mapper, boolean postOpenSource) throws Exception {

		EPUser client = searchService.searchUserByUserId(userId);

		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		if (client == null) {
			String msg = "cannot create user " + userId + ", because he/she cannot be found in phonebook.";
			logger.error(EELFLoggerDelegate.errorLogger, msg);
			throw new Exception(msg);
		}

		client.setLoginId(userId);
		client.setActive(true);

		String userInString = null;
		userInString = mapper.writerFor(EPUser.class).writeValueAsString(client);
		logger.debug(EELFLoggerDelegate.debugLogger,
				"about to post new client to remote application, users json = " + userInString);
		applicationsRestClientService.post(EPUser.class, app.getId(), userInString, String.format("/user", userId));

	}

	@SuppressWarnings("unchecked")
	protected void applyChangesToAppRolesRequest(Long appId, Long userId, String updateStatus,
			EPUserAppRolesRequest epUserAppRolesRequest) {
		final Map<String, Long> epRequestParams = new HashMap<>();
		try {
			EPUserAppRolesRequest epAppRolesRequestData = epUserAppRolesRequest;
			epAppRolesRequestData.setUpdatedDate(new Date());
			epAppRolesRequestData.setRequestStatus(updateStatus);
			HashMap<String, Long> addiotonalUpdateParam = new HashMap<String, Long>();
			addiotonalUpdateParam.put("userId", userId);
			dataAccessService.saveDomainObject(epAppRolesRequestData, addiotonalUpdateParam);
			epRequestParams.put("reqId", epUserAppRolesRequest.getId());
			List<EPUserAppRolesRequestDetail> epUserAppRolessDetailList = new ArrayList<EPUserAppRolesRequestDetail>();
			epUserAppRolessDetailList = dataAccessService.executeNamedQuery("userAppRolesRequestDetailList",
					epRequestParams, null);
			if (epUserAppRolessDetailList.size() > 0) {
				for (EPUserAppRolesRequestDetail epRequestUpdateData : epUserAppRolessDetailList) {
					EPUserAppRolesRequestDetail epAppRoleDetailData = epRequestUpdateData;
					epAppRoleDetailData.setReqType(updateStatus);
					epAppRoleDetailData.setEpRequestIdData(epAppRolesRequestData);
					HashMap<String, Long> updateDetailsParam = new HashMap<String, Long>();
					addiotonalUpdateParam.put("reqId", epUserAppRolesRequest.getId());
					dataAccessService.saveDomainObject(epAppRoleDetailData, updateDetailsParam);
				}
			}
			logger.debug(EELFLoggerDelegate.debugLogger, "The request is set to complete");

		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.errorLogger, "applyChangesToAppRolesRequest failed", e);
		}
	}

	@SuppressWarnings("unchecked")
	public void applyChangesToUserAppRolesForMyLoginsRequest(EPUser user, Long appId) {
		final Map<String, Long> params = new HashMap<>();
		final Map<String, Long> epDetailParams = new HashMap<>();
		List<EPUserAppRolesRequest> epRequestIdVal = new ArrayList<EPUserAppRolesRequest>();
		params.put("appId", appId);
		params.put("userId", user.getId());
		try {
			epRequestIdVal = (List<EPUserAppRolesRequest>) dataAccessService
					.executeNamedQuery("userAppRolesRequestList", params, null);
			if (epRequestIdVal.size() > 0) {
				EPUserAppRolesRequest epAppRolesRequestData = epRequestIdVal.get(0);
				epAppRolesRequestData.setUpdatedDate(new Date());
				epAppRolesRequestData.setRequestStatus("O");
				HashMap<String, Long> addiotonalUpdateParam = new HashMap<String, Long>();
				addiotonalUpdateParam.put("userId", user.getId());
				dataAccessService.saveDomainObject(epAppRolesRequestData, addiotonalUpdateParam);
				epDetailParams.put("reqId", epAppRolesRequestData.getId());
				List<EPUserAppRolesRequestDetail> epUserAppRolesDetailList = new ArrayList<EPUserAppRolesRequestDetail>();
				epUserAppRolesDetailList = dataAccessService.executeNamedQuery("userAppRolesRequestDetailList",
						epDetailParams, null);
				if (epUserAppRolesDetailList.size() > 0) {
					for (EPUserAppRolesRequestDetail epRequestUpdateList : epUserAppRolesDetailList) {
						EPUserAppRolesRequestDetail epAppRoleDetailData = epRequestUpdateList;
						epAppRoleDetailData.setReqType("O");
						epAppRoleDetailData.setEpRequestIdData(epAppRolesRequestData);
						HashMap<String, Long> updateDetailsParams = new HashMap<String, Long>();
						addiotonalUpdateParam.put("reqId", epAppRolesRequestData.getId());
						dataAccessService.saveDomainObject(epAppRoleDetailData, updateDetailsParams);
					}
					logger.debug(EELFLoggerDelegate.debugLogger, "User App roles request from User Page is overridden");
				}
			}

		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.errorLogger, "applyChangesToUserAppRolesRequest failed", e);
		}
	}

	/**
	 * Pushes specified user details to the specified remote app.
	 * 
	 * @param userId
	 *            OrgUserId identifying user at remote app in REST endpoint path
	 * @param user
	 *            User details to be pushed
	 * @param app
	 *            Remote app
	 * @param applicationsRestClientService
	 * @throws HTTPException
	 */
	protected void postUserToRemoteApp(String userId, EPUser user, EPApp app,
			ApplicationsRestClientService applicationsRestClientService) throws HTTPException {

		getUser(userId, app, applicationsRestClientService);

	}

	/**
	 * It returns user details for single org user id
	 * 
	 * @param userParams
	 * @return if user exists it returns list of user details otherwise empty value
	 */
	@SuppressWarnings("unchecked")
	private List<EPUser> checkIfUserExists(Map<String, String> userParams) {
		return (List<EPUser>) dataAccessService.executeNamedQuery("epUserAppId", userParams, null);
	}

	/**
	 * It checks whether the new user is valid or not otherwise throws exception
	 * 
	 * @param orgUserId
	 * @param app
	 * @return Checks if user is valid and returns message otherwise throws
	 *         exception
	 * @throws Exception
	 */
	private String validateNewUser(String orgUserId, EPApp app) throws Exception {
		EPUser epUser = searchService.searchUserByUserId(orgUserId);
		if (epUser == null) {
			throw new Exception("User does not exist");
		} else if (!epUser.getOrgUserId().equals(orgUserId)) {
			throw new Exception("User does not exist");
		} else if (app == null) {
			throw new Exception("Application does not exist");
		}
		return "Saved Successfully";
	}

	/**
	 * Checks if the fields exists or not
	 * 
	 * @param userList
	 *            contains user information
	 * @param app
	 *            contains app name
	 * @throws Exception
	 *             throws exception if the field is not valid
	 */
	private void validateExternalRequestFields(List<EPUser> userList, EPApp app) throws Exception {
		if (userList.size() == 0 || userList.isEmpty()) {
			throw new Exception("User does not exist");
		} else if (app == null) {
			throw new Exception("Application does not exist");
		} else if (!app.getEnabled() && !app.getId().equals(PortalConstants.PORTAL_APP_ID)) {
			throw new Exception(app.getMlAppName() + " application is unavailable");
		}
	}

	@SuppressWarnings("unchecked")
	public ExternalRequestFieldsValidator setExternalRequestUserAppRole(ExternalSystemUser newAppRolesForUser,
			String reqType) {
		boolean result = false;
		boolean externalSystemRequest = true;
		final Map<String, Long> params = new HashMap<>();
		final Map<String, String> userParams = new HashMap<>();
		List<EPUser> userInfo = null;
		EPUser userId = null;
		List<EPUserAppRolesRequest> epRequestId = null;
		String orgUserId = "";
		String updateStatus = "";
		String reqMessage = "";
		EPApp app = null;
		if (newAppRolesForUser != null && newAppRolesForUser.getLoginId() != null) {
			orgUserId = newAppRolesForUser.getLoginId().trim();
		}
		String appName = newAppRolesForUser.getApplicationName();
		String logMessage = ("DELETE").equals(reqType) ? "Deleting" : "Assigning/Updating";
		if (orgUserId.length() > 0) {
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			int epRequestIdSize = 0;
			try {
				app = appsService.getAppDetail(appName);
				userParams.put("orgUserIdValue", orgUserId);
				userInfo = checkIfUserExists(userParams);
				reqMessage = "Updated Successfully";
				if (!reqType.equals("DELETE") && (userInfo.size() == 0 || userInfo.isEmpty())) {
					reqMessage = validateNewUser(orgUserId, app);
				}
				if (userInfo.size() != 0 || !userInfo.isEmpty()) {
					validateExternalRequestFields(userInfo, app);
					userId = userInfo.get(0);
					params.put("appId", app.getId());
					params.put("userId", userId.getId());
					epRequestId = (List<EPUserAppRolesRequest>) dataAccessService
							.executeNamedQuery("userAppRolesRequestList", params, null);
					epRequestIdSize = epRequestId.size();
				}

				// If Non-Centralized app make sure you sync app roles before assigning to user
				if (!app.getId().equals(PortalConstants.PORTAL_APP_ID) && !app.getCentralAuth()) {
					EcompRole[] appRoles = applicationsRestClientService.get(EcompRole[].class, app.getId(), "/roles");
					syncAppRoles(sessionFactory, app.getId(), appRoles);
				}
				List<RoleInAppForUser> roleInAppForUserList = roleInAppForUserList(newAppRolesForUser.getRoles(),
						app.getId(), app.getMlAppName());
				List<EcompUserAppRoles> userRoleList = null;
				if (!userInfo.isEmpty()) {
					final Map<String, Long> appParams = new HashMap<>();
					appParams.put("userId", userId.getId());
					appParams.put("appId", app.getId());
					userRoleList = dataAccessService.executeNamedQuery("getUserAppExistingRoles", appParams, null);
				}
				// Check if list contains just account admin role
				boolean checkIfAdminRoleExists = false;
				if (reqType.equals("DELETE") && userRoleList != null) {
					checkIfAdminRoleExists = userRoleList.stream()
							.anyMatch(userRole -> userRole.getRoleId().equals(PortalConstants.ACCOUNT_ADMIN_ROLE_ID));
				} else {
					checkIfAdminRoleExists = roleInAppForUserList.stream()
							.anyMatch(roleList -> roleList.getRoleId().equals(PortalConstants.ACCOUNT_ADMIN_ROLE_ID));
				}
				// if Centralized app
				if (app.getCentralAuth()) {
					// We should add If user does not exist in remote application
					try {
						// If adding just account admin role dont make remote application user call
						if (!app.getId().equals(PortalConstants.PORTAL_APP_ID)
								&& !(checkIfAdminRoleExists && reqType.equals("DELETE"))
								&& roleInAppForUserList.size() > 1) {
							EPUser remoteAppUser = null;
							remoteAppUser = checkIfRemoteUserExits(orgUserId, app, applicationsRestClientService);
							if (remoteAppUser == null) {
								addRemoteUser(roleInAppForUserList, orgUserId, app, mapper, searchService,
										applicationsRestClientService);
								reqMessage = "Saved Successfully";
							}
						}
					} catch (Exception e) {
						reqMessage = e.getMessage();
						logger.error(EELFLoggerDelegate.errorLogger,
								"setExternalRequestUserAppRole: Failed to added remote user", e);
						throw new Exception(reqMessage);
					}
					Set<EcompRole> userRolesInLocalApp = postUsersRolesToLocalApp(roleInAppForUserList, mapper,
							applicationsRestClientService, app.getId(), orgUserId);
					RolesInAppForUser rolesInAppForUser = constructRolesInAppForUserUpdate(orgUserId, app.getId(),
							userRolesInLocalApp);
					List<RoleInAppForUser> roleAppUserList = rolesInAppForUser.roles;
					// Apply changes in external Access system
					updateUserRolesInExternalSystem(app, rolesInAppForUser.orgUserId, roleAppUserList,
							externalSystemRequest);
					logger.info(EELFLoggerDelegate.debugLogger,
							"setExternalRequestUserAppRole: {} user app roles: for app {}, user {}", logMessage,
							newAppRolesForUser.getApplicationName(), newAppRolesForUser.getLoginId());
					result = applyChangesInUserRolesForAppToEcompDB(rolesInAppForUser, externalSystemRequest, reqType);
				}
				// If local application is not centralized
				else if (!app.getCentralAuth() && app.getId().equals(PortalConstants.PORTAL_APP_ID)) {
					Set<EcompRole> userRolesInLocalApp = postUsersRolesToLocalApp(roleInAppForUserList, mapper,
							applicationsRestClientService, app.getId(), orgUserId);
					RolesInAppForUser rolesInAppForUser = constructRolesInAppForUserUpdate(orgUserId, app.getId(),
							userRolesInLocalApp);
					result = applyChangesInUserRolesForAppToEcompDB(rolesInAppForUser, externalSystemRequest, reqType);
				} else {// remote app
					// If adding just account admin role don't do remote application user call
					if (!((roleInAppForUserList.size() == 1 || reqType.equals("DELETE")) && checkIfAdminRoleExists)) {
						EPUser remoteAppUser = null;
						remoteAppUser = checkIfRemoteUserExits(orgUserId, app, applicationsRestClientService);
						if (remoteAppUser == null) {
							remoteAppUser = addRemoteUser(roleInAppForUserList, orgUserId, app, mapper, searchService,
									applicationsRestClientService);
							reqMessage = "Saved Successfully";
						}
						if (remoteAppUser != null) {
							Set<EcompRole> userRolesInRemoteApp = postUsersRolesToRemoteApp(roleInAppForUserList,
									mapper, applicationsRestClientService, app.getId(), orgUserId);

							RolesInAppForUser rolesInAppForUser = constructRolesInAppForUserUpdate(orgUserId,
									app.getId(), userRolesInRemoteApp);
							logger.info(EELFLoggerDelegate.debugLogger,
									"setExternalRequestUserAppRole: {} user app roles: for app {}, user {}", logMessage,
									newAppRolesForUser.getApplicationName(), newAppRolesForUser.getLoginId());
							result = applyChangesInUserRolesForAppToEcompDB(rolesInAppForUser, externalSystemRequest,
									reqType);
							// If no roles remain, request app to set user inactive.
							/*
							 * if (userRolesInRemoteApp.size() == 0) {
							 * logger.debug(EELFLoggerDelegate.debugLogger,
							 * "setAppWithUserRoleStateForUser: no roles in app {}, set user {} to inactive"
							 * , app, orgUserId); //TODO Need to fix the logged in user is not set to
							 * inactive remoteAppUser.setActive(false); postUserToRemoteApp(orgUserId, user,
							 * app, applicationsRestClientService); }
							 */
						}
					} else {
						// Here we are adding only we have single account admin in roleInAppForUserList
						// and this should not add in remote
						if (!(reqType.equals("DELETE")) && userInfo.isEmpty()) {
							reqMessage = "Saved Successfully";
						}
						Set<EcompRole> userRolesInRemoteApp = constructUsersEcompRoles(roleInAppForUserList);

						RolesInAppForUser rolesInAppForUser = constructRolesInAppForUserUpdate(orgUserId, app.getId(),
								userRolesInRemoteApp);
						logger.info(EELFLoggerDelegate.debugLogger,
								"setExternalRequestUserAppRole: {} user app roles: for app {}, user {}", logMessage,
								newAppRolesForUser.getApplicationName(), newAppRolesForUser.getLoginId());
						result = applyChangesInUserRolesForAppToEcompDB(rolesInAppForUser, externalSystemRequest,
								reqType);
					}
					if (!result) {
						reqMessage = "Failed to save the user app role(s)";
					}
					if (epRequestIdSize > 0 && !userInfo.isEmpty()) {
						updateStatus = "C";
						applyChangesToAppRolesRequest(app.getId(), userId.getId(), updateStatus, epRequestId.get(0));
					}
				}
			} catch (Exception e) {
				String message = String.format(
						"setExternalRequestUserAppRole: Failed to create user or update user roles for User %s, AppId %s",
						orgUserId, appName);
				logger.error(EELFLoggerDelegate.errorLogger, message, e);
				result = false;
				reqMessage = e.getMessage();
				if (epRequestIdSize > 0 && userInfo != null && !userInfo.isEmpty()) {
					updateStatus = "F";
					applyChangesToAppRolesRequest(app.getId(), userId.getId(), updateStatus, epRequestId.get(0));
				}
			}

		}
		return new ExternalRequestFieldsValidator(result, reqMessage);
	}

	/**
	 * 
	 * @param roleInAppForUserList
	 * @param mapper
	 * @param applicationsRestClientService
	 * @param appId
	 * @param userId
	 * @return Set<EcompRole>
	 * @throws JsonProcessingException
	 * @throws HTTPException
	 */
	private Set<EcompRole> postUsersRolesToLocalApp(List<RoleInAppForUser> roleInAppForUserList, ObjectMapper mapper,
			ApplicationsRestClientService applicationsRestClientService, Long appId, String userId)
			throws JsonProcessingException, HTTPException {
		Set<EcompRole> updatedUserRoles = constructUsersEcompRoles(roleInAppForUserList);
		return updatedUserRoles;
	}

	/**
	 * It constructs and returns list of user app roles when the external API role
	 * approval system calls this method
	 * 
	 * @param roleInAppForUserList
	 * @param appId
	 * @return list of user app roles
	 * @throws Exception
	 *             throws exceptions if role id does not exits
	 */
	private List<RoleInAppForUser> roleInAppForUserList(List<ExternalSystemRoleApproval> roleInAppForUserList,
			Long appId, String appName) throws Exception {
		List<RoleInAppForUser> existingUserRoles = new ArrayList<RoleInAppForUser>();
		EPRole existingAppRole = null;
		for (ExternalSystemRoleApproval roleInAppForUser : roleInAppForUserList) {
			RoleInAppForUser ecompRole = new RoleInAppForUser();
			existingAppRole = epRoleService.getAppRole(roleInAppForUser.getRoleName(), appId);
			if (existingAppRole == null) {
				logger.error(EELFLoggerDelegate.errorLogger, "roleInAppForUserList failed for the roles {}",
						roleInAppForUserList);
				throw new Exception("'" + roleInAppForUser.getRoleName() + "'" + " role does not exist for " + appName
						+ " application");
			}
			if (!existingAppRole.getActive()) {
				logger.error(EELFLoggerDelegate.errorLogger, "roleInAppForUserList failed for the roles {}",
						roleInAppForUserList);
				throw new Exception(
						roleInAppForUser.getRoleName() + " role is unavailable for " + appName + " application");
			} else {
				ecompRole.roleId = (appId == 1 || roleInAppForUser.getRoleName().equals(PortalConstants.ADMIN_ROLE))
						? existingAppRole.getId()
						: existingAppRole.getAppRoleId();
				ecompRole.roleName = roleInAppForUser.getRoleName();
				ecompRole.isApplied = true;
				existingUserRoles.add(ecompRole);
			}
		}
		return existingUserRoles;
	}

	/**
	 * 
	 * @param userId
	 * @param app
	 * @param applicationsRestClientService
	 * @return EPUser
	 * @throws HTTPException
	 */
	protected EPUser getUserFromApp(String userId, EPApp app,
			ApplicationsRestClientService applicationsRestClientService) throws HTTPException {
		// local app
		if (app.getId() == PortalConstants.PORTAL_APP_ID) {
			// Map<String,String> params = new HashMap<String,String>();
			// params.put("sbcid",userId);
			@SuppressWarnings("unchecked")
			List<EPUser> userList = (List<EPUser>) dataAccessService
					.executeQuery("from EPUser where orgUserId='" + userId + "'", null);
			if (userList != null && !userList.isEmpty())
				return userList.get(0);
			else
				return null;
		}
		// remote app

		return getUser(userId, app, applicationsRestClientService);
	}

	protected EPUser getUser(String userId, EPApp app, ApplicationsRestClientService applicationsRestClientService)
			throws HTTPException {
		return applicationsRestClientService.get(EPUser.class, app.getId(), String.format("/user/%s", userId), true);

	}

	protected boolean isAppUpgradeVersion(EPApp app) {
		return true;
	}

	public ExternalSystemAccess getExternalRequestAccess() {
		ExternalSystemAccess res = null;
		try {
			res = new ExternalSystemAccess(EPCommonSystemProperties.EXTERNAL_ACCESS_ENABLE, Boolean
					.parseBoolean(SystemProperties.getProperty(EPCommonSystemProperties.EXTERNAL_ACCESS_ENABLE)));
		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.errorLogger, "getExternalRequestAccess failed" + e.getMessage());
		}
		return res;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openecomp.portalapp.portal.service.UserRolesService#
	 * getAppRolesForUser(java.lang.Long, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public List<RoleInAppForUser> getAppRolesForUser(Long appId, String userId, Boolean extRequestValue) {

		List<RoleInAppForUser> rolesInAppForUser = null;
		EPApp app = appsService.getApp(appId);
		try {
			// for ecomp portal app, no need to make a remote call
			List<Role> roleList = new ArrayList<>();
			if (appId == PortalConstants.PORTAL_APP_ID) {
				if (app.getCentralAuth()) {
					List<CentralRole> cenRoleList = externalAccessRolesService.getRolesForApp(app.getUebKey());
					for (CentralRole cenRole : cenRoleList) {
						Role role = new Role();
						role.setActive(cenRole.isActive());
						role.setId(cenRole.getId());
						role.setName(cenRole.getName());
						role.setPriority(cenRole.getPriority());
						roleList.add(role);
					}
				} else {
					roleList = roleService.getAvailableRoles(userId);
				}
				List<Role> activeRoleList = new ArrayList<Role>();
				for (Role role : roleList) {
					if (role.getActive()) {
						if (role.getId() != 1) { // prevent portal admin from being added
							activeRoleList.add(role);
						} else if (extRequestValue) {
							activeRoleList.add(role);
						}
					}

				}
				EPUser localUser = getUserFromApp(userId, app, applicationsRestClientService);
				// If localUser does not exists return roles
				Set<EPRole> roleSet = null;
				EPRole[] roleSetList = null;
				if (localUser != null) {
					roleSet = localUser.getAppEPRoles(app);
					roleSetList = roleSet.toArray(new EPRole[0]);
				}
				rolesInAppForUser = constructRolesInAppForUserGet(activeRoleList, roleSetList, extRequestValue);
				return rolesInAppForUser;
			}

			EcompRole[] appRoles = null;
			List<EcompRole> roles = new ArrayList<>();
			if (app.getCentralAuth()) {
				// Sync application functions from External Access System
				externalAccessRolesService.syncRoleFunctionFromExternalAccessSystem(app);
				List<EPRole> applicationRoles = dataAccessService.getList(EPRole.class,
						" where app_id = " + app.getId() + " and active_yn = 'Y'", null, null);
				;
				for (EPRole role : applicationRoles) {
					EcompRole ecompRole = new EcompRole();
					ecompRole.setId(role.getId());
					ecompRole.setName(role.getName());
					roles.add(ecompRole);
				}
				appRoles = roles.toArray(new EcompRole[roles.size()]);
			} else {
				appRoles = applicationsRestClientService.get(EcompRole[].class, appId, "/roles");
			}
			// Test this error case, for generating an internal Ecomp Portal
			// error
			// EcompRole[] appRoles = null;
			// If there is an exception in the rest client api, then null will
			// be returned.
			if (appRoles != null) {
				if (!app.getCentralAuth()) {
					syncAppRoles(sessionFactory, appId, appRoles);
				}
				EcompRole[] userAppRoles = null;
				try {
					try {
						if (app.getCentralAuth()) {
							final Map<String, String> params = new HashMap<>();
							final Map<String, Long> userParams = new HashMap<>();
							params.put("orgUserIdValue", userId);
							List<EPUser> user = dataAccessService.executeNamedQuery("epUserAppId", params, null);
							userParams.put("appId", app.getId());
							userParams.put("userId", user.get(0).getId());
							List<EPUserAppCurrentRoles> userAppsRolesList = dataAccessService
									.executeNamedQuery("getUserAppCurrentRoles", userParams, null);
							List<EcompRole> setUserRoles = new ArrayList<>();
							for (EPUserAppCurrentRoles role : userAppsRolesList) {
								EcompRole ecompRole = new EcompRole();
								ecompRole.setId(role.getRoleId());
								ecompRole.setName(role.getRoleName());
								setUserRoles.add(ecompRole);
							}
							userAppRoles = setUserRoles.toArray(new EcompRole[setUserRoles.size()]);
							rolesInAppForUser = constructRolesInAppForUserGet(appRoles, userAppRoles);
							return rolesInAppForUser;
						} else {
							userAppRoles = applicationsRestClientService.get(EcompRole[].class, appId,
									String.format("/user/%s/roles", userId));
						}
					} catch (HTTPException e) {
						// Some apps are returning 400 if user is not found.
						if (e.getResponseCode() == 400) {
							logger.debug(EELFLoggerDelegate.debugLogger,
									"getAppRolesForUser caught exception with response code 400; continuing", e);
						} else {
							// Other response code, let it come thru.
							throw e;
						}
					}
					if (userAppRoles == null) {
						if (EcompPortalUtils.getExternalAppResponseCode() == 400) {
							EcompPortalUtils.setExternalAppResponseCode(200);
							String message = String.format(
									"getAppRolesForUser: App %s, User %, endpoint /user/{userid}/roles returned 400, "
											+ "assuming user doesn't exist, app is framework SDK based, and things are ok. "
											+ "Overriding to 200 until framework SDK returns a useful response.",
									Long.toString(appId), userId);
							logger.warn(EELFLoggerDelegate.applicationLogger, message);
						}
					}

					HashMap<Long, EcompRole> appRolesActiveMap = hashMapFromEcompRoles(appRoles);
					ArrayList<EcompRole> activeRoles = new ArrayList<EcompRole>();
					if (userAppRoles != null) {
						for (int i = 0; i < userAppRoles.length; i++) {
							if (appRolesActiveMap.containsKey(userAppRoles[i].getId())) {
								EcompRole role = new EcompRole();
								role.setId(userAppRoles[i].getId());
								role.setName(userAppRoles[i].getName());
								activeRoles.add(role);
							}
						}
					}
					EcompRole[] userAppRolesActive = activeRoles.toArray(new EcompRole[activeRoles.size()]);

					// If the remote application isn't down we MUST sync user
					// roles here in case we have this user here!
					syncUserRoles(sessionFactory, userId, appId, userAppRolesActive, extRequestValue, null);
				} catch (Exception e) {
					// TODO: we may need to check if user exists, maybe remote
					// app is down.
					String message = String.format(
							"getAppRolesForUser: user %s does not exist in remote application %s", userId,
							Long.toString(appId));
					logger.error(EELFLoggerDelegate.errorLogger, message, e);
					userAppRoles = new EcompRole[0];
				}
				rolesInAppForUser = constructRolesInAppForUserGet(appRoles, userAppRoles);
			}
		} catch (Exception e) {
			String message = String.format("getAppRolesForUser: failed for User %s, AppId %s", userId,
					Long.toString(appId));
			logger.error(EELFLoggerDelegate.errorLogger, message, e);
		}
		return rolesInAppForUser;

	}

	private boolean postUserRolesToMylogins(AppWithRolesForUser userAppRolesData,
			ApplicationsRestClientService applicationsRestClientService, Long appId, Long userId)
			throws JsonProcessingException, HTTPException {
		boolean result = false;
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		String userRolesAsString = mapper.writeValueAsString(userAppRolesData);
		logger.error(EELFLoggerDelegate.errorLogger,
				"Should not be reached here, as the endpoint is not defined yet from the Mylogins");
		applicationsRestClientService.post(AppWithRolesForUser.class, appId, userRolesAsString,
				String.format("/user/%s/myLoginroles", userId));
		return result;
	}

	public FieldsValidator putUserAppRolesRequest(AppWithRolesForUser userAppRolesData, EPUser user) {
		FieldsValidator fieldsValidator = new FieldsValidator();
		final Map<String, Long> params = new HashMap<>();
		EPUserAppRoles appRole = new EPUserAppRoles();
		try {
			logger.error(EELFLoggerDelegate.errorLogger,
					"Should not be reached here, still the endpoint is yet to be defined");
			boolean result = postUserRolesToMylogins(userAppRolesData, applicationsRestClientService,
					userAppRolesData.appId, user.getId());
			logger.debug(EELFLoggerDelegate.debugLogger, "putUserAppRolesRequest: result {}", result);

			params.put("appId", userAppRolesData.appId);
			EPUserAppRolesRequest epAppRolesRequestData = new EPUserAppRolesRequest();
			epAppRolesRequestData.setCreatedDate(new Date());
			epAppRolesRequestData.setUpdatedDate(new Date());
			epAppRolesRequestData.setUserId(user.getId());
			epAppRolesRequestData.setAppId(userAppRolesData.appId);
			epAppRolesRequestData.setRequestStatus("P");
			List<RoleInAppForUser> appRoleIdList = userAppRolesData.appRoles;
			Set<EPUserAppRolesRequestDetail> appRoleDetails = new LinkedHashSet<EPUserAppRolesRequestDetail>();
			dataAccessService.saveDomainObject(epAppRolesRequestData, null);
			for (RoleInAppForUser userAppRoles : appRoleIdList) {
				Boolean isAppliedVal = userAppRoles.isApplied;
				params.put("appRoleId", userAppRoles.roleId);
				if (isAppliedVal) {
					appRole = (EPUserAppRoles) dataAccessService.executeNamedQuery("appRoles", params, null).get(0);
					EPUserAppRolesRequestDetail epAppRoleDetail = new EPUserAppRolesRequestDetail();
					epAppRoleDetail.setReqRoleId(appRole.getRoleId());
					epAppRoleDetail.setReqType("P");
					epAppRoleDetail.setEpRequestIdData(epAppRolesRequestData);
					dataAccessService.saveDomainObject(epAppRoleDetail, null);
				}
			}
			epAppRolesRequestData.setEpRequestIdDetail(appRoleDetails);
			fieldsValidator.httpStatusCode = new Long(HttpServletResponse.SC_OK);

		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.errorLogger, "putUserAppRolesRequest failed", e);
			fieldsValidator.httpStatusCode = new Long(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
		return fieldsValidator;
	}

	public List<EPUserAppCatalogRoles> getUserAppCatalogRoles(EPUser userid, String appName) {
		Map<String, String> params = new HashMap<>();
		params.put("userid", userid.getId().toString());
		// params.put("appid", appid);
		params.put("appName", appName);

		@SuppressWarnings("unchecked")
		List<EPUserAppCatalogRoles> userAppRoles = (List<EPUserAppCatalogRoles>) dataAccessService
				.executeNamedQuery("userAppCatalogRoles", params, null);
		return userAppRoles;
	}

	public String updateRemoteUserProfile(String orgUserId, Long appId) {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		EPUser client = searchService.searchUserByUserId(orgUserId);
		EPUser newUser = new EPUser();
		newUser.setActive(client.getActive());
		newUser.setFirstName(client.getFirstName());
		newUser.setLastName(client.getLastName());
		newUser.setLoginId(client.getLoginId());
		newUser.setLoginPwd(client.getLoginPwd());
		newUser.setMiddleInitial(client.getMiddleInitial());
		newUser.setEmail(client.getEmail());
		newUser.setOrgUserId(client.getLoginId());
		try {
			String userAsString = mapper.writeValueAsString(newUser);
			List<EPApp> appList = appsService.getUserRemoteApps(client.getId().toString());
			// applicationsRestClientService.post(EPUser.class, appId,
			// userAsString, String.format("/user", orgUserId));
			for (EPApp eachApp : appList) {
				try {
					applicationsRestClientService.post(EPUser.class, eachApp.getId(), userAsString,
							String.format("/user/%s", orgUserId));
				} catch (Exception e) {
					logger.error(EELFLoggerDelegate.errorLogger, "Failed to update user: " + client.getOrgUserId()
							+ " in remote app. appId = " + eachApp.getId());
				}
			}
		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.errorLogger, "updateRemoteUserProfile failed", e);
			return "failure";
		}

		return "success";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openecomp.portalapp.portal.service.UserRolesService#
	 * getCachedAppRolesForUser(java.lang.Long, java.lang.Long)
	 */
	public List<EPUserApp> getCachedAppRolesForUser(Long appId, Long userId) {
		// Find the records for this user-app combo, if any
		String filter = " where user_id = " + Long.toString(userId) + " and app_id = " + Long.toString(appId);
		@SuppressWarnings("unchecked")
		List<EPUserApp> roleList = dataAccessService.getList(EPUserApp.class, filter, null, null);
		logger.debug(EELFLoggerDelegate.debugLogger, "getCachedAppRolesForUser: list size is {}", roleList.size());
		return roleList;
	}

	/**
	 * Retrieves and returns a list of user app roles for local and remote
	 * applications based on the app id.
	 * 
	 * @param appId
	 * @return list of user app roles
	 * @throws HTTPException
	 */
	public List<UserApplicationRoles> getUsersFromAppEndpoint(Long appId) throws HTTPException {
		ArrayList<UserApplicationRoles> userApplicationRoles = new ArrayList<UserApplicationRoles>();

		EPApp app = appsService.getApp(appId);
		// If local or centralized application
		if (appId == PortalConstants.PORTAL_APP_ID || app.getCentralAuth()) {
			@SuppressWarnings("unchecked")
			List<EPUser> userList = (List<EPUser>) dataAccessService.executeNamedQuery("getActiveUsers", null, null);
			for (EPUser user : userList) {
				UserApplicationRoles userWithAppRoles = convertToUserApplicationRoles(appId, user, app);
				if (userWithAppRoles.getRoles() != null && userWithAppRoles.getRoles().size() > 0)
					userApplicationRoles.add(userWithAppRoles);
			}
		}
		// remote app
		else {
			RemoteUserWithRoles[] remoteUsers = null;
			String remoteUsersString = applicationsRestClientService.getIncomingJsonString(appId, "/users");
			remoteUsers = doGetUsers(isAppUpgradeVersion(app), remoteUsersString);
			userApplicationRoles = new ArrayList<UserApplicationRoles>();
			for (RemoteUserWithRoles remoteUser : remoteUsers) {
				UserApplicationRoles userWithRemoteAppRoles = convertToUserApplicationRoles(appId, remoteUser);
				if (userWithRemoteAppRoles.getRoles() != null && userWithRemoteAppRoles.getRoles().size() > 0) {
					userApplicationRoles.add(userWithRemoteAppRoles);
				} else {
					logger.debug(EELFLoggerDelegate.debugLogger, "User " + userWithRemoteAppRoles.getOrgUserId()
							+ " doesn't have any roles assigned to any app.");
				}
			}
		}
		return userApplicationRoles;
	}

	/**
	 * 
	 * @param appId
	 * @param user
	 * @param appgetUsersFromAppEndpoint
	 * @return
	 */
	private UserApplicationRoles convertToUserApplicationRoles(Long appId, EPUser user, EPApp app) {
		UserApplicationRoles userWithRemoteAppRoles = new UserApplicationRoles();
		userWithRemoteAppRoles.setAppId(appId);
		userWithRemoteAppRoles.setOrgUserId(user.getOrgUserId());
		userWithRemoteAppRoles.setFirstName(user.getFirstName());
		userWithRemoteAppRoles.setLastName(user.getLastName());
		userWithRemoteAppRoles.setRoles(convertToRemoteRoleList(user, app));
		return userWithRemoteAppRoles;
	}

	/**
	 * 
	 * @param user
	 * @param app
	 * @return
	 */
	private List<RemoteRole> convertToRemoteRoleList(EPUser user, EPApp app) {
		List<RemoteRole> roleList = new ArrayList<RemoteRole>();
		SortedSet<EPRole> roleSet = user.getAppEPRoles(app);
		for (EPRole role : roleSet) {
			RemoteRole rRole = new RemoteRole();
			rRole.setId(role.getId());
			rRole.setName(role.getName());
			roleList.add(rRole);
		}
		return roleList;
	}

	/**
	 *  
	 * Returns RemoteUserWithRoles reading the value from remote application user in the form a string from an ObjectMapper
	 * @param postOpenSource
	 * @param remoteUsersString
	 * @return RemoteUserWithRoles
	 */
	public RemoteUserWithRoles[] doGetUsers(boolean postOpenSource, String remoteUsersString) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.readValue(remoteUsersString, RemoteUserWithRoles[].class);
		} catch (IOException e) {
			logger.error(EELFLoggerDelegate.errorLogger, "doGetUsers : Failed : Unexpected property in incoming JSON",
					e);
			logger.error(EELFLoggerDelegate.errorLogger,
					"doGetUsers : Incoming JSON that caused it --> " + remoteUsersString);
		}

		return new RemoteUserWithRoles[0];
	}
}
