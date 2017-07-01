/*   
 * Copyright (c) 2010-2020 Founder Ltd. All Rights Reserved.   
 *   
 * This software is the confidential and proprietary information of   
 * Founder. You shall not disclose such Confidential Information   
 * and shall use it only in accordance with the terms of the agreements   
 * you entered into with Founder.   
 *   
 */
package com.jd.framework.job.console.repository;

import java.io.File;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import com.jd.framework.job.console.exception.JobConsoleException;
import com.jd.framework.job.console.utils.HomeFolder;

public abstract class AbstractXmlRepository<E> implements XmlRepository<E> {
	
	private final File file;

	private final Class<E> clazz;

	private JAXBContext jaxbContext;

	protected AbstractXmlRepository(final String fileName, final Class<E> clazz) {
		file = new File(HomeFolder.getFilePathInHomeFolder(fileName));
		this.clazz = clazz;
	}

	@PostConstruct
	private void init() {
		HomeFolder.createHomeFolderIfNotExisted();
		try {
			jaxbContext = JAXBContext.newInstance(clazz);
		} catch (final JAXBException ex) {
			throw new JobConsoleException(ex);
		}
	}

	@Override
	public synchronized E load() {
		if (!file.exists()) {
			try {
				return clazz.newInstance();
			} catch (final InstantiationException | IllegalAccessException ex) {
				throw new JobConsoleException(ex);
			}
		}
		try {
			@SuppressWarnings("unchecked")
			E result = (E) jaxbContext.createUnmarshaller().unmarshal(file);
			return result;
		} catch (final JAXBException ex) {
			throw new JobConsoleException(ex);
		}
	}

	@Override
	public synchronized void save(final E entity) {
		try {
			Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.marshal(entity, file);
		} catch (final JAXBException ex) {
			throw new JobConsoleException(ex);
		}
	}
}
