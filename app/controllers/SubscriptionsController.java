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

import java.util.List;

import org.ow2.play.governance.api.GovernanceExeption;
import org.ow2.play.governance.api.SubscriptionRegistry;
import org.ow2.play.governance.api.SubscriptionService;
import org.ow2.play.governance.api.bean.Subscription;
import org.ow2.play.governance.api.bean.Topic;

import utils.Locator;

/**
 * @author chamerling
 * 
 */
public class SubscriptionsController extends PlayController {

	/**
	 * Get all the subscriptions
	 * 
	 */
	public static void subscriptions() {
		SubscriptionRegistry registry = null;
		try {
			registry = Locator.getSubscriptionRegistry(getNode());
		} catch (Exception e) {
			handleException("Locator error", e);
		}

		List<Subscription> subscriptions = null;
		try {
			subscriptions = registry.getSubscriptions();
		} catch (GovernanceExeption e) {
			e.printStackTrace();
		}
		render(subscriptions);
	}

	public static void subscription(String id) {
		handleException("Null id", new Exception(
				"Can not get subscription from null ID"));

		SubscriptionRegistry registry = null;
		try {
			registry = Locator.getSubscriptionRegistry(getNode());
		} catch (Exception e) {
			handleException("Locator error", e);
		}

		Subscription filter = new Subscription();
		filter.setId(id);
		List<Subscription> subscriptions = null;
		try {
			subscriptions = registry.getSubscriptions(filter);
		} catch (GovernanceExeption e) {
			e.printStackTrace();
		}

		Subscription subscription = null;
		if (subscriptions != null && subscriptions.size() > 0) {
			subscription = subscriptions.get(0);
		}
		render(subscription);
	}

	public static void create() {
		render();
	}

	/**
	 * POST. Creates a subscription.
	 * 
	 * @param consumer
	 * @param provider
	 * @param topicName
	 * @param topicNS
	 * @param topicPrefix
	 */
	public static void createNew(String consumer, String provider,
			String topicname, String topicns, String topicprefix, boolean save) {
		
		validation.url(consumer);
		validation.url(provider);
		validation.required(topicname);
		validation.url(topicns);
		validation.required(topicprefix);

		if (validation.hasErrors()) {
			params.flash();
			validation.keep();
			create();
		}

		try {
			SubscriptionService client = Locator
					.getSubscriptionService(getNode());

			Subscription subscription = new Subscription();
			subscription.setProvider(provider);
			subscription.setSubscriber(consumer);
			Topic topic = new Topic();
			topic.setName(topicname);
			topic.setNs(topicns);
			topic.setPrefix(topicprefix);
			subscription.setTopic(topic);

			Subscription result = client.subscribe(subscription);
			if (result != null && save) {
				// register
				SubscriptionRegistry registry = Locator
						.getSubscriptionRegistry(getNode());
				registry.addSubscription(subscription);
			}

			flash.success("Subscription has been created %s",
					result.toString());
			
		} catch (GovernanceExeption ge) {
			handleException("Can not subscribe", ge);
		} catch (Exception e) {
			handleException("Locator error", e);
		}

		// Forward to subscription service
		create();
	}
}
