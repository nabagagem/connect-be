package com.nabagagem.connectbe;

import com.nabagagem.connectbe.entities.Account;
import com.nabagagem.connectbe.entities.Address;
import com.nabagagem.connectbe.entities.ConnectProfile;
import com.nabagagem.connectbe.entities.ContactInfo;
import com.nabagagem.connectbe.entities.Gig;
import com.nabagagem.connectbe.resources.AccountResource;
import com.nabagagem.connectbe.resources.AddressResource;
import com.nabagagem.connectbe.resources.ApproachMessageResource;
import com.nabagagem.connectbe.resources.ApproachResource;
import com.nabagagem.connectbe.resources.ConnectProfileResource;
import com.nabagagem.connectbe.resources.GigResource;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@AllArgsConstructor
public class Init implements CommandLineRunner {

    private final AccountResource accountResource;
    private final AddressResource addressResource;
    private final GigResource gigResource;

    private final ApproachMessageResource approachMessageResource;

    private final ApproachResource approachResource;
    private final ConnectProfileResource connectProfileResource;

    @Override

    public void run(String... args) {
        connectProfileResource.deleteAll();
        addressResource.deleteAll();
        gigResource.deleteAll();
        accountResource.deleteAll();
        approachMessageResource.deleteAll();
        approachResource.deleteAll();

        Address address = addressResource.save(
                Address.builder()
                        .country("DE")
                        .addressLine1("address line 1")
                        .addressLine2("address line 2")
                        .zipCode("12207")
                        .build()
        );
        Gig gig = gigResource.save(
                Gig.builder()
                        .summary("summary")
                        .title("title")
                        .tags(Set.of("tag1", "tag2"))
                        .build()
        );
        Account another = accountResource.save(
                Account.builder()
                        .userId("another")
                        .addresses(Set.of(address))
                        .build()
        );
        ConnectProfile profile = connectProfileResource.save(
                ConnectProfile.builder()
                        .bio("thats my bio")
                        .contactInfo(ContactInfo.builder()
                                .countryCode("DE")
                                .dialCode("49")
                                .email("my@mail.com")
                                .phoneNumber("564564")
                                .build())
                        .language("PT")
                        .shares(Set.of(another))
                        .address(address)
                        .build());
        
        Account account = accountResource.save(
                Account.builder()
                        .userId("user")
                        .addresses(Set.of(address))
                        .connectProfiles(Set.of(profile))
                        .gigs(Set.of(gig))
                        .build()
        );

    }
}
