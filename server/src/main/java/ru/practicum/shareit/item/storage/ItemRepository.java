package ru.practicum.shareit.item.storage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByOwnerId(Long id, Pageable pageable);

    @Query("SELECT it " +
            "FROM Item it " +
            "WHERE it.available = TRUE AND " +
            "(LOWER(it.name) LIKE CONCAT('%',LOWER(:text),'%') OR " +
            "LOWER(it.description) LIKE CONCAT('%',LOWER(:text),'%'))")
    List<Item> findItemsByQueryWithPagination(@Param("text") String text, Pageable pageable);
}
