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

import models.Message;

import org.ow2.play.service.registry.api.Registry;

import play.jobs.Job;
import play.libs.F.Promise;
import play.libs.WS;
import play.libs.WS.HttpResponse;
import utils.Locator;

/**
 * Init Controller. Used to initialize the platform
 * 
 * @author chamerling
 * 
 */
public class LifeCycleController extends PlayController {
	
	public static void index() {
		render();
	}

	/**
	 * Launch the bootstrap operation in the background
	 * 
	 */
	public static void subscriptionsInit() {
		try {
			final String url = Locator.getBootstrapServiceREST(getNode())
					+ "init/all";
			try {
				new Job() {
					@Override
					public void doJob() throws Exception {
						HttpResponse response = WS.url(url).get();
						Message m = new Message();
						if (!response.success()) {
							m.title = "Error";
							m.content = "Error while invoking the Bootstrap REST Service";
						} else {
							m.title = "Success";
							m.content = "Bootstrap REST Service has been invoked";
						}
						BackgroundTaskWebSocket.liveStream.publish(m);
					}
				}.now();
			} catch (Exception e) {
				e.printStackTrace();
				flash.error("Unable bootstrap subscriptions '%s'", e.getMessage());
			}

		} catch (Exception e) {
			handleException("Fail to get endpoint", e);
		}
		index();
	}
}
