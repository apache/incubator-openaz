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

//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2013.05.23 at 02:47:00 PM EDT
//

package oasis.names.tc.xacml._3_0.core.schema.wd_17;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for ConditionType complex type.
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="ConditionType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{urn:oasis:names:tc:xacml:3.0:core:schema:wd-17}Expression"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ConditionType", propOrder = {
                                              "expression"
})
public class ConditionType {

    @XmlElementRef(name = "Expression", namespace = "urn:oasis:names:tc:xacml:3.0:core:schema:wd-17", type = JAXBElement.class)
    protected JAXBElement<?> expression;

    /**
     * Gets the value of the expression property.
     *
     * @return possible object is {@link javax.xml.bind.JAXBElement }{@code <}
     *         {@link oasis.names.tc.xacml._3_0.core.schema.wd_17.FunctionType }{@code >}
     *         {@link javax.xml.bind.JAXBElement }{@code <}{@link AttributeDesignatorType }{@code >}
     *         {@link javax.xml.bind.JAXBElement }{@code <}{@link VariableReferenceType }{@code >}
     *         {@link javax.xml.bind.JAXBElement }{@code <}{@link ExpressionType }{@code >}
     *         {@link javax.xml.bind.JAXBElement }{@code <}{@link AttributeSelectorType }{@code >}
     *         {@link javax.xml.bind.JAXBElement }{@code <}
     *         {@link oasis.names.tc.xacml._3_0.core.schema.wd_17.ApplyType }{@code >}
     *         {@link javax.xml.bind.JAXBElement }{@code <}{@link AttributeValueType }{@code >}
     */
    public JAXBElement<?> getExpression() {
        return expression;
    }

    /**
     * Sets the value of the expression property.
     *
     * @param value allowed object is {@link javax.xml.bind.JAXBElement }{@code <}
     *            {@link oasis.names.tc.xacml._3_0.core.schema.wd_17.FunctionType }{@code >}
     *            {@link javax.xml.bind.JAXBElement }{@code <}{@link AttributeDesignatorType }{@code >}
     *            {@link javax.xml.bind.JAXBElement }{@code <}{@link VariableReferenceType }{@code >}
     *            {@link javax.xml.bind.JAXBElement }{@code <}{@link ExpressionType }{@code >}
     *            {@link javax.xml.bind.JAXBElement }{@code <}{@link AttributeSelectorType }{@code >}
     *            {@link javax.xml.bind.JAXBElement }{@code <}
     *            {@link oasis.names.tc.xacml._3_0.core.schema.wd_17.ApplyType }{@code >}
     *            {@link javax.xml.bind.JAXBElement }{@code <}{@link AttributeValueType }{@code >}
     */
    public void setExpression(JAXBElement<?> value) {
        this.expression = (value);
    }

}
