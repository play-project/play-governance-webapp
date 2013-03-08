/**
 *
 * Copyright (c) 2013, Linagora
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA 
 *
 */
package controllers;

import java.util.List;

import org.ow2.play.governance.permission.api.Constants;
import org.ow2.play.governance.permission.api.Permission;
import org.ow2.play.governance.permission.api.PermissionService;
import org.ow2.play.metadata.api.MetaResource;

import play.data.validation.Required;
import play.mvc.With;
import utils.Locator;
import utils.StringHelper;

/**
 * @author chamerling
 * 
 */
@With(Secure.class)
public class PermissionController extends PlayController {

	/**
	 * Get all the permissions
	 */
	public static void index() {
		try {
			PermissionService client = Locator.getPermissionService(getNode());
			List<MetaResource> list = client.getPermissions();
			render(list);
		} catch (Exception e) {
			handleException(e.getMessage(), e);
		}
	}

	/**
	 * 
	 * @param name
	 */
	public static void permission(String name) {
		try {
			PermissionService client = Locator.getPermissionService(getNode());
			MetaResource permission = client.getPermission(name);
			render(permission);
		} catch (Exception e) {
			handleException(e.getMessage(), e);
		}
	}

	public static void create() {
		render();
	}

	public static void doCreate(
			@Required(message = "Name is required") String name,
			@Required(message = "Access is required") String access,
			@Required(message = "Agent is required") String agent,
			@Required(message = "Mode is required") String[] mode) {
		
		validation.required(name);
		validation.required(access);
		validation.required(agent);
		validation.required(mode);
		
		if (validation.hasErrors()) {
			params.flash();
			validation.keep();
			create();
		}

		try {
			PermissionService client = Locator.getPermissionService(getNode());
			Permission permission = new Permission();
			permission.name = name;

			if (access != null) {
				permission.accessTo.addAll(StringHelper.fromCSV(access));
			}

			if (agent != null) {
				permission.agent.addAll(StringHelper.fromCSV(agent));
			}

			if (mode != null) {
				for (String string : mode) {
					if (string.equalsIgnoreCase("read")) {
						permission.mode.add(Constants.READ);
					}
					if (string.equalsIgnoreCase("write")) {
						permission.mode.add(Constants.WRITE);
					}
					if (string.equalsIgnoreCase("subcribe")) {
						permission.mode.add(Constants.SUBSCRIBE);
					}
					if (string.equalsIgnoreCase("notify")) {
						permission.mode.add(Constants.NOTIFY);
					}
				}
			}

			String id = client.addPermission(permission);
			permission(id);
		} catch (Exception e) {
			handleException(e.getMessage(), e);
		}
	}
}
