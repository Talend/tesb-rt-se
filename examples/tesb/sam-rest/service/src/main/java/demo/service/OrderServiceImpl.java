/*
 * #%L
 * TESB :: Examples :: Locator Rest Client
 * %%
 * Copyright (c) 2006-2021 Talend Inc. - www.talend.com
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

package demo.service;

import java.util.HashSet;
import java.util.Set;

import demo.common.*;

public class OrderServiceImpl implements OrderService {

	static Set<Order> orderList = new HashSet<Order>();

	// populate some init data
	static {
		Order order1 = new Order();
		order1.setOrderId("1");
		order1.setDescription("Order1 Description");
		orderList.add(order1);
	}

	public Order getOrder(String id) {
		for (Order order : orderList) {
			if (order.getOrderId().equals(id)) {
				System.out.println("Sending order id="+id+" to the client...");
				System.out.println("Order Descriprion is:"+order.getDescription());
				return order;
			}
		}
		return null;
	}

	public void addOrder(Order order) {
		orderList.add(order);
	}

}
