/*
 * $Header$
 * $Revision$
 * $Date$
 * ------------------------------------------------------------------------------------------------------
 *
 * Copyright (c) SymphonySoft Limited. All rights reserved.
 * http://www.symphonysoft.com
 *
 * The software in this package is published under the terms of the BSD
 * style license a copy of which has been included with this distribution in
 * the LICENSE.txt file.
 */
package org.mule.providers.ejb;

import EDU.oswego.cs.dl.util.concurrent.SynchronizedBoolean;
import org.mule.impl.MuleMessage;
import org.mule.providers.AbstractMessageDispatcher;
import org.mule.providers.rmi.RmiConnector;
import org.mule.umo.UMOEvent;
import org.mule.umo.UMOException;
import org.mule.umo.UMOMessage;
import org.mule.umo.endpoint.UMOEndpointURI;
import org.mule.umo.provider.DispatchException;
import org.mule.umo.transformer.TransformerException;
import org.mule.util.ClassHelper;
import org.mule.util.PropertiesHelper;

import javax.ejb.EJBObject;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.util.ArrayList;
/*
 * Code by (c) 2005 P.Oikari.
 *
 * @author <a href="mailto:pnirvin@hotmail.com">P.Oikari</a>
 * @version $Revision$
 */
public class EjbMessageDispatcher extends AbstractMessageDispatcher
{
  private EjbConnector connector;

  private SynchronizedBoolean initialised = new SynchronizedBoolean(false);

  protected EJBObject remoteObject;

  protected Method invokedMethod;


  public EjbMessageDispatcher(EjbConnector connector)
  {
    super(connector);

    this.connector = connector;
  }

  protected void initialise(UMOEvent event) throws IOException, DispatchException, NotBoundException,
          NoSuchMethodException, ClassNotFoundException
  {
    if (!initialised.get())
    {
      String rmiPolicyPath = connector.getSecurityPolicy();
      String serverCodebasePath = connector.getServerCodebase();

      System.setProperty("java.security.policy", rmiPolicyPath);
      // System.setProperty("java.rmi.server.codebase",
      // serverCodebasePath);

      // Set security manager
      if (System.getSecurityManager() == null)
        System.setSecurityManager(new RMISecurityManager());

      remoteObject = getRemoteObject(event);

      invokedMethod = getMethodObject(event, remoteObject);

      initialised.set(true);
    }
  }

  private EJBObject getRemoteObject(UMOEvent event) throws RemoteException, UnknownHostException
  {
    EJBObject remoteObj = null;

    UMOEndpointURI endpointUri = event.getEndpoint().getEndpointURI();

    int port = endpointUri.getPort();

    if (port < 1)
    {
      port = RmiConnector.DEFAULT_RMI_REGISTRY_PORT;
    }

    InetAddress inetAddress = InetAddress.getByName(endpointUri.getHost());

    String serviceName = endpointUri.getPath();

    try
    {
      Object ref = connector.getJndiContext(inetAddress.getHostAddress() + ":" + port).lookup(serviceName);

      Method method = ClassHelper.getMethod("create", ref.getClass());

      remoteObj = (EJBObject) method.invoke(ref, ClassHelper.NO_ARGS);
    }
    catch (Exception e)
    {
      throw new RemoteException("Remote EJBObject lookup failed for '" + inetAddress.getHostAddress() + ":" + port + serviceName + "'", e);
    }

    return (remoteObj);
  }

  private Method getMethodObject(UMOEvent event, EJBObject remoteObject) throws DispatchException, NoSuchMethodException
  {
    UMOEndpointURI endpointUri = event.getEndpoint().getEndpointURI();

    String methodName = PropertiesHelper.getStringProperty(endpointUri.getParams(),
            RmiConnector.PARAM_SERVICE_METHOD,
            null);

    if (null == methodName)
    {
      methodName = (String) event.getEndpoint()
              .getProperties()
              .get(RmiConnector.PARAM_SERVICE_METHOD);

      if (null == methodName)
      {
        throw new DispatchException(new org.mule.config.i18n.Message("ejb",
                RmiConnector.MSG_PARAM_SERVICE_METHOD_NOT_SET),
                event.getMessage(),
                event.getEndpoint());
      }
    }


    // Parse method args
    try
    {
      String arguments = (String) event.getEndpoint()
              .getProperties()
              .get(RmiConnector.PROPERTY_SERVICE_METHOD_PARAM_TYPES);

      if (null != arguments)
      {
        ArrayList methodArgumentTypes = new ArrayList();

        String[] split = arguments.split(",");

      for (int i = 0; i < split.length; i++) {
          methodArgumentTypes.add(split[i]);

      }

        connector.setMethodArgumentTypes(methodArgumentTypes);
      }
    }
    catch (Exception e)
    {
      logger.warn("getMethodObject raised exception", e);
    }

    // Returns possible method
    return (remoteObject.getClass().getMethod(methodName, connector.getArgumentClasses()));
  }

  public UMOMessage receive(UMOEndpointURI endpointUri, long timeout) throws Exception
  {
    logger.debug("receive");

    return null;
  }

  public void doDispatch(UMOEvent event) throws Exception
  {
    logger.debug("doDispatch");

    initialise(event);

    Object[] arguments = getArgs(event);

    invokedMethod.invoke(remoteObject, arguments);
  }

  public UMOMessage doSend(UMOEvent event) throws IllegalAccessException, InvocationTargetException, Exception
  {
    logger.debug("doSend");

    initialise(event);

    Object[] arguments = getArgs(event);

    Object result = invokedMethod.invoke(remoteObject, arguments);

    return (null == result ? null
            : new MuleMessage(connector.getMessageAdapter(result).getPayload(), null));
  }

  public Object getDelegateSession() throws UMOException
  {
    return (null);
  }

  public void doDispose()
  {
  }

  private Object[] getArgs(UMOEvent event) throws TransformerException
  {
    Object payload = event.getTransformedMessage();

    return (payload instanceof Object[] ? (Object[]) payload : new Object[]{payload});
  }
}
