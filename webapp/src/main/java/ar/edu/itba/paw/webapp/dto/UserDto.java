package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class UserDto {
    private long userId;
    private String email;
    private String name;
    private LocalDateTime dateJoined;
    private boolean isActive;
    private String preferredLanguage;

    public static UserDto fromUser(final User user) {
        final UserDto dto = new UserDto();
        dto.userId = user.getUserId();
        dto.email = user.getEmail();
        dto.name = user.getName();
        dto.dateJoined = user.getDateJoined();
        dto.isActive = user.getIsActive();
        dto.preferredLanguage = user.getPreferredLanguage();

        return dto;
    }

    public static List<UserDto> fromUserList(final List<User> users) {
        return users.stream().map(UserDto::fromUser).collect(Collectors.toList());
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getDateJoined() {
        return dateJoined;
    }

    public void setDateJoined(LocalDateTime dateJoined) {
        this.dateJoined = dateJoined;
    }

    public boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(boolean active) {
        isActive = active;
    }

    public String getPreferredLanguage() {
        return preferredLanguage;
    }

    public void setPreferredLanguage(String preferredLanguage) {
        this.preferredLanguage = preferredLanguage;
    }
}
