/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the MuleSource MPL
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.ide.prototype.translators;

import org.eclipse.wst.common.internal.emf.resource.GenericTranslator;
import org.eclipse.wst.common.internal.emf.resource.IDTranslator;
import org.eclipse.wst.common.internal.emf.resource.RootTranslator;
import org.eclipse.wst.common.internal.emf.resource.SourceLinkTranslator;
import org.eclipse.wst.common.internal.emf.resource.Translator;
import org.eclipse.wst.common.internal.emf.resource.TranslatorPath;
import org.mule.ide.prototype.mulemodel.MulePackage;

/**
 * The class <code>StrutsConfigTranslator</code> provides the root translator
 * that maps the StrutsConfig model to the Apache Struts configuration file.
 * 
 * @author Daniel
 */
public class MuleConfigTranslator extends RootTranslator {

	/**
	 * shared instance
	 */
	private static MuleConfigTranslator instance;

	/**
	 * Returns the shared instance of the translator.
	 * 
	 * @return shared instance
	 */
	public static MuleConfigTranslator getInstance() {
		if (instance == null) {
			instance = new MuleConfigTranslator();
		}
		return instance;
	}

	/**
	 * StrutsConfig package
	 */
	private static MulePackage MULE_CONFIG_PACKAGE = MulePackage.eINSTANCE;

	private static final TranslatorPath GLOBAL_ENDPOINT_TRANSLATOR_PATH = new TranslatorPath(new Translator[] {
					new Translator(MuleConfigXmlMapping.MULE_CONFIG, ROOT_FEATURE), 
					new Translator(MuleConfigXmlMapping.GLOBAL_ENDPOINTS_ENDPOINT, MULE_CONFIG_PACKAGE.getMuleConfig_GlobalEndpoints()), 
					new Translator(MuleConfigXmlMapping.ATTR_NAME, MULE_CONFIG_PACKAGE.getGlobalEndpoint_Name(), Translator.DOM_ATTRIBUTE) 
				});

	private static final SourceLinkTranslator OUTBOUND_ENDPOINT_SOURCE_LINK = new SourceLinkTranslator(MuleConfigXmlMapping.ENDPOINT_ADDRESS, MULE_CONFIG_PACKAGE.getOutboundRouter_OutboundEndpoint(), 
							GLOBAL_ENDPOINT_TRANSLATOR_PATH,Translator.DOM_ATTRIBUTE );

	private static final SourceLinkTranslator INBOUND_ENDPOINT_SOURCE_LINK = new SourceLinkTranslator(MuleConfigXmlMapping.ENDPOINT_ADDRESS, MULE_CONFIG_PACKAGE.getInboundRouter_InboundEndpoint(), 
			GLOBAL_ENDPOINT_TRANSLATOR_PATH, Translator.DOM_ATTRIBUTE );

	private static GenericTranslator outboundEndpointTranslator;

	/**
	 * mappings for version 1.0
	 */
	private Translator[] children10;

	/**
	 * Creates a new <code>StrutsConfigTranslator</code>.
	 */
	public MuleConfigTranslator() {
		super(MuleConfigXmlMapping.MULE_CONFIG, MULE_CONFIG_PACKAGE
				.getMuleConfig());
	}

	/*
	 * @see org.eclipse.wst.common.internal.emf.resource.Translator#getChildren(java.lang.Object,
	 *      int)
	 */
	public Translator[] getChildren(Object target, int versionID) {
		switch (versionID) {
		default:
			if (children10 == null) {
				children10 = create10Children();
			}
			return children10;
		}
	}

	/**
	 * Creates the translators that map the children of the 'mule-configuration'
	 * element to the structural features of the <code>MuleConfigType</code>
	 * class.
	 * <p>
	 * The translators are applicable for version 1.0.
	 * 
	 * @return translators
	 */
	protected Translator[] create10Children() {
		return new Translator[] {
				IDTranslator.INSTANCE,
				createGlobalEndpointsTranslator(MuleConfigConstants.VERSION_1_0_ID),
				createDescriptorTranslator(MuleConfigConstants.VERSION_1_0_ID)
				};
	}

