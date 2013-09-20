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

import models.EventCloud;

import org.ow2.play.governance.api.EventCloudsManagementWs;
import org.ow2.play.governance.api.GovernanceExeption;

import play.mvc.With;
import utils.Locator;

/**
 * @author chamerling
 * 
 */
@With(Secure.class)
public class EventCloudController extends PlayController {

	public static void list() {
		EventCloudsManagementWs client = null;
		try {
			client = Locator.getEventCloudManagementService(getNode());
		} catch (Exception e) {
			handleException(null, e);
		}

		List<String> list = null;
		try {
			list = client.getEventCloudIds();
		} catch (Exception e) {
			handleException("Unable to get EC IDs", e);
		}
		render(list);
	}
	
	public static void ec(String id) {
		EventCloudsManagementWs client = null;
		try {
			client = Locator.getEventCloudManagementService(getNode());
		} catch (Exception e) {
			handleException(null, e);
		}
		
		EventCloud ec = new EventCloud();
		try {
			ec.publish = client.getPublishWsnServiceEndpointUrls(id);
			ec.putget = client.getPutGetWsProxyEndpointUrls(id);
			ec.subscribe = client.getSubscribeWsnProxyEndpointUrls(id);
		} catch (GovernanceExeption e) {
			flash.error("Error while getting information for %s : %s", id, e.getMessage());
			list();
		}
		ec.stream = id;
		render(ec);
	}
}
