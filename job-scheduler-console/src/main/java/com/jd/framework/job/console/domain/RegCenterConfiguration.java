/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.console.domain;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(exclude = { "zkAddressList", "namespace", "digest", "activated" })
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@NoArgsConstructor
public class RegCenterConfiguration implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5895184702501659193L;

	@XmlAttribute(required = true)
	private String name;

	@XmlAttribute(required = true)
	private String zkAddressList;

	@XmlAttribute
	private String namespace;

	@XmlAttribute
	private String digest;

	@XmlAttribute
	private boolean activated;

	public RegCenterConfiguration(final String name) {
		this.name = name;
	}

}
