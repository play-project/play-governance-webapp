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

import models.Node;
import play.mvc.Before;
import play.mvc.Controller;
import utils.Settings;

/**
 * @author chamerling
 * 
 */
public abstract class PlayController extends Controller {

	/**
	 * Push the current node in the context if any
	 */
	@Before
	public static void init() {
		Node n = Node.getCurrentNode();
		if (n != null) {
			renderArgs.put("currentNode", n);
		}
	}

	/**
	 * Get node is called by controllers when a governance client is needed. If
	 * the node is not found, the default one is returned.
	 * 
	 * @return
	 */
	protected static Node getNode() {
		// not in debug mode means getting default registry
		if (!Settings.isDebug()) {
			System.out.println("Default registry");
			return Node.getDefault();
		} else {
			Node n = Node.getCurrentNode();
			if (n == null) {
				flash.success("Please select a registry node!");
				NodeController.connect();
			} else {
				return n;
			}
		}
		System.out.println("Should not be here...");
		return null;
	}

	protected static void handleException(String message, Exception e) {
		String pattern = null;
		if (message != null) {
			pattern = message;
		} else {
			pattern = "ERROR";
		}
		flash.error(pattern + " : %s", e.getMessage());
		Application.index();
	}

}
