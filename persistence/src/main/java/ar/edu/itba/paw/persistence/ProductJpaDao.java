package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.Category;
import ar.edu.itba.paw.model.Product;
import ar.edu.itba.paw.persistance.ProductDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.util.Optional;

@Repository
public class ProductJpaDao implements ProductDao {

    private final static Logger LOGGER = LoggerFactory.getLogger(ProductJpaDao.class);

    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<Product> getById(long productId) {
        return Optional.ofNullable(em.find(Product.class, productId));
    }

    @Override
    public Product create(long categoryId, String name, String description, Long imageId, BigDecimal price) {
        final Product product = new Product(categoryId, name, description, imageId, price, true);
        em.persist(product);
        LOGGER.info("Created product with id {} for category {}", product.getProductId(), categoryId);
        return product;
    }

    @Override
    public void delete(long productId) {
        final Product product = em.getReference(Product.class, productId);
        em.remove(product);  // TODO: Logical deletion
        LOGGER.info("Deleted product id {}", product.getProductId());
    }
}
