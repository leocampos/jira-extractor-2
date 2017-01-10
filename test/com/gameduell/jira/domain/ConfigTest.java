package com.gameduell.jira.domain;

import java.net.URI;
import java.net.URL;

import org.junit.Assert;
import org.junit.Test;

public class ConfigTest {
	@Test
	public void testConfigWithoutPropertyFileShouldLoadDefaults() {
		Config config = new Config();

		assertLoadDefaults(config);
	}

	@Test
	public void testConfigWithNotExistentPropertyFileShouldLoadDefaults() {
		Config config = new Config("NON Existent path");

		assertLoadDefaults(config);
	}

	@Test
	public void testConfigWithEmptyPropertyFileShouldLoadDefaults() {
		Config config = new Config(getAbsolutePath("emptyfile.properties"));

		assertLoadDefaults(config);
	}

	@Test
	public void testConfigWithPropertyFileShouldLoadFileValues() {
		Config config = new Config(getAbsolutePath("withValues.properties"));

		Assert.assertEquals("LOGIN", config.getLogin());
		Assert.assertEquals("PASSWORD", config.getPassword());
		Assert.assertEquals("MM/dd/yyyy HH:mm", config.getDateFormat());
		Assert.assertEquals(URI.create("JIRA-URL"), config.getJiraUri());
		Assert.assertEquals("10000", config.getMaxNumberOfItems().toString());
		Assert.assertEquals("500", config.getPageSize().toString());
	}

	private String getAbsolutePath(String fileName) {
		URL resource = getResource(fileName);
		if (resource == null)
			return null;

		return resource.getFile();
	}

	private URL getResource(String fileName) {
		return this.getClass().getResource("/" + fileName);
	}

	private void assertLoadDefaults(Config config) {
		Assert.assertNull(config.getLogin());
		Assert.assertNull(config.getPassword());
		Assert.assertEquals(Config.DEFAULT_TIMEFORMAT, config.getDateFormat());
		Assert.assertEquals(URI.create(Config.DEFAULT_JIRA_URL), config.getJiraUri());
		Assert.assertEquals(Config.DEFAULT_MAX_NUM_OF_ITEMS, config.getMaxNumberOfItems().toString());
		Assert.assertEquals(Config.DEFAULT_PAGE_SIZE, config.getPageSize().toString());
	}
}
