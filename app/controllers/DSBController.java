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

import org.ow2.play.governance.api.TopicAware;
import org.ow2.play.governance.api.bean.Topic;
import org.petalslink.dsb.jbi.se.wsn.api.Subscription;
import org.petalslink.dsb.jbi.se.wsn.api.SubscriptionManagementService;

import play.mvc.With;
import utils.Locator;

/**
 * @author chamerling
 * 
 */
@With(Secure.class)
public class DSBController extends PlayController {

	/**
	 * Remove the Topic from the DSB runtime
	 * 
	 * @param name
	 * @param ns
	 * @param prefix
	 */
	public static void deleteTopic(String name, String ns, String prefix) {
		validation.required(name);
		validation.required(prefix);

		// validation url does not allow IP address...
		validation.isTrue(ns != null
				&& (ns.startsWith("http://") || ns.startsWith("https://")));

		validation.required(name);
		validation.url(ns);
		validation.required(prefix);

		if (validation.hasErrors()) {
			params.flash();
			validation.keep();
			topics();
		}

		try {
			TopicAware ta = Locator.getDSBTopicManagement(getNode());
			Topic topic = new Topic();
			topic.setName(name);
			topic.setNs(ns);
			topic.setPrefix(prefix);
			ta.delete(topic);
			flash.success("Topic has been deleted from the DSB %s",
					topic.toString());

		} catch (Exception e) {
			e.printStackTrace();
			handleException("Can not delete topic", e);
		}
		topics();
	}
	
	/**
	 * Get the topic details with all the subscriptions
	 * 
	 * @param name
	 * @param ns
	 * @param prefix
	 */
	public static void topic(String name, String ns, String prefix) {
		org.petalslink.dsb.jbi.se.wsn.api.Topic topic = new org.petalslink.dsb.jbi.se.wsn.api.Topic();
		topic.name = name;
		topic.ns = ns;
		topic.prefix = prefix;
		
		List<Subscription> subscriptions = null;
		try {
			SubscriptionManagementService client = Locator.getSubscriptionManagementService(getNode());
			subscriptions = client.getSubscriptionsForTopic(topic);
		} catch (Exception e) {
			handleException("Can not get client", e);
		}
		
		render(topic, subscriptions);
	}
	
	/**
	 * Get all the DSB topics GET
	 */
	public static void topics() {
		List<Topic> topics = null;
		try {
			TopicAware ta = Locator.getDSBTopicManagement(getNode());
			topics = ta.get();
		} catch (Exception e) {
			e.printStackTrace();
		}
		render(topics);
	}


}
