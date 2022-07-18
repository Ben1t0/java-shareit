package ru.practicum.shareit.item.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemRepository extends JpaRepository<Item, Long> {
    Collection<Item> findAllByOwnerId(Long id);

    @Query("SELECT it " +
            "FROM Item it " +
            "WHERE it.available = TRUE AND " +
            "(LOWER(it.name) LIKE CONCAT('%',LOWER(:text),'%') OR " +
            "LOWER(it.description) LIKE CONCAT('%',LOWER(:text),'%'))")
    Collection<Item> findItemsByQuery(@Param("text") String text);
}
