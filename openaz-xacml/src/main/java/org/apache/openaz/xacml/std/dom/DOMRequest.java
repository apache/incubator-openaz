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
package org.apache.openaz.xacml.std.dom;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.openaz.xacml.api.Request;
import org.apache.openaz.xacml.api.XACML3;
import org.apache.openaz.xacml.std.StdMutableRequest;
import org.apache.openaz.xacml.std.StdRequest;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * DOMRequest is used to convert XML into {@link com.att.research.xacml.api.Request} objects. The
 * {@link com.att.research.xacml.api.Request} objects returned by this class are instances of
 * {@link com.att.research.xacml.std.StdMutableRequest}, not <code>DOMRequest</code>. Object instances are
 * generated by loading a file, string, InputStream, or XML Node tree representing the Request.
 */
public class DOMRequest {
    private static final Log logger = LogFactory.getLog(DOMRequest.class);

    /*
     * Prevent creation of instances - this class contains only static methods that return other object types.
     */
    protected DOMRequest() {
    }

    /**
     * Parse and XML string into a {@link org.apache.openaz.xacml.api.Request} object.
     *
     * @param xmlString
     * @return
     * @throws DOMStructureException
     */
    public static Request load(String xmlString) throws DOMStructureException {
        Request request = null;
        InputStream is = null;
        try {
            is = new ByteArrayInputStream(xmlString.getBytes("UTF-8"));
            request = DOMRequest.load(is);
        } catch (UnsupportedEncodingException ex) {
            throw new DOMStructureException("Exception loading String Request: " + ex.getMessage(), ex);
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (Exception idontcare) {
            }
        }
        return request;
    }

