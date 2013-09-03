/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource All rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package br.com.caelum.vraptor.validator;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory for JSR303 ValidatorFactory
 *
 * @author Lucas Cavalcanti
 * @author Otávio Scherer Garcia
 * @since 3.1.3
 *
 */
@ApplicationScoped
@Alternative
public class ValidatorFactoryCreator {

	private static final Logger logger = LoggerFactory.getLogger(ValidatorFactoryCreator.class);

	private ValidatorFactory factory;
	
	@PostConstruct
	public void buildFactory() {
		factory = Validation.byDefaultProvider()
		        .configure()
		        .buildValidatorFactory();

        logger.debug("Initializing JSR303 factory for bean validation");
	}

	@Produces @Default @javax.enterprise.context.ApplicationScoped
	public ValidatorFactory getInstance() {
		if (factory == null) { //pico don't call PostConstruct
			buildFactory();
		}
		return factory;
	}
}
