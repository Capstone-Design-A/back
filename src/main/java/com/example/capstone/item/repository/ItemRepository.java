package com.example.capstone.item.repository;

import com.example.capstone.item.Item;
import com.example.capstone.item.repository.custom.ItemRepositoryCustom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long>, ItemRepositoryCustom {
    Page<Item> findByCategoryId(Long categoryId, Pageable pageable);

    Page<Item> findAllBy(Pageable pageable);

    @Query("select i from Item i " +
            "where (i.stock - ((select sum(oi.quantity) from OrderItem oi where oi.item.id = i.id and oi.status != com.example.capstone.order.common.OrderStatus.CANCELED )) <= (i.stock * 0.1)) " +
            "OR i.deadline < :seven")
    Page<Item> searchImminentItem(LocalDateTime seven, Pageable pageable);

    @Query("select i from Item i " +
            "where " +
                "(i.stock - ((" +
                "select sum(oi.quantity) " +
                "from OrderItem oi " +
                "where " +
                    "oi.item.seller.id = :sellerId " +
                    "and oi.item.id = i.id " +
                    "and oi.status != com.example.capstone.order.common.OrderStatus.CANCELED)) <= (i.stock * 0.1)) " +
            "OR (:nowDate > i.deadline and i.deadline < :seven)")
    Page<Item> searchImminentItem(Long sellerId, LocalDateTime nowDate, LocalDateTime seven, Pageable pageable);

    @Query("SELECT i FROM Item i " +
            "LEFT JOIN OrderItem oi ON oi.item.id = i.id " +
            "WHERE oi.createdAt " +
            "BETWEEN :startDay AND :endDay " +
            "GROUP BY i " +
            "ORDER BY COUNT(oi) DESC")
    Page<Item> searchPopularItem(LocalDateTime startDay, LocalDateTime endDay, Pageable pageable);

    @Query("SELECT i FROM Item i " +
            "JOIN Subscription sub ON i.seller.id = sub.toMember.id " +
            "where sub.fromMember.id = :fromMemberId " +
            "ORDER BY i.createdAt DESC")
    Page<Item> searchSubscribedItem(Long fromMemberId, Pageable pageable);

    Page<Item> findByNameContaining(String keyword, Pageable pageable);

    Page<Item> findBySellerId(Long sellerId, Pageable pageable);

    Optional<Item> findItemById(Long itemId);

}
