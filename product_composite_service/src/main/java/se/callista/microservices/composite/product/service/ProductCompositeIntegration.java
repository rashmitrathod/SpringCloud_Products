package se.callista.microservices.composite.product.service;


import java.io.IOException;
import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

import se.callista.microservises.core.product.model.Product;
import se.callista.microservises.core.recommendation.model.Recommendation;
import se.callista.microservises.core.review.model.Review;

/**
 * Created by magnus on 05/03/15.
 */
@Component
public class ProductCompositeIntegration {

	@Autowired
	private LoadBalancerClient loadBalancer;

	private RestTemplate restTemplate;

	private ObjectReader productReader = null;

	// -------- //
	// PRODUCTS //
	// -------- //

	@HystrixCommand(fallbackMethod = "defaultProduct")
	public ResponseEntity<Product> getProduct(int productId) {
		ResponseEntity<String> resultStr = null;
		Product product = null;
		try {

			URI uri = getServiceUrl("product");

			String url = uri.toString() + "/product/" + productId;

			restTemplate = new RestTemplate();
			resultStr = restTemplate.getForEntity(url, String.class);

			product = response2Product(resultStr);
			System.out.println(resultStr);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return createOkResponse(product);
	}

	
	/**
     * Fallback method for getProduct()
     *
     * @param productId
     * @return
     */
    public ResponseEntity<Product> defaultProduct(int productId) {
        return createResponse(null, HttpStatus.BAD_GATEWAY);
    }
    
    
    
    
    
 // --------------- //
    // RECOMMENDATIONS //
    // --------------- //

    @HystrixCommand(fallbackMethod = "defaultRecommendations")
    public ResponseEntity<List<Recommendation>> getRecommendations(int productId) {
        try {
            System.out.println("GetRecommendations...");

            URI uri = getServiceUrl("recommendation");

            String url = uri.toString() + "/recommendation?productId=" + productId;
            System.out.println("GetRecommendations from URL: {}"+ url);

            ResponseEntity<String> resultStr = restTemplate.getForEntity(url, String.class);
            System.out.println("GetRecommendations http-status: {}"+ resultStr.getStatusCode());
            System.out.println("GetRecommendations body: {}"+ resultStr.getBody());

            List<Recommendation> recommendations = response2Recommendations(resultStr);
            System.out.println("GetRecommendations.cnt {}"+ recommendations.size());

            return createOkResponse(recommendations);
        } catch (Throwable t) {
        	System.out.println("getRecommendations error"+ t);
            throw t;
//            throw new RuntimeException(t);
        }
    }


    /**
     * Fallback method for getRecommendations()
     *
     * @param productId
     * @return
     */
    public ResponseEntity<List<Review>> defaultRecommendations(int productId) {
    	System.out.println("Using fallback method for recommendation-service");
        return createResponse(null, HttpStatus.BAD_GATEWAY);
    }
    
    
    
 // ------- //
    // REVIEWS //
    // ------- //

    @HystrixCommand(fallbackMethod = "defaultReviews")
    public ResponseEntity<List<Review>> getReviews(int productId) {
        System.out.println("GetReviews...");

        URI uri = getServiceUrl("review");

        String url = uri.toString() + "/review?productId=" + productId;
        System.out.println("GetReviews from URL: {}"+ url);

        ResponseEntity<String> resultStr = restTemplate.getForEntity(url, String.class);
        System.out.println("GetReviews http-status: {}"+ resultStr.getStatusCode());
        System.out.println("GetReviews body: {}"+ resultStr.getBody());

        List<Review> reviews = response2Reviews(resultStr);
        System.out.println("GetReviews.cnt {}"+ reviews.size());

        return createOkResponse(reviews);
    }


    /**
     * Fallback method for getReviews()
     *
     * @param productId
     * @return
     */
    public ResponseEntity<List<Review>> defaultReviews(int productId) {
    	System.out.println("Using fallback method for review-service");
        return createResponse(null, HttpStatus.BAD_GATEWAY);
    }
    
	

	public Product response2Product(ResponseEntity<String> response) {
		try {
			return getProductReader().readValue(response.getBody());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private ObjectReader getProductReader() {

		if (productReader != null)
			return productReader;

		ObjectMapper mapper = new ObjectMapper();
		return productReader = mapper.reader(Product.class);
	}
	
	
	
	 private List<Recommendation> response2Recommendations(ResponseEntity<String> response) {
	        try {
	            ObjectMapper mapper = new ObjectMapper();
	            List list = mapper.readValue(response.getBody(), new TypeReference<List<Recommendation>>() {});
	            List<Recommendation> recommendations = list;
	            return recommendations;

	        } catch (IOException e) {
	            System.out.println("IO-err. Failed to read JSON"+ e);
	            throw new RuntimeException(e);

	        } catch (RuntimeException re) {
	            System.out.println("RTE-err. Failed to read JSON"+ re);
	            throw re;
	        }
	    }
	 
	 private List<Review> response2Reviews(ResponseEntity<String> response) {
	        try {
	            ObjectMapper mapper = new ObjectMapper();
	            List list = mapper.readValue(response.getBody(), new TypeReference<List<Review>>() {});
	            List<Review> reviews = list;
	            return reviews;

	        } catch (IOException e) {
	            System.out.println("IO-err. Failed to read JSON"+ e);
	            throw new RuntimeException(e);

	        } catch (RuntimeException re) {
	            System.out.println("RTE-err. Failed to read JSON"+ re);
	            throw re;
	        }
	    }
	

	public URI getServiceUrl(String serviceId) {
		return getServiceUrl(serviceId, null);
	}

	protected URI getServiceUrl(String serviceId, String fallbackUri) {
		URI uri = null;
		try {

			ServiceInstance instance = loadBalancer.choose(serviceId);

			if (instance == null) {
				throw new RuntimeException("Can't find a service with serviceId = " + serviceId);
			}

			uri = instance.getUri();
			System.out.println("Resolved serviceId '{}' to URL '{}'., ServiceID :" + serviceId + " URI: " + uri);

		} catch (RuntimeException e) {
			// Eureka not available, use fallback if specified otherwise rethrow
			// the error
			if (fallbackUri == null) {
				throw e;

			} else {
				uri = URI.create(fallbackUri);
				System.out.println(
						"Failed to resolve serviceId '{}' to URL '{}'., ServiceID :" + serviceId + " URI: " + uri);
			}
		}

		return uri;
	}

	public <T> ResponseEntity<T> createOkResponse(T body) {
		return createResponse(body, HttpStatus.OK);
	}

	public <T> ResponseEntity<T> createResponse(T body, HttpStatus httpStatus) {
		return new ResponseEntity<>(body, httpStatus);
	}

}