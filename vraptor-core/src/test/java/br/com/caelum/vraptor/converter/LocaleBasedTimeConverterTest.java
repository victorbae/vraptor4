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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.converter.ConversionError;
import br.com.caelum.vraptor.converter.LocaleBasedTimeConverter;
import br.com.caelum.vraptor.core.JstlLocalization;
import br.com.caelum.vraptor.core.RequestInfo;
import br.com.caelum.vraptor.http.MutableRequest;

public class LocaleBasedTimeConverterTest {

    static final String LOCALE_KEY = "javax.servlet.jsp.jstl.fmt.locale";
    
    private LocaleBasedTimeConverter converter;
    private @Mock MutableRequest request;
    private @Mock HttpSession session;
    private @Mock ServletContext context;
    private @Mock ResourceBundle bundle;
    private @Mock JstlLocalization jstlLocalization;
    
	@Before
	public void setup() {
        MockitoAnnotations.initMocks(this);
        
        FilterChain chain = mock(FilterChain.class);
        final RequestInfo webRequest = new RequestInfo(context, chain, request, null);
        jstlLocalization = new JstlLocalization(webRequest);
        converter = new LocaleBasedTimeConverter(jstlLocalization);
        bundle = ResourceBundle.getBundle("messages");
        Locale.setDefault(Locale.ENGLISH);
	}

	@Test
	public void shouldBeAbleToConvert() throws ParseException {
        when(request.getAttribute(LOCALE_KEY + ".request")).thenReturn("pt_br");
        
		Date date = new SimpleDateFormat("HH:mm:ss").parse("23:52:00");
		assertThat(converter.convert("23:52", Time.class, bundle), is(equalTo(date)));
		assertThat(converter.convert("23:52:00", Time.class, bundle), is(equalTo(date)));
	}

	@Test
	public void shouldUseTheDefaultLocale() throws ParseException {
        when(request.getAttribute(LOCALE_KEY + ".request")).thenReturn(null);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(LOCALE_KEY + ".session")). thenReturn(null);
        when(context.getAttribute(LOCALE_KEY + ".application")). thenReturn(null);
        when(context.getInitParameter(LOCALE_KEY)). thenReturn(null);
        when(request.getLocale()).thenReturn(null);
        
		Date date = new SimpleDateFormat("HH:mm:ss").parse("23:52:00");
		String formattedHour = DateFormat.getTimeInstance(DateFormat.SHORT).format(date);
		assertThat(converter.convert(formattedHour, Time.class, bundle), is(equalTo(date)));
		
	}

	@Test
	public void shouldBeAbleToConvertEmpty() {
		assertThat(converter.convert("", Time.class, bundle), is(nullValue()));
	}

	@Test
	public void shouldBeAbleToConvertNull() {
		assertThat(converter.convert(null, Time.class, bundle), is(nullValue()));
	}

	@Test
	public void shouldThrowExceptionWhenUnableToParse() {
        when(request.getAttribute(LOCALE_KEY + ".request")).thenReturn("pt_br");
        
		try {
			converter.convert("25:dd:88", Time.class, bundle);
		} catch (ConversionError e) {
			assertThat(e.getMessage(), is(equalTo("25:dd:88 is not a valid time.")));
		}
	}
}
