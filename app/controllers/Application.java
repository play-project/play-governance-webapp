package controllers;

import java.util.List;

import models.Node;
import play.mvc.With;

public class Application extends PlayController {

	private static String getURL() {
		Node n = Node.getCurrentNode();
		if (n == null) {
			flash.success("Please connect!");
			NodeController.connect();
		}
		return n.baseURL;
	}
	
	public static void index() {
		// redirect to topics
		//TopicsController.topics();
		render();
	}
}