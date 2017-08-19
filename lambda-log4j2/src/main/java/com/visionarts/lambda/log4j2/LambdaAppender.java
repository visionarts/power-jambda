/*
 * Copyright 2017 the original author or authors.
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

package com.visionarts.lambda.log4j2;


import java.io.Serializable;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.LambdaRuntime;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;

/**
 * LambdaAppender is the custom log4j 2 appender
 * to be used in the log4j2.xml file.
 * You should not be required to use this class directly.
 *
 * You can use this appender like this:
 * <pre>
 * &lt;?xml version="1.0" encoding="UTF-8"?&gt;
 * &lt;Configuration status="WARN"&gt;
 *      &lt;Appenders&gt;
 *          &lt;Lambda name="Lambda"&gt;
 *              &lt;PatternLayout pattern="%d{HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n"/&gt;
 *          &lt;/Lambda&gt;
 *      &lt;/Appenders&gt;
 *      &lt;Loggers&gt;
 *          &lt;Root level="error"&gt;
 *              &lt;AppenderRef ref="Lambda"/&gt;
 *          &lt;/Root&gt;
 *      &lt;/Loggers&gt;
 * &lt;/Configuration&gt;
 * </pre>
 */
@Plugin(name = "Lambda", category = "Core", elementType = "appender", printObject = true)
public class LambdaAppender extends AbstractAppender {

    private LambdaLogger logger = LambdaRuntime.getLogger();

    private LambdaAppender(String name, Filter filter,
            Layout<? extends Serializable> layout, boolean ignoreExceptions) {
        super(name, filter, layout, ignoreExceptions);
    }

    public void append(LogEvent event) {
        logger.log(new String(getLayout().toByteArray(event)));
    }

    @PluginFactory
    public static LambdaAppender createAppender(@PluginAttribute("name") String name,
                                              @PluginAttribute("ignoreExceptions") boolean ignoreExceptions,
                                              @PluginElement("Layout") Layout<? extends Serializable> layout,
                                              @PluginElement("Filters") Filter filter) {
        if (name == null) {
            LOGGER.error("No name provided for LambdaAppender");
            return null;
        }

        if (layout == null) {
            layout = PatternLayout.createDefaultLayout();
        }
        return new LambdaAppender(name, filter, layout, ignoreExceptions);
    }

}
