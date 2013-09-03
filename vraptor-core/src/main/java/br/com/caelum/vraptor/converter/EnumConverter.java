/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 * 	http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 */

package br.com.caelum.vraptor.converter;

import static com.google.common.base.Strings.isNullOrEmpty;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import br.com.caelum.vraptor.Convert;
import br.com.caelum.vraptor.Converter;

/**
 * Accepts either the ordinal value or name. Null and empty strings are treated
 * as null.
 *
 * @author Guilherme Silveira
 */
@Convert(Enum.class)
public class EnumConverter implements Converter {

	/**
	 * Enums are always final, so I can suppress this warning safely
	 */
	@SuppressWarnings("unchecked")
    public Object convert(String value, Class type, ResourceBundle bundle) {
	    if (isNullOrEmpty(value)) {
            return null;
        }
	    
        if (Character.isDigit(value.charAt(0))) {
            return resolveByOrdinal(value, type, bundle);
        } else {
            return resolveByName(value, type, bundle);
        }
    }

    private Object resolveByName(String value, Class enumType, ResourceBundle bundle) {
        try {
            return Enum.valueOf(enumType, value);
        } catch (IllegalArgumentException e) {
			throw new ConversionError(MessageFormat.format(bundle.getString("is_not_a_valid_enum_value"), value));
        }
    }

    private Object resolveByOrdinal(String value, Class enumType, ResourceBundle bundle) {
        try {
            int ordinal = Integer.parseInt(value);
            if (ordinal >= enumType.getEnumConstants().length) {
    			throw new ConversionError(MessageFormat.format(bundle.getString("is_not_a_valid_enum_value"), value));
            }
            return enumType.getEnumConstants()[ordinal];
        } catch (NumberFormatException e) {
			throw new ConversionError(MessageFormat.format(bundle.getString("is_not_a_valid_enum_value"), value));
        }
    }

}
