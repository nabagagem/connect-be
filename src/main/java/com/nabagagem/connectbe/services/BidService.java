package com.nabagagem.connectbe.services;

import com.nabagagem.connectbe.domain.BidCommand;
import com.nabagagem.connectbe.domain.ListBidsCommand;
import com.nabagagem.connectbe.entities.Bid;
import com.nabagagem.connectbe.resources.BidRepository;
import com.nabagagem.connectbe.resources.JobRepo;
import com.nabagagem.connectbe.resources.ProfileRepo;
import com.nabagagem.connectbe.services.notifications.PublishNotification;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
@Transactional
public class BidService {
    private final BidRepository bidRepository;
    private final BidMapper bidMapper;
    private final ProfileRepo profileRepo;
    private final JobRepo jobRepo;

    @PublishNotification
    public Bid create(BidCommand bidCommand) {
        Bid bid = bidMapper.toEntity(bidCommand);
        bid.setTargetJob(jobRepo.findById(bidCommand.jobId()).orElseThrow());
        bid.setOwner(profileRepo.findById(bidCommand.owner()).orElseThrow());
        return bidRepository.save(bid);
    }

    public List<Bid> listUserBids(ListBidsCommand listBidsCommand) {
        return bidRepository.findByOwner(
                listBidsCommand.userId(),
                listBidsCommand.bidSearchParams().bidStatus(),
                listBidsCommand.bidSearchParams().direction().toString(),
                listBidsCommand.pageable());
    }

    public Optional<Bid> findById(UUID bidId) {
        return bidRepository.findById(bidId);
    }

    public void delete(UUID bidId) {
        bidRepository.deleteById(bidId);
    }
}
