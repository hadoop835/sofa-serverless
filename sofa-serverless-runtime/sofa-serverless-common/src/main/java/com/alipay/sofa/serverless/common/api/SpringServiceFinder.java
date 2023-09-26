/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alipay.sofa.serverless.common.api;

import com.alipay.sofa.ark.api.ArkClient;
import com.alipay.sofa.ark.common.util.StringUtils;
import com.alipay.sofa.ark.spi.model.Biz;
import com.alipay.sofa.ark.spi.model.BizState;
import com.alipay.sofa.serverless.common.service.ServiceProxyFactory;
import sun.reflect.CallerSensitive;

import java.util.List;
import java.util.Map;

/**
 * @author: yuanyuan
 * @date: 2023/9/21 9:11 下午
 */
public class SpringServiceFinder {

    @CallerSensitive
    public static <T> T getBaseService(String name) {
        Biz masterBiz = ArkClient.getMasterBiz();
        return ServiceProxyFactory.createServiceProxy(masterBiz, name, null);
    }

    @CallerSensitive
    public static <T> T getBaseService(Class<T> serviceType) {
        Biz masterBiz = ArkClient.getMasterBiz();
        return ServiceProxyFactory.createServiceProxy(masterBiz, serviceType, null);
    }

    public static <T> Map<String, T> listBaseServices(Class<T> serviceType) {
        Biz masterBiz = ArkClient.getMasterBiz();
        return ServiceProxyFactory.batchCreateServiceProxy(masterBiz, serviceType,
            null);
    }

    @CallerSensitive
    public static <T> T getModuleService(String moduleName, String moduleVersion, String name) {
        Biz biz = determineMostSuitableBiz(moduleName, moduleVersion);
        return ServiceProxyFactory.createServiceProxy(biz, name, null);
    }

    @CallerSensitive
    public static <T> T getModuleService(String moduleName, String moduleVersion,
                                         Class<T> serviceType) {
        Biz biz = determineMostSuitableBiz(moduleName, moduleVersion);
        return ServiceProxyFactory.createServiceProxy(biz, serviceType, null);
    }

    public static <T> Map<String, T> listModuleServices(String moduleName, String moduleVersion,
                                                        Class<T> serviceType) {
        Biz biz = determineMostSuitableBiz(moduleName, moduleVersion);
        return ServiceProxyFactory.batchCreateServiceProxy(biz, serviceType, null);
    }

    public static Biz determineMostSuitableBiz(String moduleName, String moduleVersion) {
        Biz biz;
        if (StringUtils.isEmpty(moduleVersion)) {
            List<Biz> bizList = ArkClient.getBizManagerService().getBiz(moduleName);
            biz = bizList.stream().filter(it -> BizState.ACTIVATED == it.getBizState()).findFirst().get();
        } else {
            biz = ArkClient.getBizManagerService().getBiz(moduleName, moduleVersion);
        }
        return biz;
    }
}
