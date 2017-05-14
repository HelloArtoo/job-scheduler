/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.demo.simple.pbs;

import java.util.Date;
import java.util.List;

import com.jd.framework.job.api.SegmentContext;
import com.jd.framework.job.api.simple.SimpleJob;
import com.jd.framework.job.demo.fixture.model.Product;
import com.jd.framework.job.demo.flow.pbs.service.ProductService;
import com.jd.framework.job.demo.flow.pbs.service.ProductServiceFactory;

/**
 * 场景： 商品同步至数据源
 * 
 * 
 * @author Rong Hu
 * @version 1.0, 2017-5-13
 */
public class ProductSyncJob implements SimpleJob {

	private ProductService productSerice = ProductServiceFactory.getSerivce();
	// 一帧50个SKU
	private final int FRAME_COUNT = 50;

	@Override
	public void execute(SegmentContext segmentContext) {
		System.out.println(String.format(
				"@@Job-Scheduller@@: Thread ID: %s, Date: %s, Segment Context: %s, Action: %s", Thread.currentThread()
						.getId(), new Date(), segmentContext, " simple job executing."));

		List<Product> data = productSerice.fetchProducts(segmentContext.getSegmentParameter(), FRAME_COUNT);
		for (Product each : data) {
			each.setDone(true);
			System.out.println(String.format("Product {sku: [%s],productName:[%s]} is done. ", each.getSku(),
					each.getProductName()));
		}
	}
}
