/**
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
