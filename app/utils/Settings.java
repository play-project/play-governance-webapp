package utils;

import play.mvc.Scope.Session;

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

/**
 * @author chamerling
 * 
 */
public class Settings {

	public static final String DEBUG = "org.ow2.play.debug";

	public static final String DEFAULT_REGISTRY = "org.ow2.play.registry";

	public static final boolean isDebug() {
		return Session.current().get(DEBUG) != null
				&& Session.current().get(DEBUG).equalsIgnoreCase("on");
	}

	public static final void switchDebug() {
		if (isDebug()) {
			Session.current().put(DEBUG, "off");
		} else {
			Session.current().put(DEBUG, "on");
		}
		// remove registry from session to be sure to get the default one
		Session.current().remove("node");
	}

	public static final String getDebugMode() {
		return (isDebug() ? "on" : "off");
	}

}
