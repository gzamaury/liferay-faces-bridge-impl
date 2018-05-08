/**
 * Copyright (c) 2000-2018 Liferay, Inc. All rights reserved.
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
package com.liferay.faces.bridge.test.integration.demo.applicant;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;

import org.junit.runners.MethodSorters;

import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.faces.bridge.test.integration.BridgeTestUtil;
import com.liferay.faces.test.selenium.browser.BrowserDriver;
import com.liferay.faces.test.selenium.browser.BrowserDriverManagingTesterBase;
import com.liferay.faces.test.selenium.browser.TestUtil;
import com.liferay.faces.test.selenium.browser.WaitingAsserter;


/**
 * @author  Kyle Stiemann
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public abstract class ApplicantTesterBase extends BrowserDriverManagingTesterBase {

	// Logger
	private static final Logger logger = LoggerFactory.getLogger(ApplicantTesterBase.class);

	// Private Constants
	protected static final String LIFERAY_JSF_JERSEY_PNG_FILE_PATH = TestUtil.JAVA_IO_TMPDIR + "liferay-jsf-jersey.png";

	@Test
	public void runApplicantPortletTest_A_ApplicantViewRendered() throws Exception {

		BrowserDriver browserDriver = getBrowserDriver();
		navigateWindowToDemo(browserDriver);

		WaitingAsserter waitingAsserter = getWaitingAsserter();
		waitingAsserter.assertElementDisplayed(getFirstNameFieldXpath());
		waitingAsserter.assertElementDisplayed(getLastNameFieldXpath());
		waitingAsserter.assertElementDisplayed(getEmailAddressFieldXpath());
		waitingAsserter.assertElementDisplayed(getPhoneNumberFieldXpath());
		waitingAsserter.assertElementDisplayed(getDateOfBirthFieldXpath());
		waitingAsserter.assertElementDisplayed(getCityFieldXpath());
		waitingAsserter.assertElementDisplayed(getProvinceIdFieldXpath());
		waitingAsserter.assertElementDisplayed(getPostalCodeFieldXpath());
		waitingAsserter.assertElementDisplayed(getShowHideCommentsLinkXpath());
		assertFileUploadChooserDisplayed(browserDriver, waitingAsserter);

		String facesImplName = System.getProperty("faces.impl.name");
		assertLibraryElementDisplayed(waitingAsserter, facesImplName, browserDriver);

		if (isLiferayFacesAlloyIncluded()) {
			assertLibraryElementDisplayed(waitingAsserter, "Liferay Faces Alloy", browserDriver);
		}

		assertLibraryElementDisplayed(waitingAsserter, "Liferay Faces Bridge Impl", browserDriver);

		if (TestUtil.getContainer().contains("liferay")) {
			assertLibraryElementDisplayed(waitingAsserter, "Liferay Faces Bridge Ext", browserDriver);
		}

		String extraLibraryName = getExtraLibraryName();

		if (extraLibraryName != null) {
			assertLibraryElementDisplayed(waitingAsserter, extraLibraryName, browserDriver);
		}
	}

	@Test
	public void runApplicantPortletTest_B_EditMode() {

		// Test that changing the date pattern via preferences changes the Birthday value in the portlet.
		BrowserDriver browserDriver = getBrowserDriver();
		browserDriver.clickElement(getEditModeXpath());

		String datePatternPreferencesXpath = getDatePatternPreferencesXpath();

		try {
			browserDriver.waitForElementEnabled(datePatternPreferencesXpath);
		}
		catch (TimeoutException e) {

			resetBrowser();
			throw (e);
		}

		browserDriver.clearElement(datePatternPreferencesXpath);

		String newDatePattern = "MM/dd/yy";
		browserDriver.sendKeysToElement(datePatternPreferencesXpath, newDatePattern);

		String preferencesSubmitButtonXpath = getPreferencesSubmitButtonXpath();
		browserDriver.clickElement(preferencesSubmitButtonXpath);

		String dateOfBirthFieldXpath = getDateOfBirthFieldXpath();

		try {
			browserDriver.waitForElementEnabled(dateOfBirthFieldXpath);
		}
		catch (TimeoutException e) {

			resetBrowser();
			throw (e);
		}

		Date today = new Date();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(newDatePattern);
		TimeZone gmtTimeZone = TimeZone.getTimeZone("Greenwich");
		simpleDateFormat.setTimeZone(gmtTimeZone);

		String todayString = simpleDateFormat.format(today);
		WaitingAsserter waitingAsserter = getWaitingAsserter();
		waitingAsserter.assertTextPresentInElementValue(todayString, dateOfBirthFieldXpath);

		// Test that resetting the date pattern via preferences changes the Birthday year back to the long version.
		browserDriver.clickElement(getEditModeXpath());

		try {
			browserDriver.waitForElementEnabled(datePatternPreferencesXpath);
		}
		catch (TimeoutException e) {

			resetBrowser();
			throw (e);
		}

		String preferencesResetButtonXpath = getPreferencesResetButtonXpath();
		browserDriver.clickElement(preferencesResetButtonXpath);

		try {
			browserDriver.waitForElementEnabled(dateOfBirthFieldXpath);
		}
		catch (TimeoutException e) {

			resetBrowser();
			throw (e);
		}

		String oldDatePattern = "MM/dd/yyyy";
		simpleDateFormat.applyPattern(oldDatePattern);
		todayString = simpleDateFormat.format(today);
		waitingAsserter.assertTextPresentInElementValue(todayString, dateOfBirthFieldXpath);
	}

	@Test
	public void runApplicantPortletTest_C_FirstNameField() {

		BrowserDriver browserDriver = getBrowserDriver();
		String firstNameFieldXpath = getFirstNameFieldXpath();
		browserDriver.createActions().sendKeys(Keys.TAB).perform();
		browserDriver.sendKeysToElement(firstNameFieldXpath, "asdf");

		String lastNameFieldXpath = getLastNameFieldXpath();
		Action lastNameFieldClick = browserDriver.createClickElementAction(lastNameFieldXpath);
		browserDriver.performAndWaitForRerender(lastNameFieldClick, firstNameFieldXpath);

		String firstNameFieldErrorXpath = getFieldErrorXpath(firstNameFieldXpath);
		WaitingAsserter waitingAsserter = getWaitingAsserter();
		waitingAsserter.assertElementNotDisplayed(firstNameFieldErrorXpath);
		browserDriver.clearElement(firstNameFieldXpath);
		browserDriver.performAndWaitForRerender(lastNameFieldClick, firstNameFieldXpath);
		waitingAsserter.assertTextPresentInElement("Value is required", firstNameFieldErrorXpath);
	}

	@Test
	public void runApplicantPortletTest_D_EmailValidation() {

		BrowserDriver browserDriver = getBrowserDriver();
		String emailAddressFieldXpath = getEmailAddressFieldXpath();
		browserDriver.centerElementInCurrentWindow(emailAddressFieldXpath);
		sendKeysTabAndWaitForRerender(browserDriver, emailAddressFieldXpath, "test");

		String emailAddressFieldErrorXpath = getFieldErrorXpath(emailAddressFieldXpath);
		WaitingAsserter waitingAsserter = getWaitingAsserter();
		waitingAsserter.assertTextPresentInElement("Invalid e-mail address", emailAddressFieldErrorXpath);
		sendKeysTabAndWaitForRerender(browserDriver, emailAddressFieldXpath, "@liferay.com");
		waitingAsserter.assertElementNotDisplayed(emailAddressFieldErrorXpath);
	}

	@Test
	public void runApplicantPortletTest_E_AllFieldsRequired() {

		BrowserDriver browserDriver = getBrowserDriver();
		clearAllFields(browserDriver);
		browserDriver.clickElementAndWaitForRerender(getSubmitButtonXpath());

		WaitingAsserter waitingAsserter = getWaitingAsserter();
		waitingAsserter.assertTextPresentInElement("Value is required", getFieldErrorXpath(getFirstNameFieldXpath()));
		waitingAsserter.assertTextPresentInElement("Value is required", getFieldErrorXpath(getLastNameFieldXpath()));
		waitingAsserter.assertTextPresentInElement("Value is required",
			getFieldErrorXpath(getEmailAddressFieldXpath()));
		waitingAsserter.assertTextPresentInElement("Value is required", getFieldErrorXpath(getPhoneNumberFieldXpath()));
		waitingAsserter.assertTextPresentInElement("Value is required", getFieldErrorXpath(getDateOfBirthFieldXpath()));
		waitingAsserter.assertTextPresentInElement("Value is required", getFieldErrorXpath(getCityFieldXpath()));
		waitingAsserter.assertTextPresentInElement("Value is required", getFieldErrorXpath(getProvinceIdFieldXpath()));
		waitingAsserter.assertTextPresentInElement("Value is required", getFieldErrorXpath(getPostalCodeFieldXpath()));
	}

	@Test
	public void runApplicantPortletTest_F_AutoPopulateCityState() {

		BrowserDriver browserDriver = getBrowserDriver();
		browserDriver.centerElementInCurrentWindow(getPostalCodeFieldXpath());
		sendKeysTabAndWaitForRerender(browserDriver, getPostalCodeFieldXpath(), "32801");

		WaitingAsserter waitingAsserter = getWaitingAsserter();
		waitingAsserter.assertTextPresentInElementValue("Orlando", getCityFieldXpath());
		waitingAsserter.assertTextPresentInElementValue("3", getProvinceIdFieldXpath());
	}

	@Test
	public void runApplicantPortletTest_G_Comments() {

		BrowserDriver browserDriver = getBrowserDriver();
		String showHideCommentsLinkXpath = getShowHideCommentsLinkXpath();
		browserDriver.clickElementAndWaitForRerender(showHideCommentsLinkXpath);

		String commentsXpath = getCommentsXpath();
		browserDriver.sendKeysToElement(commentsXpath, "testing 1, 2, 3");
		browserDriver.clickElementAndWaitForRerender(showHideCommentsLinkXpath);
		browserDriver.clickElementAndWaitForRerender(showHideCommentsLinkXpath);
		getWaitingAsserter().assertTextPresentInElement("testing 1, 2, 3", commentsXpath);
	}

	@Test
	public void runApplicantPortletTest_H_DateValidation() {

		BrowserDriver browserDriver = getBrowserDriver();
		String dateOfBirthFieldXpath = getDateOfBirthFieldXpath();
		browserDriver.centerElementInCurrentWindow(dateOfBirthFieldXpath);
		browserDriver.clearElement(dateOfBirthFieldXpath);
		sendKeysTabAndWaitForRerender(browserDriver, dateOfBirthFieldXpath, "12/34/5678");

		String dateOfBirthFieldErrorXpath = getFieldErrorXpath(dateOfBirthFieldXpath);
		WaitingAsserter waitingAsserter = getWaitingAsserter();
		waitingAsserter.assertTextPresentInElement("Invalid date format", dateOfBirthFieldErrorXpath);
		browserDriver.clearElement(dateOfBirthFieldXpath);
		sendKeysTabAndWaitForRerender(browserDriver, dateOfBirthFieldXpath, "01/02/3456");
		waitingAsserter.assertElementNotDisplayed(dateOfBirthFieldErrorXpath);
	}

	@Test
	public void runApplicantPortletTest_I_FileUpload() {

		BrowserDriver browserDriver = getBrowserDriver();
		String fileUploadChooserXpath = getFileUploadChooserXpath();
		WebElement fileUploadChooser = browserDriver.findElementByXpath(fileUploadChooserXpath);

		// Set PrimeFaces p:fileUpload transform style to "none" since it causes the element to not be displayed
		// according to Selenium (although the element is visible to users).
		browserDriver.executeScriptInCurrentWindow("arguments[0].style.transform = 'none';", fileUploadChooser);

		// Workaround https://github.com/ariya/phantomjs/issues/10993 by removing the multiple attribute from <input
		// type="file" />
		if (browserDriver.getBrowserName().equals("phantomjs")) {

			browserDriver.executeScriptInCurrentWindow(
				"var multipleFileUploadElements = document.querySelectorAll('input[type=\"file\"][multiple]');" +
				"for (var i = 0; i < multipleFileUploadElements.length; i++) {" +
				"multipleFileUploadElements[i].removeAttribute('multiple'); }");
		}

		fileUploadChooser.sendKeys(LIFERAY_JSF_JERSEY_PNG_FILE_PATH);
		submitFile(browserDriver);
		getWaitingAsserter().assertTextPresentInElement("jersey", getUploadedFileXpath());
	}

	@Test
	public void runApplicantPortletTest_J_Submit() {

		BrowserDriver browserDriver = getBrowserDriver();
		clearAllFields(browserDriver);
		browserDriver.clearElement(getCommentsXpath());

		String firstNameFieldXpath = getFirstNameFieldXpath();
		browserDriver.waitForElementEnabled(firstNameFieldXpath);
		browserDriver.sendKeysToElement(firstNameFieldXpath, "David");
		browserDriver.sendKeysToElement(getLastNameFieldXpath(), "Samuel");
		browserDriver.sendKeysToElement(getEmailAddressFieldXpath(), "no_need@just.pray");
		browserDriver.sendKeysToElement(getPhoneNumberFieldXpath(), "(way) too-good");
		selectDate(browserDriver);
		browserDriver.sendKeysToElement(getCityFieldXpath(), "North Orlando");
		selectProvince(browserDriver);
		browserDriver.sendKeysToElement(getPostalCodeFieldXpath(), "32802");

		String genesis11 =
			"Indeed the people are one and they all have one language, and this is what they begin to do ...";
		browserDriver.sendKeysToElement(getCommentsXpath(), genesis11);
		browserDriver.waitForElementNotDisplayed(getFieldErrorXpath("//*"));
		browserDriver.clickElement(getSubmitButtonXpath());
		getWaitingAsserter().assertTextPresentInElement("Thank you David", getConfimationFormXpath());
	}

	@Before
	public void setUpApplicantTester() {
		getBrowserDriver().setWaitTimeOut(TestUtil.getBrowserDriverWaitTimeOut() * 2);
	}

	@After
	public void tearDownApplicantTester() {
		getBrowserDriver().setWaitTimeOut(TestUtil.getBrowserDriverWaitTimeOut());
	}

	protected abstract String getPortletPageName();

	protected void assertFileUploadChooserDisplayed(BrowserDriver browserDriver, WaitingAsserter waitingAsserter) {
		waitingAsserter.assertElementDisplayed(getFileUploadChooserXpath());
	}

	protected void assertLibraryElementDisplayed(WaitingAsserter waitingAsserter, String libraryName,
		BrowserDriver browserDriver) {

		String libraryVersionXpath = "//li[contains(.,'" + libraryName + "')]";
		waitingAsserter.assertElementDisplayed(libraryVersionXpath);

		if (logger.isInfoEnabled()) {

			WebElement libraryVersionElement = browserDriver.findElementByXpath(libraryVersionXpath);
			logger.info(libraryVersionElement.getText());
		}
	}

	protected void clearAllFields(BrowserDriver browserDriver) {

		browserDriver.clearElement(getFirstNameFieldXpath());
		browserDriver.clearElement(getLastNameFieldXpath());
		browserDriver.clearElement(getEmailAddressFieldXpath());
		browserDriver.clearElement(getPhoneNumberFieldXpath());
		browserDriver.clearElement(getDateOfBirthFieldXpath());
		browserDriver.clearElement(getCityFieldXpath());
		clearProvince(browserDriver);
		browserDriver.clearElement(getPostalCodeFieldXpath());
	}

	protected void clearProvince(BrowserDriver browserDriver) {
		createSelect(browserDriver, getProvinceIdFieldXpath()).selectByVisibleText("Select");
	}

	protected final Select createSelect(BrowserDriver browserDriver, String selectXpath) {

		WebElement selectField = browserDriver.findElementByXpath(selectXpath);

		return new Select(selectField);
	}

	protected String getCityFieldXpath() {
		return "//input[contains(@id,':city')]";
	}

	protected String getCommentsXpath() {
		return "//textarea[contains(@id,':comments')]";
	}

	protected String getConfimationFormXpath() {
		return "//form[@method='post']";
	}

	protected String getDateOfBirthFieldXpath() {
		return "//input[contains(@id,':dateOfBirth')]";
	}

	protected String getDatePatternPreferencesXpath() {
		return "//input[contains(@id,':datePattern')]";
	}

	protected String getEditModeXpath() {
		return "//a[contains(@id,'editLink')]";
	}

	protected String getEmailAddressFieldXpath() {
		return "//input[contains(@id,':emailAddress')]";
	}

	protected String getExtraLibraryName() {
		return null;
	}

	protected String getFieldErrorXpath(String fieldXpath) {
		return fieldXpath + "/../span[@class='portlet-msg-error']";
	}

	protected String getFileUploadChooserXpath() {
		return "//input[@type='file']";
	}

	protected String getFirstNameFieldXpath() {
		return "//input[contains(@id,':firstName')]";
	}

	protected String getLastNameFieldXpath() {
		return "//input[contains(@id,':lastName')]";
	}

	protected String getLogoXpath() {
		return "//img[contains(@src, 'liferay-logo.png')]";
	}

	protected String getPhoneNumberFieldXpath() {
		return "//input[contains(@id,':phoneNumber')]";
	}

	protected String getPostalCodeFieldXpath() {
		return "//input[contains(@id,':postalCode')]";
	}

	protected String getPostalCodeToolTipXpath() {
		return "//img[contains(@title, 'Type any of these ZIP codes')]";
	}

	protected String getPreferencesResetButtonXpath() {
		return "//input[@type='submit'][@value='Reset']";
	}

	protected String getPreferencesSubmitButtonXpath() {
		return "//input[@type='submit'][@value='Submit']";
	}

	protected String getProvinceIdFieldXpath() {
		return "//select[contains(@id,':provinceId')]";
	}

	protected String getShowHideCommentsLinkXpath() {
		return "//a[contains(text(), 'Show Comments') or contains(text(), 'Hide Comments')]";
	}

	protected String getSubmitAnotherApplicationButton() {
		return "//input[@type='submit'][contains(@value, 'Submit Another Application')]";
	}

	protected String getSubmitButtonXpath() {
		return "//input[@type='submit'][@value='Submit']";
	}

	protected String getSubmitFileButtonXpath() {
		return "//form[@method='post'][@enctype='multipart/form-data']/input[@type='submit'][@value='Submit']";
	}

	protected String getUploadedFileXpath() {
		return "//tr[@class='portlet-section-body results-row']/td[2]";
	}

	protected boolean isLiferayFacesAlloyIncluded() {
		return true;
	}

	protected void navigateWindowToDemo(BrowserDriver browserDriver) {

		String demoContext = BridgeTestUtil.getDemoContext(getPortletPageName());
		browserDriver.navigateWindowTo(TestUtil.DEFAULT_BASE_URL + demoContext);
	}

	protected void resetBrowser() {

		// Reset everything in case there was an error.
		BrowserDriver browserDriver = getBrowserDriver();
		browserDriver.clearBrowserCookies();
		TestUtil.signIn(browserDriver);
		navigateWindowToDemo(browserDriver);
	}

	protected void selectDate(BrowserDriver browserDriver) {
		browserDriver.sendKeysToElement(getDateOfBirthFieldXpath(), "01/02/3456");
	}

	protected void selectProvince(BrowserDriver browserDriver) {
		createSelect(browserDriver, getProvinceIdFieldXpath()).selectByVisibleText("FL");
	}

	protected final void sendKeysTabAndWaitForRerender(BrowserDriver browserDriver, String elementXpath,
		CharSequence... keys) {

		Actions actions = browserDriver.createActions(elementXpath);
		WebElement element = browserDriver.findElementByXpath(elementXpath);
		actions.sendKeys(element, keys);
		actions.sendKeys(Keys.TAB);
		browserDriver.performAndWaitForRerender(actions.build(), elementXpath);
	}

	protected void submitFile(BrowserDriver browserDriver) {

		browserDriver.clickElement(getSubmitFileButtonXpath());
		browserDriver.waitForElementDisplayed(getUploadedFileXpath());
	}
}
