package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.model.Promotion;
import ar.edu.itba.paw.webapp.utils.UriUtils;

import javax.ws.rs.core.UriInfo;
import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class PromotionDto {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private BigDecimal discountPercentage;

    private URI selfUrl;
    private URI sourceUrl;
    private URI destinationUrl;

    public static PromotionDto fromPromotion(final UriInfo uriInfo, final Promotion promotion, final long restaurantId) {
        final PromotionDto dto = new PromotionDto();
        dto.startDate = promotion.getStartDate();
        dto.endDate = promotion.getEndDate();
        dto.discountPercentage = promotion.getDiscountPercentage();

        dto.selfUrl = UriUtils.getPromotionUri(uriInfo, restaurantId, promotion.getPromotionId());
        dto.sourceUrl = UriUtils.getProductUri(uriInfo, promotion.getSource());
        dto.destinationUrl = UriUtils.getProductUri(uriInfo, promotion.getDestination());

        return dto;
    }

    public static List<PromotionDto> fromPromotionCollection(final UriInfo uriInfo, final Collection<Promotion> promotions, long restaurantId) {
        return promotions.stream().map(c -> fromPromotion(uriInfo, c, restaurantId)).collect(Collectors.toList());
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public BigDecimal getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(BigDecimal discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public URI getSelfUrl() {
        return selfUrl;
    }

    public void setSelfUrl(URI selfUrl) {
        this.selfUrl = selfUrl;
    }

    public URI getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(URI sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public URI getDestinationUrl() {
        return destinationUrl;
    }

    public void setDestinationUrl(URI destinationUrl) {
        this.destinationUrl = destinationUrl;
    }
}
