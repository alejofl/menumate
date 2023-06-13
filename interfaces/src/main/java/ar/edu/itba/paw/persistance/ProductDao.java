package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.model.Product;
import ar.edu.itba.paw.model.Promotion;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

public interface ProductDao {

    Optional<Product> getById(long productId);

    Product create(long categoryId, String name, String description, Long imageId, BigDecimal price);

    void delete(long productId);

    Promotion createPromotion(Product source, LocalDateTime startDate, LocalDateTime endDate, float discount);

    Optional<Promotion> hasPromotionInRange(long sourceProductId, LocalDateTime startDate, LocalDateTime endDate);

    void updateNameAndDescription(Product product, String name, String description);

    void stopPromotionByDestination(long destinationProductId);

    void stopPromotionsBySource(long sourceProductId);

    void startActivePromotions();

    void closeInactivePromotions();
}
