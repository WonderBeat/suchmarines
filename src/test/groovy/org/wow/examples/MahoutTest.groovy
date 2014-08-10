package org.wow.examples

import org.apache.mahout.cf.taste.impl.model.file.FileDataModel
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity
import org.apache.mahout.cf.taste.recommender.RecommendedItem
import org.springframework.core.io.ClassPathResource
import spock.lang.Specification
/**
 *
 */
class MahoutTest extends Specification {

    def 'Mahout example recommendations'() {
        given:
        //consider every line has the format userID,itemID,value, where "value" is similar to "rating"
        def resource = new ClassPathResource("dataset.csv")
        def model = new FileDataModel(resource.getFile())
        def similarity = new PearsonCorrelationSimilarity(model)
        def neighborhood = new ThresholdUserNeighborhood(0.1, similarity, model)
        def recommender = new GenericUserBasedRecommender(model, neighborhood, similarity)

        when:
        def recommendations = recommender.recommend(2, 5);

        then:
        for (RecommendedItem recommendation : recommendations) {
            System.out.println(recommendation);
        }
    }

}
