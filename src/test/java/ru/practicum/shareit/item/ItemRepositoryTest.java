package ru.practicum.shareit.item;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class ItemRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;

    private User user1;
    private User user2;
    private Item item1;
    private Item item2;

    @BeforeEach
    void before() {
        user1 = userRepository.save(new User(1L, "Anton", "anton@mail.ru"));
        user2 = userRepository.save(new User(2L, "Denis", "denis@mail.ru"));
        item1 = itemRepository.save(new Item(1L, "Book", "Interesting",
                true, user1.getId(), null));
        item2 = itemRepository.save(new Item(2L, "Beer", "Tasty",
                true, user2.getId(), null));
    }

    @AfterEach
    void after() {
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void shouldFindByOwner() {
        final List<Item> owner = itemRepository.getItemsByOwnerOrderByIdAsc(user1.getId());
        assertNotNull(owner);
        assertEquals(1L, owner.get(0).getId());
        assertEquals("Book", owner.get(0).getName());
    }

    @Test
    void shouldFindByText() {
        Collection<Item> itemList = itemRepository.searchItemsByText("st");
        assertThat(itemList.size(), is(2));
    }
}
