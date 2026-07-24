package com.travelapp.wishlist.ports;
import com.travelapp.wishlist.domain.WishlistItem;
import java.util.*;

public interface WishlistRepository {
    WishlistItem save(WishlistItem item);
    Optional<WishlistItem> findById(UUID id);
    List<WishlistItem> findByTripId(UUID tripId);
    List<WishlistItem> findByTripIdAndCity(UUID tripId, String city);
    void deleteById(UUID id);
}
