package se.callista.microservices.composite.product.service;



import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.util.Date;
import java.util.List;

import javax.ws.rs.Produces;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import se.callista.microservices.composite.product.model.ProductAggregated;
import se.callista.microservises.core.product.model.Product;
import se.callista.microservises.core.recommendation.model.Recommendation;
import se.callista.microservises.core.review.model.Review;

/**
 * Created by magnus on 04/03/15.
 */
@RestController
public class ProductCompositeService {

    private static final Logger LOG = LoggerFactory.getLogger(ProductCompositeService.class);

    @Autowired
    ProductCompositeIntegration integration;


    @RequestMapping("/")
    public String getProduct() {
        return "{\"timestamp\":\"" + new Date() + "\",\"content\":\"Hello from ProductAPi\"}";
    }

    @RequestMapping("/product/{productId}")
    @Produces(APPLICATION_JSON)
    public ResponseEntity<ProductAggregated> getProduct(@PathVariable int productId) {

        // 1. First get mandatory product information
    	System.out.println("Inside getProduct of ProductCompositIntegration..");
    	ResponseEntity<Product> productResult = integration.getProduct(productId);

        
        
        if (!productResult.getStatusCode().is2xxSuccessful()) {

        	// We can't proceed, return whatever fault we got from the getProduct call
            return createResponse(null, productResult.getStatusCode());
        }
        

        
       // 2. Get optional recommendations
        List<Recommendation> recommendations = null;
        try {
            ResponseEntity<List<Recommendation>> recommendationResult = integration.getRecommendations(productId);
            if (!recommendationResult.getStatusCode().is2xxSuccessful()) {
                // Something went wrong with getRecommendations, simply skip the recommendation-information in the response
                System.out.println("Call to getRecommendations failed: {}"+ recommendationResult.getStatusCode());
            } else {
                recommendations = recommendationResult.getBody();
            }
        } catch (Throwable t) {
        	System.out.println("getProduct error"+ t);
            throw t;
        }


        // 3. Get optional reviews
        ResponseEntity<List<Review>> reviewsResult = integration.getReviews(productId);
        List<Review> reviews = null;
        if (!reviewsResult.getStatusCode().is2xxSuccessful()) {
            // Something went wrong with getReviews, simply skip the review-information in the response
            System.out.println("Call to getReviews failed: {}"+ reviewsResult.getStatusCode());
        } else {
            reviews = reviewsResult.getBody();
        }

       // return "ProductCompositService getProduct called successfully";
        return createOkResponse(new ProductAggregated(productResult.getBody(), recommendations, reviews));
    }
    
    public <T> ResponseEntity<T> createResponse(T body, HttpStatus httpStatus) {
        return new ResponseEntity<>(body, httpStatus);
    }
    
    public <T> ResponseEntity<T> createOkResponse(T body) {
        return createResponse(body, HttpStatus.OK);
    }
    
    /**
     * Clone an existing result as a new one, filtering out http headers that not should be moved on and so on...
     *
     * @param result
     * @param <T>
     * @return
     */
  
}
