package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.model.UserAddress;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class UserAddressDto {
    private String address;
    private String name;
    private LocalDateTime lastUsed;

    public static UserAddressDto fromUserAddress(final UserAddress userAddress) {
        UserAddressDto dto = new UserAddressDto();
        dto.address = userAddress.getAddress();
        dto.name = userAddress.getName();
        dto.lastUsed = userAddress.getLastUsed();

        return dto;
    }

    public static List<UserAddressDto> fromUserAddressCollection(final Collection<UserAddress> userAddresses) {
        return userAddresses.stream().map(UserAddressDto::fromUserAddress).collect(Collectors.toList());
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
}
