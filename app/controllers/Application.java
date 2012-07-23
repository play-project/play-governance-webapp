package controllers;

import java.util.List;

import models.Node;

import org.petalslink.dsb.cxf.CXFHelper;
import org.petalslink.dsb.jaxws.JAXWSHelper;

import play.mvc.Before;
import play.mvc.Controller;
import utils.Locator;
import eu.playproject.governance.api.EventGovernance;
import eu.playproject.governance.api.bean.Metadata;
import eu.playproject.governance.api.bean.Topic;

public class Application extends PlayController {

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
		connect();
	}

	public static void nodeDisconnect(Long id) {
		session.remove("node");
		flash.success("Disconnected...");
		connect();
	}

	private static String getURL() {
		Node n = Node.getCurrentNode();
		if (n == null) {
			flash.success("Please connect!");
			connect();
		}
		return n.baseURL;
	}
	
	public static void index() {
		render();
	}


}