	/**
	 * Creates a translator that maps the 'model/descriptor' element to the
	 * 'components' reference of the <code>MuleConfig</code> class.
	 * 
	 * @return translator
	 */
	protected static Translator createDescriptorTranslator(int versionID) {
		GenericTranslator translator = new GenericTranslator(
				MuleConfigXmlMapping.MODEL_MULE_DESCRIPTOR,
				MULE_CONFIG_PACKAGE
						.getMuleConfig_Components(), MULE_CONFIG_PACKAGE.getGenericComponent());
		translator.setChildren(new Translator[] {
				IDTranslator.INSTANCE,
				new Translator(MuleConfigXmlMapping.COMMENT,
						MULE_CONFIG_PACKAGE.getAbstractComponent_Comment(),
						Translator.COMMENT_FEATURE),
				new Translator(MuleConfigXmlMapping.ATTR_NAME,
						MULE_CONFIG_PACKAGE.getAbstractComponent_Name(),
						Translator.DOM_ATTRIBUTE | Translator.CDATA_CONTENT),
				createInboundEndpointTranslator(versionID),
				createOutboundEndpointTranslator(versionID) });
		return translator;
	}
	
	/**
	 * Creates a translator that maps an inbound router element to the
	 * 'inbound' router of a <code>Component</code>.
	 * 
	 * @return translator
	 */
	protected static Translator createInboundEndpointTranslator(int versionID) {
		GenericTranslator translator = new GenericTranslator(
				MuleConfigXmlMapping.INBOUND_ROUTER,
				MULE_CONFIG_PACKAGE
						.getAbstractComponent_InboundRouter());
		translator.setChildren(new Translator[] {
				IDTranslator.INSTANCE,
				INBOUND_ENDPOINT_SOURCE_LINK
//				,
//				new Translator(MuleConfigXmlMapping.ENDPOINT_ADDRESS,
//						MULE_CONFIG_PACKAGE.getOutboundRouterType_OutboundEndpoint(),
//						Translator.DOM_ATTRIBUTE | Translator.CDATA_CONTENT)
				});
		return translator;
	}
	
	/**
	 * Creates a translator that maps an inbound router element to the
	 * 'inbound' router of a <code>Component</code>.
	 * 
	 * @return translator
	 */
	protected static Translator createOutboundEndpointTranslator(int versionID) {
		if (outboundEndpointTranslator != null) {
			return outboundEndpointTranslator; 
		}
		outboundEndpointTranslator = new GenericTranslator(
				MuleConfigXmlMapping.OUTBOUND_ROUTER,
				MULE_CONFIG_PACKAGE
						.getAbstractComponent_OutboundRouter());
		outboundEndpointTranslator.setChildren(new Translator[] {
				IDTranslator.INSTANCE,
				OUTBOUND_ENDPOINT_SOURCE_LINK
//				,
//				new Translator(MuleConfigXmlMapping.ENDPOINT_ADDRESS,
//						MULE_CONFIG_PACKAGE.getOutboundRouterType_OutboundEndpoint(),
//						Translator.DOM_ATTRIBUTE | Translator.CDATA_CONTENT)
				});
		return outboundEndpointTranslator;
	}
	
	
	/**
	 * Creates a translator that maps the 'global-endpoints/endpoint' element to the
	 * 'globalEndpoints' reference of the <code>MuleConfig</code> class.
	 * 
	 * @return translator
	 */
	protected static Translator createGlobalEndpointsTranslator(int versionID) {
		GenericTranslator translator = new GenericTranslator(
				MuleConfigXmlMapping.GLOBAL_ENDPOINTS_ENDPOINT,
				MULE_CONFIG_PACKAGE
						.getMuleConfig_GlobalEndpoints(), OBJECT_MAP);
		translator.setChildren(new Translator[] {
				IDTranslator.INSTANCE,
				new Translator(MuleConfigXmlMapping.COMMENT,
						MULE_CONFIG_PACKAGE.getGlobalEndpoint_Comment(),
						Translator.COMMENT_FEATURE),
				new Translator(MuleConfigXmlMapping.ATTR_ADDRESS,
						MULE_CONFIG_PACKAGE.getEndpoint_Address(),
						Translator.DOM_ATTRIBUTE | Translator.CDATA_CONTENT),
				new Translator(MuleConfigXmlMapping.ATTR_NAME,
						MULE_CONFIG_PACKAGE.getGlobalEndpoint_Name(),
						Translator.DOM_ATTRIBUTE | Translator.CDATA_CONTENT) });
		return translator;
	}
}
