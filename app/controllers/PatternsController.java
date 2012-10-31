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

import org.ow2.play.governance.api.PatternRegistry;
import org.ow2.play.governance.api.bean.Pattern;

import utils.Locator;

/**
 * Manage platform patterns
 * 
 * @author chamerling
 * 
 */
public class PatternsController extends PlayController {

	public static void list() {
		PatternRegistry client = null;
		try {
			client = Locator.getPatternRegistry(getNode());
		} catch (Exception e) {
			handleException(null, e);
		}

		List<Pattern> list = null;
		try {
			list = client.all();
		} catch (Exception e) {
			handleException("Unable to get patterns", e);
		}
		render(list);
	}
}
