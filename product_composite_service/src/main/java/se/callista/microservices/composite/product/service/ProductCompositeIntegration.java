package se.callista.microservices.composite.product.service;

import java.io.IOException;
import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

import se.callista.microservises.core.product.model.Product;

/**
 * Created by magnus on 05/03/15.
 */
@Component
public class ProductCompositeIntegration {

    @Autowired
    private LoadBalancerClient loadBalancer;


    @Autowired
    private RestTemplate restTemplate;
    
    private ObjectReader productReader = null;

    // -------- //
    // PRODUCTS //
    // -------- //
    
    

    @HystrixCommand(fallbackMethod = "defaultProduct")
    public ResponseEntity<String> getProduct(int productId) {
    	ResponseEntity<String> resultStr=null;
    	try{
    	System.out.println("Will call getProduct with Hystrix protection");

        URI uri = getServiceUrl("product");
        System.out.println("URI: "+uri);

        String url = uri.toString() + "/product/" + productId;
        System.out.println("GetProduct from URL: "+ url);

        String str = restTemplate.getForObject(url, String.class);
        
        System.out.println("ResultStr : "+str);
        System.out.println("GetProduct http-status: {}"+ resultStr.getStatusCode());
        System.out.println("GetProduct body: {}"+ resultStr.getBody());

        //Product product = response2Product(resultStr);
        //System.out.println("GetProduct.id: {}"+ product.getProductId());

        System.out.println(resultStr);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
        
        return resultStr;
    }
    
    public ResponseEntity<String> defaultProduct(int productId) {
        System.out.println("Inside defaultProduct of ProductComposit...");
        return null;
    }
    
    public Product response2Product(ResponseEntity<String> response) {
        try {
            return getProductReader().readValue(response.getBody());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private ObjectReader getProductReader() {

        if (productReader != null) return productReader;

        ObjectMapper mapper = new ObjectMapper();
        return productReader = mapper.reader(Product.class);
    }
    
    public URI getServiceUrl(String serviceId) {
        return getServiceUrl(serviceId, null);
    }
    
    protected URI getServiceUrl(String serviceId, String fallbackUri) {
        URI uri = null;
        try {
        	
        	System.out.println("Inside getServiceUrl before LB choose method");
            ServiceInstance instance = loadBalancer.choose(serviceId);
            System.out.println("Inside getServiceUrl after LB choose method :"+instance);

            if (instance == null) {
                throw new RuntimeException("Can't find a service with serviceId = " + serviceId);
            }

            uri = instance.getUri();
            System.out.println("Resolved serviceId '{}' to URL '{}'., ServiceID :"+ serviceId +" URI: "+ uri);

        } catch (RuntimeException e) {
            // Eureka not available, use fallback if specified otherwise rethrow the error
            if (fallbackUri == null) {
                throw e;

            } else {
                uri = URI.create(fallbackUri);
                System.out.println("Failed to resolve serviceId '{}' to URL '{}'., ServiceID :"+ serviceId +" URI: "+ uri);
            }
        }

        return uri;
    }
}