package com.example.hcy_bridge.utils;

import com.alibaba.fastjson.JSONObject;
import org.springframework.cloud.client.ServiceInstance;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * @program xiaofeng_test
 * @description:
 * @author: xiaoFeng
 * @create: 2020/08/06 11:01
 */
public class HttpRequestUtils {
  public static final String URI_INDEX = "/hms/hcyBridge";

  /**
   * 获取url请求参数
   *
   * @param request HttpServletRequest
   * @return 映射
   */
  public static Map getParams(HttpServletRequest request) {
    Map map = new HashMap();
    Enumeration paramNames = request.getParameterNames();
    while (paramNames.hasMoreElements()) {
      String paramName = (String) paramNames.nextElement();
      String[] paramValues = request.getParameterValues(paramName);
      if (paramValues.length == 1) {
        String paramValue = paramValues[0];
        if (paramValue.length() != 0) {
          map.put(paramName, paramValue);
        }
      }
    }
    return map;
  }

  /**
   * 获取body参数
   *
   * @param jsonObject json数据
   * @return 映射
   */
  public static Map<String, Object> getBody(JSONObject jsonObject) {
    Map<String, Object> map = new HashMap();
    for (String key : jsonObject.keySet()) {
      map.put(key, jsonObject.get(key));
    }
    return map;
  }

  /**
   * 获取header参数
   *
   * @param request HttpServletRequest
   * @return 映射
   */
  public static Map<String, String> getHeader(HttpServletRequest request) {
    Map<String, String> map = new HashMap();
    Enumeration<String> e = request.getHeaderNames();
    while (e.hasMoreElements()) {
      String headerName = e.nextElement();
      map.put(headerName, request.getHeader(headerName));
    }
    return map;
  }

  public static String getUrl(HttpServletRequest request, ServiceInstance serviceInstance) {
    String requestPort = request.getParameter("requestPort");
    String requestURI = request.getRequestURI();
    String url = requestURI.substring(URI_INDEX.length());
    String urlParam = request.getQueryString();
    return serviceInstance.getScheme()
        + "://"
        + serviceInstance.getHost()
        + ":"
        + requestPort
        + url
        + "?"
        + urlParam;
  }
}
