package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> getItemsByOwnerOrderByIdAsc(Long userId);

    @Query("select i from Item i where ((LOWER(i.name) like LOWER(concat('%', :text, '%'))) " +
            "or (LOWER(i.description) like LOWER(concat('%', :text, '%'))))")
    Collection<Item> searchItemsByText(@Param("text") String text);
}
