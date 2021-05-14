package com.nhb.api.service;

import com.nhb.api.domain.ApiDetailMessage;
import com.nhb.api.domain.ApiSimpleMessage;

import java.util.List;

/**
 * @version 1.0
 * @description:
 * @author: luck_nhb
 * @date: 2021/4/28 18:27
 */
public interface IApiService {

    List<ApiSimpleMessage> getApiSimpleMessages();

    ApiDetailMessage getApiDetailMessages(String requestUrl);
}
