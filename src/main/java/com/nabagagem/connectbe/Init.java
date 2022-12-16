package com.nabagagem.connectbe;

import com.nabagagem.connectbe.entities.Account;
import com.nabagagem.connectbe.entities.Address;
import com.nabagagem.connectbe.entities.Gig;
import com.nabagagem.connectbe.resources.AccountResource;
import com.nabagagem.connectbe.resources.AddressResource;
import com.nabagagem.connectbe.resources.GigResource;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;

@Component
@AllArgsConstructor
public class Init implements CommandLineRunner {

    private final AccountResource accountResource;
    private final AddressResource addressResource;
    private final GigResource gigResource;

    @Override
    public void run(String... args) {
        addressResource.delete();
        gigResource.deleteAll();
        accountResource.delete();

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
        accountResource.save(
                Account.builder()
                        .userId(UUID.randomUUID().toString())
                        .addresses(Set.of(address))
                        .gigs(Set.of(gig))
                        .build()
        );

    }
}
