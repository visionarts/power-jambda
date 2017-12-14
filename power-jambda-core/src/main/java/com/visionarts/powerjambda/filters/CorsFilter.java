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

package com.visionarts.powerjambda.filters;

import com.google.common.net.HttpHeaders;
import com.visionarts.powerjambda.AwsProxyResponse;
import com.visionarts.powerjambda.cors.CorsConfiguration;

/**
 * This filter appends some HTTP headers about CORS configuration to {@code AwsProxyResponse} object.
 */
public class CorsFilter implements Filter<AwsProxyResponse> {

    private CorsConfiguration corsConfiguration;

    public CorsFilter(Class<? extends CorsConfiguration> corsConfigurationClazz) {
        try {
            this.corsConfiguration = corsConfigurationClazz.getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Error while instantiating CorsConfiguration class", e);
        }
    }

    @Override
    public void filter(AwsProxyResponse response) {
        corsConfiguration.getAllowOrigin()
            .ifPresent(o -> response.addHeaderIfAbsent(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, o));
        corsConfiguration.getAllowCredentials()
            .ifPresent(c -> response.addHeaderIfAbsent(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, c.toString()));
    }
}
