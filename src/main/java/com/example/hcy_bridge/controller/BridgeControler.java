package com.example.hcy_bridge.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.hcy_bridge.utils.HttpRequestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.*;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;

/**
 * @program xiaofeng_test
 * @description:
 * @author: xiaoFeng
 * @create: 2020/08/05 14:00
 */
@RestController
public class BridgeControler {

  /** 注册在eureka上的项目名称 */
  private final String PROJECT_NAME = "registerName";

  /** 项目前缀 */
  private final String PROJECT_NAME_CONTEXT_PATH = "/context/path";

  /** 项目启动端口号 */
  private final String DEFAULT_PORT = "8080";

  @Value("${server.port}")
  String port;

  @Autowired private DiscoveryClient discoveryClient;

  /**
   * 根据项目名获取在eureka上的注册信息
   *
   * @param applicationName 要查询的项目名称
   * @return 注册信息
   */
  @RequestMapping("/service-instances/{applicationName}")
  public String serviceInstancesByApplicationName(@PathVariable String applicationName) {
    List<ServiceInstance> instances = this.discoveryClient.getInstances(applicationName);
    return JSON.toJSONString(instances);
  }

  /**
   * get方式桥接
   *
   * @param request
   * @return 返回值
   * @throws IOException
   */
  @GetMapping(PROJECT_NAME_CONTEXT_PATH + "/**")
  public ResponseEntity getBridge(HttpServletRequest request) throws IOException {
    String url = getUrl(request);
    RestTemplate restTemplate = new RestTemplate();
    // create headers
    HttpHeaders headers = buildGetHeader(request);
    // build the request
    HttpEntity httpEntity = new HttpEntity(headers);
    // make an HTTP GET request with headers
    ResponseEntity responseEntity =
        restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);
    return responseEntity;
  }

  /**
   * post方式桥接接口
   *
   * @param request
   * @param jsonObject
   * @return 返回值
   */
  @PostMapping(PROJECT_NAME_CONTEXT_PATH + "/hms/hcySaas/**")
  public ResponseEntity postBridge(HttpServletRequest request, @RequestBody JSONObject jsonObject) {
    RestTemplate restTemplate = new RestTemplate();
    // request body parameters
    Map<String, Object> body = HttpRequestUtils.getBody(jsonObject);
    // 创建header
    HttpHeaders httpHeaders = buildPostHeader(request);
    // 组装请求数据
    HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, httpHeaders);
    // url
    String url = getUrl(request);
    // send POST request
    ResponseEntity responseEntity = restTemplate.postForEntity(url, entity, String.class);

    return responseEntity;
  }

  private ServiceInstance getInstance(String requestPort) {
    List<ServiceInstance> serviceInstances = this.discoveryClient.getInstances(PROJECT_NAME);
    if (CollectionUtils.isEmpty(serviceInstances)) {
      System.out.println("can't find ServiceInstance");
      return null;
    }
    ServiceInstance outData = null;
    for (ServiceInstance serviceInstance : serviceInstances) {
      if (serviceInstance.getPort() == Integer.parseInt(requestPort)) {
        outData = serviceInstance;
      }
    }
    return outData;
  }

  private String getUrl(HttpServletRequest request) {
    String requestPort = request.getParameter("requestPort");
    if (requestPort == null) {
      requestPort = DEFAULT_PORT;
    }
    ServiceInstance serviceInstance = getInstance(requestPort);
    if (serviceInstance == null) {
      System.out.println("can't find ServiceInstance2");
      return null;
    }
    return HttpRequestUtils.getUrl(request, serviceInstance);
  }

  private HttpHeaders buildPostHeader(HttpServletRequest request) {
    HttpHeaders headers = new HttpHeaders();
    // set `content-type` header
    headers.setContentType(MediaType.APPLICATION_JSON);
    // set `accept` header
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    Map<String, String> headersMap = HttpRequestUtils.getHeader(request);
    for (String key : headersMap.keySet()) {
      headers.set(key, headersMap.get(key));
    }

    return headers;
  }

  private HttpHeaders buildGetHeader(HttpServletRequest request) {
    HttpHeaders headers = new HttpHeaders();
    // set `Content-Type` and `Accept` headers
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    Map<String, String> headersMap = HttpRequestUtils.getHeader(request);
    for (String key : headersMap.keySet()) {
      headers.set(key, headersMap.get(key));
    }
    return headers;
  }
}
