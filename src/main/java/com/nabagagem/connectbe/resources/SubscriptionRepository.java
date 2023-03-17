package com.nabagagem.connectbe.resources;

import com.nabagagem.connectbe.entities.Subscription;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.UUID;

@RepositoryRestResource
public interface SubscriptionRepository extends PagingAndSortingRepository<Subscription, UUID> {
}