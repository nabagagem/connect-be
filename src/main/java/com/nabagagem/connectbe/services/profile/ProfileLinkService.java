package com.nabagagem.connectbe.services.profile;

import com.nabagagem.connectbe.domain.profile.ProfileLinksCommand;
import com.nabagagem.connectbe.entities.ConnectProfile;
import com.nabagagem.connectbe.entities.LinkType;
import com.nabagagem.connectbe.entities.ProfileLink;
import com.nabagagem.connectbe.repos.ProfileLinkRepository;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class ProfileLinkService {
    private final ProfileService profileService;
    private final ProfileLinkRepository profileLinkRepository;

    public void update(ProfileLinksCommand profileLinksCommand) {
        ConnectProfile profile = profileService.findOrCreate(profileLinksCommand.id());
        profileLinkRepository.deleteByProfile(profile);
        profile.setProfileLinks(
                profileLinksCommand.links()
                        .entrySet().stream()
                        .filter(entry -> StringUtils.isNotBlank(entry.getValue()))
                        .map(entry -> ProfileLink.builder()
                                .linkURL(toURL(entry.getKey(), entry.getValue()))
                                .linkType(entry.getKey())
                                .profile(profile)
                                .build())
                        .collect(Collectors.toSet())
        );
        profileService.save(profile);
    }

    private String toURL(LinkType linkType, String url) {
        if (url.startsWith("https")) {
            return url;
        }
        String[] parts = url.split("/");
        final String lastPart = parts[parts.length - 1];
        return switch (linkType) {
            case X -> String.format("https://twitter.com/%s", lastPart);
            case LINKEDIN -> String.format("https://linkedin.com/%s", getLinkedInUser(parts));
            case FACEBOOK -> String.format("https://facebook.com/%s", lastPart);
            default -> url;
        };
    }

    private String getLinkedInUser(String[] parts) {
        int length = parts.length;
        String lastPart = parts[length - 1];
        return lastPart.contains("locale=") && length >= 2 ? parts[length - 2] : lastPart;
    }
}
