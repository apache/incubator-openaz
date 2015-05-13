/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */

/*
 *                        AT&T - PROPRIETARY
 *          THIS FILE CONTAINS PROPRIETARY INFORMATION OF
 *        AT&T AND IS NOT TO BE DISCLOSED OR USED EXCEPT IN
 *             ACCORDANCE WITH APPLICABLE AGREEMENTS.
 *
 *          Copyright (c) 2013 AT&T Knowledge Ventures
 *              Unpublished and Not for Publication
 *                     All Rights Reserved
 */
package org.apache.openaz.xacml.std.jaxp;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributesType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.RequestReferenceType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.RequestType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.openaz.xacml.std.StdMutableRequest;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * JaxpRequest extends {@link com.att.research.xacml.std.StdMutableRequest} with methods for creation from
 * JAXP elements.
 */
public class JaxpRequest extends StdMutableRequest {
    private static Log logger = LogFactory.getLog(JaxpRequest.class);

    public JaxpRequest() {
    }

    public static JaxpRequest newInstance(RequestType requestType) {
        if (requestType == null) {
            throw new NullPointerException("Null RequestType");
        }
        JaxpRequest jaxpRequest = new JaxpRequest();
        jaxpRequest.setCombinedDecision(requestType.isCombinedDecision());
        jaxpRequest.setReturnPolicyIdList(requestType.isReturnPolicyIdList());
        if (requestType.getAttributes() != null) {
            Iterator<AttributesType> iterAttributesTypes = requestType.getAttributes().iterator();
            while (iterAttributesTypes.hasNext()) {
                jaxpRequest.add(JaxpRequestAttributes.newInstance(iterAttributesTypes.next()));
            }
        }
        if (requestType.getMultiRequests() != null
            && requestType.getMultiRequests().getRequestReference() != null) {
            Iterator<RequestReferenceType> iterRequestReferenceTypes = requestType.getMultiRequests()
                .getRequestReference().iterator();
            while (iterRequestReferenceTypes.hasNext()) {
                jaxpRequest.add(JaxpRequestReference.newInstance(iterRequestReferenceTypes.next()));
            }
        }
        if (requestType.getRequestDefaults() != null) {
            jaxpRequest.setRequestDefaults(JaxpRequestDefaults.newInstance(requestType.getRequestDefaults()));
        }

        return jaxpRequest;
    }

    /**
     * Creates a new <code>JaxpRequest</code> by loading it from an XML <code>File</code>.
     *
     * @param fileXmlRequest the <code>File</code> containing the Request XML
     * @return a new <code>JaxpRequest</code> generated by parsing the given XML file
     * @throws javax.xml.parsers.ParserConfigurationException
     * @throws java.io.IOException
     * @throws org.xml.sax.SAXException
     * @throws javax.xml.bind.JAXBException
     */
    public static JaxpRequest load(File fileXmlRequest) throws ParserConfigurationException, IOException,
        SAXException, JAXBException {
        if (fileXmlRequest == null) {
            throw new NullPointerException("Null File");
        }

        /*
         * Create XML document factory and builder
         */
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

        /*
         * Parse the file into a Document
         */
        Document document = documentBuilder.parse(fileXmlRequest);
        if (document == null) {
            logger.error("No Document returned parsing \"" + fileXmlRequest.getAbsolutePath() + "\"");
            return null;
        }

        NodeList nodeListRoot = document.getChildNodes();
        if (nodeListRoot == null || nodeListRoot.getLength() == 0) {
            logger.warn("No child elements of the XML document");
            return null;
        }
        Node nodeRoot = nodeListRoot.item(0);
        if (nodeRoot == null || nodeRoot.getNodeType() != Node.ELEMENT_NODE) {
            logger.warn("Root of the document is not an ELEMENT");
            return null;
        }

        JAXBContext context = JAXBContext.newInstance(RequestType.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        JAXBElement<RequestType> jaxbElementRequest = unmarshaller.unmarshal(nodeRoot,
                                                                             RequestType.class);
        if (jaxbElementRequest == null || jaxbElementRequest.getValue() == null) {
            logger.error("JAXB unmarshalling did not return a RequestType node");
            return null;
        }
        return JaxpRequest.newInstance(jaxbElementRequest.getValue());

    }

    public static void main(String[] args) {
        for (String fileName : args) {
            JaxpRequest jaxpRequest = null;
            try {
                jaxpRequest = JaxpRequest.load(new File(fileName));
            } catch (Exception ex) {
                logger.fatal("Failed to load \"" + fileName + "\" as a JaxpRequest", ex);
                continue;
            }
            if (jaxpRequest == null) {
                logger.warn("Null JaxpRequest returned for file \"" + fileName + "\"");
            } else {
                logger.info("JaxpRequest for file \"" + fileName + "\"=" + jaxpRequest.toString());
            }
        }
    }

}
