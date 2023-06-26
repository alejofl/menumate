package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.Product;
import ar.edu.itba.paw.model.Promotion;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

public interface ProductService {

    Optional<Product> getById(long productId);

    Product create(long categoryId, String name, String description, byte[] image, BigDecimal price);

    Product update(long productId, String name, BigDecimal price, String description);

    void updateImage(long productId, byte[] image);

    void delete(long productId);

    Promotion createPromotion(long sourceProductId, LocalDateTime startDate, LocalDateTime endDate, int discountPercentage);

    /**
     * Gets whether a product has any promotions whose active time range intersects with the specified time range.
     *
     * @return An empty optional if no promotions were found, or one (any) promotion if at least one was found.
     */
    Optional<Promotion> hasPromotionInRange(long sourceProductId, LocalDateTime startDate, LocalDateTime endDate);

    void stopPromotionByDestination(long destinationProductId);
}
