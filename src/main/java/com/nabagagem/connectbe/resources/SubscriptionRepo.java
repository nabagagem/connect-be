package com.nabagagem.connectbe.resources;

import com.nabagagem.connectbe.entities.Subscription;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SubscriptionRepo extends PagingAndSortingRepository<Subscription, UUID>,
        CrudRepository<Subscription, UUID> {
}