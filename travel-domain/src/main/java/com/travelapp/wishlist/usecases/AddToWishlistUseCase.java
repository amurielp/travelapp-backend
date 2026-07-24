package com.travelapp.wishlist.usecases;
import com.travelapp.wishlist.domain.WishlistItem;
import com.travelapp.wishlist.ports.WishlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service @RequiredArgsConstructor
public class AddToWishlistUseCase {
    private final WishlistRepository wishlist;

    @Transactional
    public WishlistItem execute(AddToWishlistCommand cmd) {
        return wishlist.save(WishlistItem.builder()
            .id(UUID.randomUUID())
            .tripId(cmd.tripId())
            .name(cmd.name())
            .category(cmd.category())
            .destinationCity(cmd.destinationCity())
            .latitude(cmd.latitude())
            .longitude(cmd.longitude())
            .externalPlaceId(cmd.externalPlaceId())
            .source("manual")
            .priority(cmd.priority() > 0 ? cmd.priority() : 2)
            .estimatedCost(cmd.estimatedCost())
            .websiteUrl(cmd.websiteUrl())
            .build());
    }
}
