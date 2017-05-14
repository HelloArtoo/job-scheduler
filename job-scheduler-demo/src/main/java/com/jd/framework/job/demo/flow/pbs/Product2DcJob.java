/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.demo.flow.pbs;

import java.util.Date;
import java.util.List;

import com.jd.framework.job.api.SegmentContext;
import com.jd.framework.job.api.flow.FlowJob;
import com.jd.framework.job.demo.fixture.model.Product;
import com.jd.framework.job.demo.flow.pbs.service.ProductService;
import com.jd.framework.job.demo.flow.pbs.service.ProductServiceFactory;

/**
 * 
 * 场景：商品配送至某仓
 * 
 * @author Rong Hu
 * @version 1.0, 2017-5-13
 */
public class Product2DcJob implements FlowJob<Product> {

	private ProductService productSerice = ProductServiceFactory.getSerivce();
	// 一帧50个SKU
	private final int FRAME_COUNT = 50;

	@Override
	public List<Product> fetchData(SegmentContext segmentContext) {
		System.out.println(String.format("Job-Scheduller: Thread ID: %s, Date: %s, Segment Context: %s, Action: %s",
				Thread.currentThread().getId(), new Date(), segmentContext, " flow job fetch data"));
		return productSerice.fetchProducts(segmentContext.getSegmentParameter(), FRAME_COUNT);
	}

	@Override
	public void processData(SegmentContext segmentContext, List<Product> data) {
		System.out.println(String.format("Job-Scheduller: Thread ID: %s, Date: %s, Segment Context: %s, Action: %s",
				Thread.currentThread().getId(), new Date(), segmentContext, " flow job is processing data"));

		for (Product product : data) {
			product.setDone(true);
			System.out.println(String.format("Product {sku: [%s],productName:[%s]} is in dc [%s]. ", product.getSku(),
					product.getProductName(), product.getDc()));
		}
	}

}