    /**
     * Read a file containing the XML description of a XACML Request and parse it into a
     * {@link com.att.research.xacml.api.Request} Object. This is only used for testing. In normal operation a
     * Request arrives through the RESTful interface and is processed using
     * <code>load(String xmlString)</code>.
     *
     * @param fileRequest
     * @return
     * @throws DOMStructureException
     */
    public static Request load(File fileRequest) throws DOMStructureException {
        Request request = null;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(fileRequest);
            request = DOMRequest.load(fis);
        } catch (FileNotFoundException ex) {
            throw new DOMStructureException("Exception loading File Request: " + ex.getMessage(), ex);
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (Exception idontcare) {
            }
        }
        return request;
    }

    /**
     * Read characters from the given <code>InputStream</code> and parse them into an XACML
     * {@link com.att.research.xacml.api.Request} object.
     *
     * @param is
     * @return
     * @throws DOMStructureException
     */
    public static Request load(InputStream is) throws DOMStructureException {
        /*
         * Get the DocumentBuilderFactory
         */
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        if (documentBuilderFactory == null) {
            throw new DOMStructureException("No XML DocumentBuilderFactory configured");
        }
        documentBuilderFactory.setNamespaceAware(true);

        /*
         * Get the DocumentBuilder
         */
        DocumentBuilder documentBuilder = null;
        try {
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
        } catch (Exception ex) {
            throw new DOMStructureException("Exception creating DocumentBuilder: " + ex.getMessage(), ex);
        }

        /*
         * Parse the XML file
         */
        Document document = null;
        Request request = null;
        try {
            document = documentBuilder.parse(is);
            if (document == null) {
                throw new Exception("Null document returned");
            }

            Node rootNode = document.getFirstChild();
            while (rootNode != null && rootNode.getNodeType() != Node.ELEMENT_NODE) {
                rootNode = rootNode.getNextSibling();
            }
            if (rootNode == null) {
                throw new Exception("No child in document");
            }

            if (DOMUtil.isInNamespace(rootNode, XACML3.XMLNS)) {
                if (XACML3.ELEMENT_REQUEST.equals(rootNode.getLocalName())) {
                    request = DOMRequest.newInstance(rootNode);
                    if (request == null) {
                        throw new DOMStructureException("Failed to parse Request");
                    }
                } else {
                    throw DOMUtil.newUnexpectedElementException(rootNode);
                }
            } else {
                throw DOMUtil.newUnexpectedElementException(rootNode);
            }
        } catch (Exception ex) {
            throw new DOMStructureException("Exception loading Request: " + ex.getMessage(), ex);
        }
        return request;
    }

    /**
     * Creates a new {@link com.att.research.xacml.api.Request} by parsing the given <code>Node</code>
     * representing a XACML Request element.
     *
     * @param nodeRequest the <code>Node</code> representing the XACML Request element.
     * @return a new {@link com.att.research.xacml.std.StdMutableRequest} parsed from the given
     *         <code>Node</code>
     * @throws DOMStructureException if the conversion cannot be made
     */
    public static Request newInstance(Node nodeRequest) throws DOMStructureException {
        Element elementRequest = DOMUtil.getElement(nodeRequest);
        boolean bLenient = DOMProperties.isLenient();

        StdMutableRequest stdMutableRequest = new StdMutableRequest();

        stdMutableRequest.setReturnPolicyIdList(DOMUtil
            .getBooleanAttribute(elementRequest, XACML3.ATTRIBUTE_RETURNPOLICYIDLIST, !bLenient));
        stdMutableRequest.setCombinedDecision(DOMUtil.getBooleanAttribute(elementRequest,
                                                                          XACML3.ATTRIBUTE_COMBINEDDECISION,
                                                                          !bLenient));

        NodeList children = elementRequest.getChildNodes();
        int numChildren;
        boolean sawAttributes = false;
        if (children != null && (numChildren = children.getLength()) > 0) {
            for (int i = 0; i < numChildren; i++) {
                Node child = children.item(i);
                if (DOMUtil.isElement(child)) {
                    if (DOMUtil.isInNamespace(child, XACML3.XMLNS)) {
                        String childName = child.getLocalName();
                        if (XACML3.ELEMENT_ATTRIBUTES.equals(childName)) {
                            stdMutableRequest.add(DOMRequestAttributes.newInstance(child));
                            sawAttributes = true;
                        } else if (XACML3.ELEMENT_REQUESTDEFAULTS.equals(childName)) {
                            stdMutableRequest.setRequestDefaults(DOMRequestDefaults.newInstance(child));
                        } else if (XACML3.ELEMENT_MULTIREQUESTS.equals(childName)) {
                            NodeList grandchildren = child.getChildNodes();
                            int numGrandchildren;
                            if (grandchildren != null && (numGrandchildren = grandchildren.getLength()) > 0) {
                                for (int j = 0; j < numGrandchildren; j++) {
                                    Node grandchild = grandchildren.item(j);
                                    if (DOMUtil.isElement(grandchild)) {
                                        if (DOMUtil.isInNamespace(grandchild, XACML3.XMLNS)) {
                                            if (XACML3.ELEMENT_REQUESTREFERENCE.equals(grandchild
                                                .getLocalName())) {
                                                stdMutableRequest.add(DOMRequestReference
                                                    .newInstance(grandchild));
                                            } else {
                                                if (!bLenient) {
                                                    throw DOMUtil.newUnexpectedElementException(grandchild,
                                                                                                nodeRequest);
                                                }
                                            }
                                        } else {
                                            if (!bLenient) {
                                                throw DOMUtil.newUnexpectedElementException(grandchild,
                                                                                            nodeRequest);
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            if (!bLenient) {
                                throw DOMUtil.newUnexpectedElementException(child, nodeRequest);
                            }
                        }
                    } else {
                        if (!bLenient) {
                            throw DOMUtil.newUnexpectedElementException(child, nodeRequest);
                        }
                    }
                }
            }
        }
        if (!sawAttributes && !bLenient) {
            throw DOMUtil.newMissingElementException(nodeRequest, XACML3.XMLNS, XACML3.ELEMENT_ATTRIBUTES);
        }

        return new StdRequest(stdMutableRequest);
    }

    /**
     * Convert XACML2 into XACML3.
     *
     * @param nodeRequest
     * @return
     * @throws DOMStructureException
     */
    public static boolean repair(Node nodeRequest) throws DOMStructureException {
        Element elementRequest = DOMUtil.getElement(nodeRequest);
        boolean result = false;

        result = DOMUtil.repairBooleanAttribute(elementRequest, XACML3.ATTRIBUTE_RETURNPOLICYIDLIST, false,
                                                logger) || result;
        result = DOMUtil.repairBooleanAttribute(elementRequest, XACML3.ATTRIBUTE_COMBINEDDECISION, false,
                                                logger) || result;

        NodeList children = elementRequest.getChildNodes();
        int numChildren;
        boolean sawAttributes = false;
        if (children != null && (numChildren = children.getLength()) > 0) {
            for (int i = 0; i < numChildren; i++) {
                Node child = children.item(i);
                if (DOMUtil.isElement(child)) {
                    if (DOMUtil.isInNamespace(child, XACML3.XMLNS)) {
                        String childName = child.getLocalName();
                        if (XACML3.ELEMENT_ATTRIBUTES.equals(childName)) {
                            result = DOMRequestAttributes.repair(child) || result;
                            sawAttributes = true;
                        } else if (XACML3.ELEMENT_REQUESTDEFAULTS.equals(childName)) {
                            result = result || DOMRequestDefaults.repair(child);
                        } else if (XACML3.ELEMENT_MULTIREQUESTS.equals(childName)) {
                            NodeList grandchildren = child.getChildNodes();
                            int numGrandchildren;
                            if (grandchildren != null && (numGrandchildren = grandchildren.getLength()) > 0) {
                                for (int j = 0; j < numGrandchildren; j++) {
                                    Node grandchild = grandchildren.item(j);
                                    if (DOMUtil.isElement(grandchild)) {
                                        if (DOMUtil.isInNamespace(grandchild, XACML3.XMLNS)) {
                                            if (XACML3.ELEMENT_REQUESTREFERENCE.equals(grandchild
                                                .getLocalName())) {
                                                result = DOMRequestReference.repair(grandchild) || result;
                                            } else {
                                                logger.warn("Unexpected element " + grandchild.getNodeName());
                                                child.removeChild(grandchild);
                                                result = true;
                                            }
                                        } else {
                                            logger.warn("Unexpected element " + grandchild.getNodeName());
                                            child.removeChild(grandchild);
                                            result = true;
                                        }
                                    }
                                }
                            }
                        } else {
                            logger.warn("Unexpected element " + child.getNodeName());
                            elementRequest.removeChild(child);
                            result = true;
                        }
                    } else {
                        logger.warn("Unexpected element " + child.getNodeName());
                        elementRequest.removeChild(child);
                        result = true;
                    }
                }
            }
        }
        if (!sawAttributes) {
            throw DOMUtil.newMissingElementException(nodeRequest, XACML3.XMLNS, XACML3.ELEMENT_ATTRIBUTES);
        }

        return result;
    }

    /**
     * Unit test program to load an XML file containing a XACML Request document.
     *
     * @param args the list of Request files to load and parse
     */
    public static void main(String[] args) {
        if (args.length > 0) {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setNamespaceAware(true);
            for (String xmlFileName : args) {
                File fileXml = new File(xmlFileName);
                if (!fileXml.exists()) {
                    System.err.println("Input file \"" + fileXml.getAbsolutePath() + "\" does not exist.");
                    continue;
                } else if (!fileXml.canRead()) {
                    System.err.println("Permission denied reading input file \"" + fileXml.getAbsolutePath()
                                       + "\"");
                    continue;
                }
                System.out.println(fileXml.getAbsolutePath() + ":");
                try {
                    DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
                    assert (documentBuilder.isNamespaceAware());
                    Document documentRequest = documentBuilder.parse(fileXml);
                    assert (documentRequest != null);

                    NodeList children = documentRequest.getChildNodes();
                    if (children == null || children.getLength() == 0) {
                        System.err.println("No Requests found in \"" + fileXml.getAbsolutePath() + "\"");
                        continue;
                    } else if (children.getLength() > 1) {
                        System.err
                            .println("Multiple Requests found in \"" + fileXml.getAbsolutePath() + "\"");
                    }
                    Node nodeRequest = children.item(0);
                    if (!nodeRequest.getLocalName().equals(XACML3.ELEMENT_REQUEST)) {
                        System.err.println("\"" + fileXml.getAbsolutePath() + "\" is not a Request");
                        continue;
                    }

                    Request domRequest = DOMRequest.newInstance(nodeRequest);
                    System.out.println(domRequest.toString());
                    System.out.println();
                } catch (Exception ex) {
                    ex.printStackTrace(System.err);
                }
            }
        }
    }

}
