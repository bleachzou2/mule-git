/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the MuleSource MPL
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.ide.prototype.palette;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.mule.ide.prototype.mulemodel.MuleFactory;

/*
 * The content provider class is responsible for
 * providing objects to the view. It can wrap
 * existing objects in adapters or simply return
 * objects as-is. These objects may be sensitive
 * to the current input of the view, or ignore
 * it and always show the same content 
 * (like Task List, for example).
 */

public class PaletteItem implements IAdaptable {
	private String name;
	private String type;
	private ImageDescriptor imageDescriptor;

	private FolderItem parent;

	public PaletteItem(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setParent(FolderItem parent) {
		this.parent = parent;
	}

	public FolderItem getParent() {
		return parent;
	}

	public String toString() {
		return getName();
	}

	public Object getAdapter(Class key) {
		return null;
	}

	public ImageDescriptor getImageDescriptor() {
		return imageDescriptor;
	}

	public void setImageDescriptor(ImageDescriptor image) {
		this.imageDescriptor = image;
	}
	
	public boolean mayDropOn(EObject obj) {
		return false;
	}
	
	public void performDropOn(EObject obj, MuleFactory factory) {
	}
}
