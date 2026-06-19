package com.rione.social;

import static io.cucumber.junit.platform.engine.Constants.FILTER_TAGS_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

@Suite
@IncludeEngines("cucumber")
@SelectPackages("features.social")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "com.rione.social")
@ConfigurationParameter(key = FILTER_TAGS_PROPERTY_NAME, value = "not @pending")
class SocialAcceptanceTest {
}
