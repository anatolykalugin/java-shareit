package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class ItemServiceTest {
    @Autowired
    private final CommentRepository commentRepository;
    @Autowired
    private final ItemRepository itemRepository;
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final BookingRepository bookingRepository;
    @Autowired
    private final BookingService bookingService;
    @Autowired
    private final UserService userService = Mockito.mock(UserService.class);
    @Autowired
    private final ItemService itemService;

    private final User owner = new User(1L, "Denis", "denis@mail.ru");
    private final User user2 = new User(2L, "Igor", "igor@mail.ru");
    private final Item item = new Item(1L, "Пиво", "Козел светлый", true,
            owner.getId(), null);
    private final ItemWithBookingsDto itemCommentDto;


    @Autowired
    public ItemServiceTest(CommentRepository commentRepository, UserRepository userRepository,
                           ItemRepository itemRepository,
                           BookingService bookingService, BookingRepository bookingRepository,
                           ItemService itemService) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.itemService = itemService;
        this.bookingService = bookingService;
        this.bookingRepository = bookingRepository;
        LocalDateTime localDateTime = LocalDateTime.now();
        itemCommentDto = ItemMapper.mapToWithBookings(
                item,
                bookingRepository.findTopByItem_IdAndEndPeriodBeforeOrderByEndPeriodDesc(item.getId(), localDateTime),
                bookingRepository.findTopByItem_IdAndStartPeriodAfterOrderByStartPeriodAsc(item.getId(), localDateTime),
                new ArrayList<>()
        );
        userRepository.save(owner);
        userRepository.save(user2);
    }

    @Test
    void shouldCreateItem() {
        ItemDto item1 = itemService.createItem(item.getOwner(), ItemMapper.mapTo(item, new ArrayList<>()));
        assertEquals(itemRepository.findById(item.getId()).orElse(null).getId(), item1.getId());
    }

    @Test
    void shouldUpdateItem() {
        Item item = new Item(1L, "Все еще пиво", "но уже Козел темный", true,
                owner.getId(), null);
        Item updatedItem = new Item();
        itemService.createItem(item.getOwner(), ItemMapper.mapTo(item, new ArrayList<>()));
        updatedItem.setAvailable(false);
        updatedItem.setDescription("Козел темный");
        updatedItem.setName("Пивко");
        userService.getUserById(1L);
        itemRepository.findById(1L);
        itemService.updateItem(1L, 1L, ItemMapper.mapTo(updatedItem, new ArrayList<>()));
        updatedItem.setId(1L);
        updatedItem.setOwner(owner.getId());
        assertEquals(itemRepository.findById(item.getId()).orElse(null).getDescription(),
                updatedItem.getDescription());
        assertEquals(itemRepository.findById(item.getId()).orElse(null).getName(),
                updatedItem.getName());
    }

    @Test
    void shouldGetItemById() {
        ItemDto item1 = itemService.createItem(item.getOwner(), ItemMapper.mapTo(item, new ArrayList<>()));
        assertEquals(itemCommentDto.getId(), itemService.getItemById(item1.getId(), owner.getId()).getId());
    }

    @Test
    void shouldGetAllUsersItems() {
        assertEquals(List.of(itemCommentDto).get(0).getId(),
                itemService.getUsersItems(item.getOwner()).get(0).getId());
    }

    @Test
    void emptyTextSearchTest() {
        assertEquals(new ArrayList<>(),
                itemService.searchItemsByText(" "));
    }

    @Test
    void shouldFindItemByText() {
        assertEquals(List.of(ItemMapper.mapTo(item, new ArrayList<>())).get(0).getId(),
                itemService.searchItemsByText("кО").get(0).getId());
    }

    @Test
    void shouldFailCreatingItemEmptyName() {
        Item itemNoName = new Item(1L, "", "Надувная лодка", true,
                owner.getId(), null);
        assertThrows(ValidationException.class, () -> itemService.createItem(itemNoName.getOwner(),
                ItemMapper.mapTo(itemNoName, new ArrayList<>())));
    }

    @Test
    void shouldFailCreatingItemEmptyDescription() {
        Item itemNoDescr = new Item(1L, "Лодка", "", true, owner.getId(), null);
        assertThrows(ValidationException.class, () -> itemService.createItem(itemNoDescr.getOwner(),
                ItemMapper.mapTo(itemNoDescr, new ArrayList<>())));
    }

    @Test
    void shouldFailCreatingItemNullAvailability() {
        Item itemNoAvailable = new Item(1L, "Куртка", "Зимняя", null,
                owner.getId(), null);
        assertThrows(ValidationException.class, () -> itemService.createItem(itemNoAvailable.getOwner(),
                ItemMapper.mapTo(itemNoAvailable, new ArrayList<>())));
    }

    @Test
    void shouldCreateComment() throws InterruptedException {
        BookingDto bookingDto = BookingDto.builder()
                .start(LocalDateTime.now().plusSeconds(1))
                .end(LocalDateTime.now().plusSeconds(2))
                .itemId(item.getId())
                .build();
        Booking booking = BookingMapper.mapFrom(bookingService.createBooking(bookingDto, user2.getId()), user2, item);
        bookingService.updateBooking(owner.getId(), booking.getId(), true);
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Это текст комментария");
        Thread.sleep(10000);
        CommentDto comment = itemService.postComment(user2.getId(), item.getId(), commentDto);
        assertEquals(commentRepository.findById(comment.getId()).orElse(null).getText(), comment.getText());
    }
}
