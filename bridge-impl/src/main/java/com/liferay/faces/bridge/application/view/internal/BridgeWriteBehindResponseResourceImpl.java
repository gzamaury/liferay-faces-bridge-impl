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
package com.liferay.faces.bridge.application.view.internal;

import java.io.IOException;

import javax.portlet.ResourceResponse;
import javax.portlet.faces.Bridge;
import javax.portlet.faces.BridgeWriteBehindResponse;
import javax.portlet.filter.ResourceResponseWrapper;


/**
 * <p>Section 7.5.2 of the JSF specification requires that JSP-based views have the ability to interleave native HTML
 * markup with the markup generated by JSF component. But as described in Section 6.2.1 of the Portlet Bridge Spec,
 * there is no standard way of supporting this in the JSF API. However, the Portlet Bridge Spec however requires that
 * one part of the interleaving process be supported by the bridge, specifically {@link Bridge#AFTER_VIEW_CONTENT}. This
 * feature is responsible for rendering of markup that appears after the closing </f:view> component tag.</p>
 *
 * <p>The Portlet Bridge Spec requires that the {@link Bridge#AFTER_VIEW_CONTENT} feature be supported by implementing
 * the {@link BridgeWriteBehindResponse} interface. However with it is possible to have the JSF implementation (Mojarra
 * or MyFaces) handle the entire interleaving process by itself. So although this class implements the {@link
 * BridgeWriteBehindResponse} interface, it only does so for the sake of completeness. The corresponding methods
 * implemented in this class throw {@link UnsupportedOperationException} which proves that they are never called at
 * runtime.</p>
 *
 * @author  Neil Griffin
 */
public class BridgeWriteBehindResponseResourceImpl extends ResourceResponseWrapper
	implements BridgeWriteBehindResponse {

	public BridgeWriteBehindResponseResourceImpl(ResourceResponse resourceResponse) {
		super(resourceResponse);
	}

	@Override
	public void flushMarkupToWrappedResponse() throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean hasFacesWriteBehindMarkup() {
		throw new UnsupportedOperationException();
	}

	@Override
	public byte[] getBytes() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setCharacterEncoding(String charset) {
		throw new UnsupportedOperationException();
	}

	@Override
	public char[] getChars() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setContentLength(int len) {
		throw new UnsupportedOperationException();

	}

	@Override
	public boolean isBytes() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isChars() {
		throw new UnsupportedOperationException();
	}
}
