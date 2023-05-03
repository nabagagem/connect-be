package com.nabagagem.connectbe.resources;

import com.nabagagem.connectbe.entities.Bid;
import com.nabagagem.connectbe.entities.BidStatus;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.UUID;

public interface BidRepository extends CrudRepository<Bid, UUID>,
        PagingAndSortingRepository<Bid, UUID> {

    @Query("""
                select b from Bid b
                    inner join fetch b.targetJob j
                where (
                       (b.owner.id = :ownerId and :direction = 'FROM_ME')
                    or (j.owner.id = :ownerId and :direction = 'TO_ME')
                )
                and (b.bidStatus = :bidStatus or :bidStatus is null)
            """)
    List<Bid> findByOwner(UUID ownerId,
                          BidStatus bidStatus,
                          String direction, Sort sort);
}