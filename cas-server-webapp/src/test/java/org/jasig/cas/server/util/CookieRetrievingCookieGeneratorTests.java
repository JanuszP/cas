/**
 * Licensed to Jasig under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Jasig licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jasig.cas.server.util;

import javax.servlet.http.Cookie;

import junit.framework.TestCase;
import org.jasig.cas.server.login.DefaultLoginRequestImpl;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * 
 * @author Scott Battaglia
 * @version $Revision: 1.1 $ $Date: 2005/08/19 18:27:17 $
 * @since 3.2.1
 *
 */
public final class CookieRetrievingCookieGeneratorTests extends TestCase {
    
    private CookieRetrievingCookieGenerator g;

    protected void setUp() throws Exception {
        this.g = new CookieRetrievingCookieGenerator();
        this.g.setRememberMeMaxAge(100);
        this.g.setCookieDomain("cas.org");
        this.g.setCookieMaxAge(5);
        this.g.setCookiePath("/");
        this.g.setCookieName("test");
    }
    
    public void testCookieAddWithRememberMe() {
        final DefaultLoginRequestImpl request = new DefaultLoginRequestImpl(null, "ha", false);
        request.setLongTermLoginRequest(true);
        final MockHttpServletResponse response = new MockHttpServletResponse();
        
       this.g.addCookie(request, response, "test");
        
        final Cookie c = response.getCookie("test");
        assertEquals(100, c.getMaxAge());
        assertEquals("test", c.getValue());
    }
    
    public void testCookieAddWithoutRememberMe() {
        final DefaultLoginRequestImpl request = new DefaultLoginRequestImpl(null, "ha", false);
        final MockHttpServletResponse response = new MockHttpServletResponse();
        
        this.g.addCookie(request, response, "test");
        
        final Cookie c = response.getCookie("test");
        assertEquals(5, c.getMaxAge());
        assertEquals("test", c.getValue());
    }
    
    public void testCookieRetrieve() {
        final MockHttpServletRequest request = new MockHttpServletRequest();
        final Cookie cookie = new Cookie("test", "test");
        cookie.setDomain("cas.org");
        cookie.setMaxAge(5);
        request.setCookies(cookie);
        
        assertEquals("test", this.g.retrieveCookieValue(request));

        
    }
    
    

}
