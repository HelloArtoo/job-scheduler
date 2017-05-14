/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.demo.flow.pbs.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.jd.framework.job.demo.fixture.model.Product;

public class ProductService {

	private final int skuTotal = 1000 * 1000 * 5;
	private Map<Integer, Product> data = new ConcurrentHashMap<>(skuTotal, 1);

	public ProductService() {
		System.out.println("正在初始化 Product 500W sku...");
		int tmp = 1000 * 1000 * 10;
		int[] dcs = { 3, 6, 10 };
		int index = 0;
		for (int i = 0; i < skuTotal; i++) {
			int sku = (tmp + i);
			int dc = dcs[index++];
			if (index == 3) {
				index = 0;
			}
			data.put(sku, new Product((long) sku, "自营商品" + i, dc));
		}
		System.out.println(String.format("500W sku初始化结束。"));
	}

	public List<Product> fetchProducts(String parameter, int framecount) {

		List<Product> result = new ArrayList<>();
		int dc = Integer.valueOf(parameter);
		int count = 0;
		for (Entry<Integer, Product> entry : data.entrySet()) {
			Product product = entry.getValue();
			if (product.getDc() == dc && !product.isDone()) {
				result.add(product);
				count++;
			}
			if (count == framecount) {
				break;
			}
		}

		return result;
	}
}
