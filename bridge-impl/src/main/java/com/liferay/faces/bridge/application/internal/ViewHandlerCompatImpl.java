/**
 * Copyright (c) 2000-2016 Liferay, Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.liferay.faces.bridge.application.internal;

import java.lang.reflect.Method;

import javax.faces.application.ViewHandler;
import javax.faces.application.ViewHandlerWrapper;
import javax.faces.context.FacesContext;

import com.liferay.faces.util.logging.Logger;
import com.liferay.faces.util.logging.LoggerFactory;
import com.liferay.faces.util.product.ProductConstants;
import com.liferay.faces.util.product.ProductMap;


/**
 * This class provides a compatibility layer that isolates differences between JSF1 and JSF2.
 *
 * @author  Neil Griffin
 */
public abstract class ViewHandlerCompatImpl extends ViewHandlerWrapper {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(ViewHandlerCompatImpl.class);

	// Public Constants
	public static final String RESPONSE_CHARACTER_ENCODING = "com.liferay.faces.bridge.responseCharacterEncoding";

	// Private Constants
	private static final boolean MOJARRA_DETECTED = ProductMap.getInstance().get(ProductConstants.JSF).getTitle()
		.equals(ProductConstants.MOJARRA);

	/**
	 * Mojarra 1.x does not have the ability to process faces-config navigation-rule entries with to-view-id containing
	 * EL-expressions. This method compensates for that shortcoming by evaluating the EL-expression that may be present
	 * in the specified viewId.
	 *
	 * @param   facesContext  The current FacesContext.
	 * @param   viewId        The viewId that may contain an EL expression.
	 *
	 * @return  If an EL-expression was present in the specified viewId, then returns the evaluated expression.
	 *          Otherwise, returns the specified viewId unchanged.
	 */
	protected String evaluateExpressionJSF1(FacesContext facesContext, String viewId) {

		// This method has overridden behavior for JSF 1 but simply returns the specified viewId for JSF 2
		return viewId;
	}

	protected ViewHandler getFacesRuntimeViewHandler() {

		ViewHandler viewHandler = getWrapped();

		while (viewHandler instanceof ViewHandlerWrapper) {
			ViewHandlerWrapper viewHandlerWrapper = (ViewHandlerWrapper) viewHandler;
			viewHandler = getWrappedViewHandler(viewHandlerWrapper);
		}

		return viewHandler;
	}

	protected ViewHandler getWrappedViewHandler(ViewHandlerWrapper viewHandlerWrapper) {

		ViewHandler wrappedViewHandler = null;

		try {
			Method getWrappedMethod = viewHandlerWrapper.getClass().getMethod("getWrapped");

			// ViewHandlerWrapper.getWrapped() is public in JSF 2.x but is protected in JSF 1.2
			getWrappedMethod.setAccessible(true);
			wrappedViewHandler = (ViewHandler) getWrappedMethod.invoke(viewHandlerWrapper);
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		return wrappedViewHandler;
	}
}
