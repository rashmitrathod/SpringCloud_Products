package se.callista.microservices.composite.product.service;



import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public String getProduct(@PathVariable int productId) {

        // 1. First get mandatory product information
    	System.out.println("Inside getProduct of ProductCompositIntegration..");
        integration.getProduct(productId);

        
        
        /*if (!productResult.getStatusCode().is2xxSuccessful()) {
            // We can't proceed, return whatever fault we got from the getProduct call
            return util.createResponse(null, productResult.getStatusCode());
        }

        // 2. Get optional recommendations
        List<Recommendation> recommendations = null;
        try {
            ResponseEntity<List<Recommendation>> recommendationResult = integration.getRecommendations(productId);
            if (!recommendationResult.getStatusCode().is2xxSuccessful()) {
                // Something went wrong with getRecommendations, simply skip the recommendation-information in the response
                LOG.debug("Call to getRecommendations failed: {}", recommendationResult.getStatusCode());
            } else {
                recommendations = recommendationResult.getBody();
            }
        } catch (Throwable t) {
            LOG.error("getProduct error", t);
            throw t;
        }


        // 3. Get optional reviews
        ResponseEntity<List<Review>> reviewsResult = integration.getReviews(productId);
        List<Review> reviews = null;
        if (!reviewsResult.getStatusCode().is2xxSuccessful()) {
            // Something went wrong with getReviews, simply skip the review-information in the response
            LOG.debug("Call to getReviews failed: {}", reviewsResult.getStatusCode());
        } else {
            reviews = reviewsResult.getBody();
        }
*/
        return "ProductCompositService getProduct called successfully";
    }
}
