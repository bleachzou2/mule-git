/**
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the MuleSource MPL
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.ide.prototype.mulemodel.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;
import org.mule.ide.prototype.mulemodel.AbstractComponent;
import org.mule.ide.prototype.mulemodel.Connector;
import org.mule.ide.prototype.mulemodel.GlobalEndpoint;
import org.mule.ide.prototype.mulemodel.InterceptorDefinition;
import org.mule.ide.prototype.mulemodel.MuleConfig;
import org.mule.ide.prototype.mulemodel.MulePackage;
import org.mule.ide.prototype.mulemodel.Properties;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Config</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.mule.ide.prototype.mulemodel.impl.MuleConfigImpl#getComponents <em>Components</em>}</li>
 *   <li>{@link org.mule.ide.prototype.mulemodel.impl.MuleConfigImpl#getVersion <em>Version</em>}</li>
 *   <li>{@link org.mule.ide.prototype.mulemodel.impl.MuleConfigImpl#getDescription <em>Description</em>}</li>
 *   <li>{@link org.mule.ide.prototype.mulemodel.impl.MuleConfigImpl#getProperties <em>Properties</em>}</li>
 *   <li>{@link org.mule.ide.prototype.mulemodel.impl.MuleConfigImpl#getInterceptors <em>Interceptors</em>}</li>
 *   <li>{@link org.mule.ide.prototype.mulemodel.impl.MuleConfigImpl#getConnectors <em>Connectors</em>}</li>
 *   <li>{@link org.mule.ide.prototype.mulemodel.impl.MuleConfigImpl#getGlobalEndpoints <em>Global Endpoints</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class MuleConfigImpl extends EObjectImpl implements MuleConfig {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static final String copyright = "Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com"; //$NON-NLS-1$

	/**
	 * The cached value of the '{@link #getComponents() <em>Components</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getComponents()
	 * @generated
	 * @ordered
	 */
	protected EList components = null;

	/**
	 * The default value of the '{@link #getVersion() <em>Version</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getVersion()
	 * @generated
	 * @ordered
	 */
	protected static final String VERSION_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getVersion() <em>Version</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getVersion()
	 * @generated
	 * @ordered
	 */
	protected String version = VERSION_EDEFAULT;

	/**
	 * The default value of the '{@link #getDescription() <em>Description</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDescription()
	 * @generated
	 * @ordered
	 */
	protected static final String DESCRIPTION_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getDescription() <em>Description</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDescription()
	 * @generated
	 * @ordered
	 */
	protected String description = DESCRIPTION_EDEFAULT;

	/**
	 * The cached value of the '{@link #getProperties() <em>Properties</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getProperties()
	 * @generated
	 * @ordered
	 */
	protected Properties properties = null;

	/**
	 * The cached value of the '{@link #getInterceptors() <em>Interceptors</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getInterceptors()
	 * @generated
	 * @ordered
	 */
	protected InterceptorDefinition interceptors = null;

	/**
	 * The cached value of the '{@link #getConnectors() <em>Connectors</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getConnectors()
	 * @generated
	 * @ordered
	 */
	protected EList connectors = null;

	/**
	 * The cached value of the '{@link #getGlobalEndpoints() <em>Global Endpoints</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getGlobalEndpoints()
	 * @generated
	 * @ordered
	 */
	protected GlobalEndpoint globalEndpoints = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected MuleConfigImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected EClass eStaticClass() {
		return MulePackage.Literals.MULE_CONFIG;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public GlobalEndpoint getGlobalEndpoints() {
		return globalEndpoints;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetGlobalEndpoints(GlobalEndpoint newGlobalEndpoints, NotificationChain msgs) {
		GlobalEndpoint oldGlobalEndpoints = globalEndpoints;
		globalEndpoints = newGlobalEndpoints;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, MulePackage.MULE_CONFIG__GLOBAL_ENDPOINTS, oldGlobalEndpoints, newGlobalEndpoints);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setGlobalEndpoints(GlobalEndpoint newGlobalEndpoints) {
		if (newGlobalEndpoints != globalEndpoints) {
			NotificationChain msgs = null;
			if (globalEndpoints != null)
				msgs = ((InternalEObject)globalEndpoints).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - MulePackage.MULE_CONFIG__GLOBAL_ENDPOINTS, null, msgs);
			if (newGlobalEndpoints != null)
				msgs = ((InternalEObject)newGlobalEndpoints).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - MulePackage.MULE_CONFIG__GLOBAL_ENDPOINTS, null, msgs);
			msgs = basicSetGlobalEndpoints(newGlobalEndpoints, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MulePackage.MULE_CONFIG__GLOBAL_ENDPOINTS, newGlobalEndpoints, newGlobalEndpoints));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void addComponent() {
		// TODO: implement this method
		// Ensure that you remove @generated or mark it @generated NOT
		throw new UnsupportedOperationException();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList getComponents() {
		if (components == null) {
			components = new EObjectContainmentEList(AbstractComponent.class, this, MulePackage.MULE_CONFIG__COMPONENTS);
		}
		return components;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setVersion(String newVersion) {
		String oldVersion = version;
		version = newVersion;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MulePackage.MULE_CONFIG__VERSION, oldVersion, version));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setDescription(String newDescription) {
		String oldDescription = description;
		description = newDescription;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MulePackage.MULE_CONFIG__DESCRIPTION, oldDescription, description));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Properties getProperties() {
		return properties;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetProperties(Properties newProperties, NotificationChain msgs) {
		Properties oldProperties = properties;
		properties = newProperties;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, MulePackage.MULE_CONFIG__PROPERTIES, oldProperties, newProperties);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setProperties(Properties newProperties) {
		if (newProperties != properties) {
			NotificationChain msgs = null;
			if (properties != null)
				msgs = ((InternalEObject)properties).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - MulePackage.MULE_CONFIG__PROPERTIES, null, msgs);
			if (newProperties != null)
				msgs = ((InternalEObject)newProperties).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - MulePackage.MULE_CONFIG__PROPERTIES, null, msgs);
			msgs = basicSetProperties(newProperties, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MulePackage.MULE_CONFIG__PROPERTIES, newProperties, newProperties));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public InterceptorDefinition getInterceptors() {
		return interceptors;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetInterceptors(InterceptorDefinition newInterceptors, NotificationChain msgs) {
		InterceptorDefinition oldInterceptors = interceptors;
		interceptors = newInterceptors;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, MulePackage.MULE_CONFIG__INTERCEPTORS, oldInterceptors, newInterceptors);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setInterceptors(InterceptorDefinition newInterceptors) {
		if (newInterceptors != interceptors) {
			NotificationChain msgs = null;
			if (interceptors != null)
				msgs = ((InternalEObject)interceptors).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - MulePackage.MULE_CONFIG__INTERCEPTORS, null, msgs);
			if (newInterceptors != null)
				msgs = ((InternalEObject)newInterceptors).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - MulePackage.MULE_CONFIG__INTERCEPTORS, null, msgs);
			msgs = basicSetInterceptors(newInterceptors, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, MulePackage.MULE_CONFIG__INTERCEPTORS, newInterceptors, newInterceptors));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList getConnectors() {
		if (connectors == null) {
			connectors = new EObjectContainmentEList(Connector.class, this, MulePackage.MULE_CONFIG__CONNECTORS);
		}
		return connectors;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case MulePackage.MULE_CONFIG__COMPONENTS:
				return ((InternalEList)getComponents()).basicRemove(otherEnd, msgs);
			case MulePackage.MULE_CONFIG__PROPERTIES:
				return basicSetProperties(null, msgs);
			case MulePackage.MULE_CONFIG__INTERCEPTORS:
				return basicSetInterceptors(null, msgs);
			case MulePackage.MULE_CONFIG__CONNECTORS:
				return ((InternalEList)getConnectors()).basicRemove(otherEnd, msgs);
			case MulePackage.MULE_CONFIG__GLOBAL_ENDPOINTS:
				return basicSetGlobalEndpoints(null, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case MulePackage.MULE_CONFIG__COMPONENTS:
				return getComponents();
			case MulePackage.MULE_CONFIG__VERSION:
				return getVersion();
			case MulePackage.MULE_CONFIG__DESCRIPTION:
				return getDescription();
			case MulePackage.MULE_CONFIG__PROPERTIES:
				return getProperties();
			case MulePackage.MULE_CONFIG__INTERCEPTORS:
				return getInterceptors();
			case MulePackage.MULE_CONFIG__CONNECTORS:
				return getConnectors();
			case MulePackage.MULE_CONFIG__GLOBAL_ENDPOINTS:
				return getGlobalEndpoints();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case MulePackage.MULE_CONFIG__COMPONENTS:
				getComponents().clear();
				getComponents().addAll((Collection)newValue);
				return;
			case MulePackage.MULE_CONFIG__VERSION:
				setVersion((String)newValue);
				return;
			case MulePackage.MULE_CONFIG__DESCRIPTION:
				setDescription((String)newValue);
				return;
			case MulePackage.MULE_CONFIG__PROPERTIES:
				setProperties((Properties)newValue);
				return;
			case MulePackage.MULE_CONFIG__INTERCEPTORS:
				setInterceptors((InterceptorDefinition)newValue);
				return;
			case MulePackage.MULE_CONFIG__CONNECTORS:
				getConnectors().clear();
				getConnectors().addAll((Collection)newValue);
				return;
			case MulePackage.MULE_CONFIG__GLOBAL_ENDPOINTS:
				setGlobalEndpoints((GlobalEndpoint)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void eUnset(int featureID) {
		switch (featureID) {
			case MulePackage.MULE_CONFIG__COMPONENTS:
				getComponents().clear();
				return;
			case MulePackage.MULE_CONFIG__VERSION:
				setVersion(VERSION_EDEFAULT);
				return;
			case MulePackage.MULE_CONFIG__DESCRIPTION:
				setDescription(DESCRIPTION_EDEFAULT);
				return;
			case MulePackage.MULE_CONFIG__PROPERTIES:
				setProperties((Properties)null);
				return;
			case MulePackage.MULE_CONFIG__INTERCEPTORS:
				setInterceptors((InterceptorDefinition)null);
				return;
			case MulePackage.MULE_CONFIG__CONNECTORS:
				getConnectors().clear();
				return;
			case MulePackage.MULE_CONFIG__GLOBAL_ENDPOINTS:
				setGlobalEndpoints((GlobalEndpoint)null);
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case MulePackage.MULE_CONFIG__COMPONENTS:
				return components != null && !components.isEmpty();
			case MulePackage.MULE_CONFIG__VERSION:
				return VERSION_EDEFAULT == null ? version != null : !VERSION_EDEFAULT.equals(version);
			case MulePackage.MULE_CONFIG__DESCRIPTION:
				return DESCRIPTION_EDEFAULT == null ? description != null : !DESCRIPTION_EDEFAULT.equals(description);
			case MulePackage.MULE_CONFIG__PROPERTIES:
				return properties != null;
			case MulePackage.MULE_CONFIG__INTERCEPTORS:
				return interceptors != null;
			case MulePackage.MULE_CONFIG__CONNECTORS:
				return connectors != null && !connectors.isEmpty();
			case MulePackage.MULE_CONFIG__GLOBAL_ENDPOINTS:
				return globalEndpoints != null;
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (version: "); //$NON-NLS-1$
		result.append(version);
		result.append(", description: "); //$NON-NLS-1$
		result.append(description);
		result.append(')');
		return result.toString();
	}

} //MuleConfigImpl