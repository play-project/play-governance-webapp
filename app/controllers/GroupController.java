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

import org.ow2.play.governance.api.GovernanceExeption;
import org.ow2.play.governance.api.GroupService;
import org.ow2.play.governance.api.bean.Group;

import play.mvc.With;
import utils.Locator;

/**
 * @author chamerling
 *
 */
@With(Secure.class)
public class GroupController extends PlayController {
	
	/**
	 * List the groups
	 */
	public static final void list() {
		try {
			GroupService group = Locator.getGroupService(getNode());
			List<Group> list = group.get();
			render(list);
		} catch (Exception e) {
			handleException("Can not get groups", e);
		}
	}
	
	/**
	 * 
	 * @param name
	 */
	public static final void group(String name) {
		try {
			GroupService client = Locator.getGroupService(getNode());
			Group group = client.getGroupFromName(name);
			render(group);
		} catch (Exception e) {
			e.printStackTrace();
			handleException("Can not get groups", e);
		}
	}
}
