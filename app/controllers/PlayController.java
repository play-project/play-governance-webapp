/**
 *
 * Copyright (c) 2012, PetalsLink
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

/**
 * @author chamerling
 * 
 */
public abstract class PlayController extends Controller {

	@Before
	public static void init() {
		Node n = Node.getCurrentNode();
		if (n != null) {
			renderArgs.put("currentNode", n);
		}
	}

	protected static Node getNode() {
		Node n = Node.getCurrentNode();
		if (n == null) {
			flash.success("Please connect!");
			Application.connect();
		}
		return n;
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
