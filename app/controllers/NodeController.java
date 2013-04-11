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

import models.Node;
import play.mvc.With;

/**
 * Controls the registry selection
 * 
 * @author chamerling
 *
 */
@With(Secure.class)
public class NodeController extends PlayController {
	
	public static void connect() {
		List<Node> nodes = Node.all().fetch();
		render(nodes);
	}

	public static void nodeConnect(Long id) {
		Node n = Node.findById(id);
		if (n != null) {
			session.put("node", n.id);
			flash.success("Connected to node %s", n.name);
		} else {
			flash.error("No such node");
		}
		Application.index();
	}

	public static void nodeDisconnect(Long id) {
		session.remove("node");
		flash.success("Disconnected...");
		Application.index();
	}
	
	public static void disconnectAll() {
		session.remove("node");
		flash.success("Disconnected!");
		Application.index();
	}
}
