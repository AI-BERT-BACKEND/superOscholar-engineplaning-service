package com.aibert.dosw.domain.ports.out;

import com.aibert.dosw.domain.model.recommendation.Recommendation;
import java.util.List;
import java.util.Optional;

/**
 * Output port for recommendation persistence.
 * The infrastructure implements this contract.
 */
public interface RecommendationRepositoryPort {

    /**
     * Saves a new recommendation.
     *
     * @param recommendation recommendation to save
     * @return saved recommendation with assigned id
     */
    Recommendation save(Recommendation recommendation);

    /**
     * Finds all recommendations for a user.
     *
     * @param userId user id
     * @return recommendations ordered by descending date
     */
    List<Recommendation> findByUserId(String userId);

    /**
     * Finds unread recommendations for a user.
     *
     * @param userId user id
     * @return unread recommendations
     */
    List<Recommendation> findUnreadByUserId(String userId);

    /**
     * Finds a recommendation by id.
     *
     * @param id recommendation id
     * @return optional recommendation if it exists
     */
    Optional<Recommendation> findById(String id);

    /**
     * Marks a recommendation as read.
     *
     * @param id recommendation id
     */
    void markAsRead(String id);

    /**
     * Saves multiple recommendations in batch.
     *
     * @param recommendations list of recommendations
     * @return saved recommendations
     */
    List<Recommendation> saveAll(List<Recommendation> recommendations);
}
