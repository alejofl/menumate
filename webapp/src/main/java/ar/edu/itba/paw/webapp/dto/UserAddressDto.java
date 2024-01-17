package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.model.UserAddress;
import ar.edu.itba.paw.webapp.utils.UriUtils;

import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class UserAddressDto {
    private long addressId;
    private String address;
    private String name;
    private LocalDateTime lastUsed;

    private URI selfUrl;

    public static UserAddressDto fromUserAddress(final UriInfo uriInfo, final UserAddress userAddress) {
        UserAddressDto dto = new UserAddressDto();
        dto.addressId = userAddress.getAddressId();
        dto.address = userAddress.getAddress();
        dto.name = userAddress.getName();
        dto.lastUsed = userAddress.getLastUsed();

        dto.selfUrl = UriUtils.getUserAddressUri(uriInfo, userAddress);

        return dto;
    }

    public static List<UserAddressDto> fromUserAddressCollection(final UriInfo uriInfo, final Collection<UserAddress> userAddresses) {
        return userAddresses.stream().map(u -> fromUserAddress(uriInfo, u)).collect(Collectors.toList());
    }

    public long getAddressId() {
        return addressId;
    }

    public void setAddressId(long addressId) {
        this.addressId = addressId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getLastUsed() {
        return lastUsed;
    }

    public void setLastUsed(LocalDateTime lastUsed) {
        this.lastUsed = lastUsed;
    }

    public URI getSelfUrl() {
        return selfUrl;
    }

    public void setSelfUrl(URI selfUrl) {
        this.selfUrl = selfUrl;
    }

    @Override
    public int hashCode() {
        return Objects.hash(addressId, address, name, lastUsed);
    }
}
