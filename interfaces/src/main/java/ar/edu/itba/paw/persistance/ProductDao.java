package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.model.Category;
import ar.edu.itba.paw.model.Product;
import ar.edu.itba.paw.model.Promotion;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ProductDao {

    Optional<Product> getById(long productId);

    Optional<Promotion> getPromotionById(long promotionId);

    Product create(long category, String name, String description, Long imageId, BigDecimal price);

    Product create(Category category, String name, String description, Long imageId, BigDecimal price);

    void delete(long productId);

    Promotion createPromotion(Product source, LocalDateTime startDate, LocalDateTime endDate, BigDecimal discountPercentage);

    Optional<Promotion> hasPromotionInRange(long sourceProductId, LocalDateTime startDate, LocalDateTime endDate);

    void updateNameAndDescription(Product product, String name, String description);

    void stopPromotion(long restaurantId, long promotionId);

    void stopPromotionsBySource(long sourceProductId);

    void startActivePromotions();

    void closeInactivePromotions();

    boolean areAllProductsFromRestaurant(long restaurantId, List<Long> productIds);
}
