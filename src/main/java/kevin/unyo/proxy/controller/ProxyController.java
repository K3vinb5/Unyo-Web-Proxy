package kevin.unyo.proxy.controller;

import java.util.Enumeration;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/unyo")
public class ProxyController {

  private final RestTemplate restTemplate = new RestTemplate();

  // TODO the error is in the requestBody its format is not what the restTemplate
  // object is expecting, resulting in the api not recognizing the requestBody.
  @PostMapping("/proxy")
  public ResponseEntity<String> proxyRequest(@RequestBody String requestBody) {
    System.out.println("Hii" + requestBody);
    String externalApiUrl = "https://anilist.co/api/v2/oauth/token";
    HttpEntity<String> request = new HttpEntity<>(requestBody);
    ResponseEntity<String> response;
    try {
      response = restTemplate.postForEntity(externalApiUrl, request, String.class);
      return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
    } catch (Exception e) {
      response = restTemplate.postForEntity(externalApiUrl, request, String.class);
      return ResponseEntity.ok(response.getBody().toString());
    }
    // return ResponseEntity.ok(requestBody);
  }

  @RequestMapping("/getToken")
  public ResponseEntity<byte[]> forwardRequest(HttpServletRequest request, @RequestBody(required = false) byte[] body) {
    try {
      String forwardUri = "https://anilist.co/api/v2/oauth/token";
      // String requestUri = request.getRequestURI();
      // String queryString = request.getQueryString();
      // String forwardUri = targetUrl + requestUri + (queryString != null ? "?" +
      // queryString : "");

      HttpMethod method = HttpMethod.POST;
      HttpHeaders headers = new HttpHeaders();

      Enumeration<String> headerNames = request.getHeaderNames();
      while (headerNames.hasMoreElements()) {
        String headerName = headerNames.nextElement();
        headers.add(headerName, request.getHeader(headerName));
      }

      HttpEntity<byte[]> httpEntity = new HttpEntity<>(body, headers);
      ResponseEntity<byte[]> responseEntity = restTemplate.exchange(forwardUri, method, httpEntity, byte[].class);

      return new ResponseEntity<>(responseEntity.getBody(), responseEntity.getHeaders(),
          responseEntity.getStatusCode());
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage().getBytes());
    }
  }

  @GetMapping("/carol")
  public ResponseEntity<String> carolTest() {
    return ResponseEntity.ok("ok");
  }

}
