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

import org.opensaml.Configuration;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.Marshaller;
import org.opensaml.xml.io.MarshallerFactory;
import org.opensaml.xml.io.Unmarshaller;
import org.opensaml.xml.io.UnmarshallerFactory;
import org.opensaml.xml.parse.BasicParserPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * @author Scott Battaglia
 * @version $Revision$ $Date$
 * @since 4.0.0
 */
public final class XmlMarshallingUtils {

    private static final Logger logger = LoggerFactory.getLogger(XmlMarshallingUtils.class);

    // TODO we don't really want this to be static, but its fine for now.
    private static final BasicParserPool ppMgr = new BasicParserPool();

    static {
        ppMgr.setNamespaceAware(true);
    }

    private XmlMarshallingUtils() {
        // nothing to do.  Just preventing you from creating your own instances.
    }

    public static String marshall(final XMLObject xmlObject) {
        try {
            final MarshallerFactory marshallerFactory = Configuration.getMarshallerFactory();
            final Marshaller marshaller  = marshallerFactory.getMarshaller(xmlObject);
            final Element e = marshaller.marshall(xmlObject);

            final TransformerFactory transformerFactory = TransformerFactory.newInstance();
            final Transformer transformer = transformerFactory.newTransformer();
            final DOMSource source = new DOMSource(e);
            final StringWriter sw = new StringWriter();
            final StreamResult streamResult = new StreamResult(sw);
            transformer.transform(source, streamResult);

            return sw.toString();
        } catch (final Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    public static <T> T unmarshall(final String response) {
        try {
            final UnmarshallerFactory unmarshallerFactory = Configuration.getUnmarshallerFactory();
            final Element e = stringToElement(response);
            final Unmarshaller unmarshaller = unmarshallerFactory.getUnmarshaller(e);
            final XMLObject xmlObject = unmarshaller.unmarshall(e);

            return (T) xmlObject;
        } catch (final Exception e) {
            logger.info(e.getMessage(), e);
            throw new IllegalArgumentException(e);
        }
    }

    public static Element stringToElement(final String string) {
        try {
            final Document d = ppMgr.parse(new StringReader(string));
            return d.getDocumentElement();
        } catch (final Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }
}
