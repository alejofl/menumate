package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.model.Product;
import ar.edu.itba.paw.webapp.utils.UriUtils;

import javax.ws.rs.core.UriInfo;
import java.math.BigDecimal;
import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ProductDto {
    private long productId;
    private long categoryId;
    private String name;
    private String description;
    private Long imageId;
    private BigDecimal price;
    private boolean available;
    private boolean deleted;

    private URI selfUrl;
    private URI imageUrl;
    private URI categoryUrl;

    public static ProductDto fromProduct(final UriInfo uriInfo, final Product product) {
        final ProductDto dto = new ProductDto();
        dto.productId = product.getProductId();
        dto.categoryId = product.getCategoryId();
        dto.name = product.getName();
        dto.description = product.getDescription();
        dto.imageId = product.getImageId();
        dto.price = product.getPrice();
        dto.available = product.getAvailable();
        dto.deleted = product.getDeleted();

        dto.selfUrl = UriUtils.getProductUri(uriInfo, product);
        dto.imageUrl = UriUtils.getImageUri(uriInfo, product.getImageId());
        dto.categoryUrl = UriUtils.getCategoryUri(uriInfo, product.getCategory());

        return dto;
    }

    public static List<ProductDto> fromProductCollection(final UriInfo uriInfo, final Collection<Product> products) {
        return products.stream().map(p -> fromProduct(uriInfo, p)).collect(Collectors.toList());
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getImageId() {
        return imageId;
    }

    public void setImageId(Long imageId) {
        this.imageId = imageId;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public boolean getAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public URI getSelfUrl() {
        return selfUrl;
    }

    public void setSelfUrl(URI selfUrl) {
        this.selfUrl = selfUrl;
    }

    public URI getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(URI imageUrl) {
        this.imageUrl = imageUrl;
    }

    public URI getCategoryUrl() {
        return categoryUrl;
    }

    public void setCategoryUrl(URI categoryUrl) {
        this.categoryUrl = categoryUrl;
    }
}
