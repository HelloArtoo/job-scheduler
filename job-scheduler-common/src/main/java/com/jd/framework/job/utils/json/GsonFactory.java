/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.utils.json;

import java.lang.reflect.Type;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;

/**
 * 
 * Gson 构造工厂
 * 
 * @author Rong Hu
 * @version 1.0, 2017-4-4
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GsonFactory {

	private static final GsonBuilder GSON_BUILDER = new GsonBuilder();

	private static volatile Gson gson = GSON_BUILDER.create();

	/**
	 * 注册Gson解析对象.
	 * 
	 * @param type
	 *            Gson解析对象类型
	 * @param typeAdapter
	 *            Gson解析对象适配器
	 */
	public static synchronized void registerTypeAdapter(final Type type, final TypeAdapter<?> typeAdapter) {
		GSON_BUILDER.registerTypeAdapter(type, typeAdapter);
		gson = GSON_BUILDER.create();
	}

	/**
	 * 获取Gson实例.
	 * 
	 * @return Gson实例
	 */
	public static Gson getGson() {
		return gson;
	}
}