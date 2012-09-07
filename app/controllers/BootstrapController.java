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

import org.ow2.play.governance.api.BootSubscriptionService;
import org.ow2.play.governance.api.GovernanceExeption;
import org.ow2.play.governance.api.SubscriptionManagement;
import org.ow2.play.governance.api.SubscriptionService;
import org.ow2.play.governance.api.bean.Subscription;

import utils.Locator;

/**
 * @author chamerling
 * 
 */
public class BootstrapController extends PlayController {

	/**
	 * List subscriptions which will be replayed...
	 */
	public static void subscriptions() {
		BootSubscriptionService client = null;
		try {
			client = Locator.getBootSubscriptionService(getNode());
		} catch (Exception e) {
			handleException("Locator error", e);
		}

		List<Subscription> subscriptions = null;
		try {
			subscriptions = client.all();
		} catch (GovernanceExeption e) {
			e.printStackTrace();
		}
		render(subscriptions);
	}

	/**
	 * Launch all the boot subscriptions. All subscriptions will be launched
	 * here!
	 */
	public static void launchBootSubscriptions() {
		
		BootSubscriptionService client = null;
		try {
			client = Locator.getBootSubscriptionService(getNode());
		} catch (Exception e) {
			handleException("Locator error", e);
		}

		SubscriptionManagement subscriptionService = null;
		try {
			subscriptionService = Locator.getSubscriptionManagement(getNode());
		} catch (Exception e) {
			handleException("Locator error", e);
		}

		List<Subscription> subscriptions = null;
		try {
			subscriptions = client.all();
		} catch (GovernanceExeption e) {
			e.printStackTrace();
		}

		List<Subscription> result = null;
		if (subscriptions != null) {
			try {
				result = subscriptionService.subscribe(subscriptions);
			} catch (GovernanceExeption e) {
				flash.error("Error while creating subscriptions '%s'",
						e.getMessage());
			}
		}
		subscriptionsResult(result);
	}

	/**
	 * Display subscriptions
	 * 
	 * @param subscriptions
	 */
	public static final void subscriptionsResult(
			List<Subscription> subscriptions) {
		renderTemplate("BootstrapController/subscriptionsresult.html",
				subscriptions);
	}

}
