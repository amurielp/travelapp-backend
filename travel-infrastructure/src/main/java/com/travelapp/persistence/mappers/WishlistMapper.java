package com.travelapp.persistence.mappers;

import com.travelapp.persistence.entities.WishlistItemEntity;
import com.travelapp.wishlist.domain.WishlistItem;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface WishlistMapper {

    @Mapping(target = "createdAt", ignore = true)
    WishlistItemEntity toEntity(WishlistItem item);

    WishlistItem toDomain(WishlistItemEntity entity);
}
