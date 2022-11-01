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
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.ItemRequest;
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
    private final User user3 = new User(3L, "Igor2", "igor2@mail.ru");
    private final Item item = new Item(1L, "Пиво", "Козел светлый", true,
            1L, null);
    private final Item item2 = new Item(2L, "Пиво 2", "Сиквел", true,
            2L, null);
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
        userRepository.save(user3);
    }

    @Test
    void shouldCreateItem() {
        ItemDto item1 = itemService.createItem(item.getOwner(), ItemMapper.mapTo(item, new ArrayList<>()));
        assertEquals(itemRepository.findById(item.getId()).orElse(null).getId(), item1.getId());
    }

    @Test
    void shouldCreateSecondItem() {
        ItemDto secondItem = itemService.createItem(item.getOwner(), ItemMapper.mapTo(item2, new ArrayList<>()));
        assertEquals(itemRepository.findById(item2.getId()).orElse(null).getId(), secondItem.getId());
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
    void shouldGetSimpleItemById() {
        assertEquals(2L, itemService.getItemById(user3.getId(), 2L).getId());
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
        Item itemNotAvailable = new Item(1L, "Куртка", "Зимняя", null,
                owner.getId(), null);
        assertThrows(ValidationException.class, () -> itemService.createItem(itemNotAvailable.getOwner(),
                ItemMapper.mapTo(itemNotAvailable, new ArrayList<>())));
    }

    @Test
    void shouldFailCreatingItemWrongUser() {
        Item itemWrongUser = new Item(1L, "Куртка", "Зимняя", null,
                20L, new ItemRequest(10L, "qwerty",
                new User(20L, "Qerty", "a@a.ru"), LocalDateTime.now()));
        assertThrows(NotFoundException.class, () -> itemService.createItem(itemWrongUser.getOwner(),
                ItemMapper.mapTo(itemWrongUser, new ArrayList<>())));
    }

    @Test
    void shouldFailCreatingItemWrongRequest() {
        Item itemWrongRequest = new Item(4L, "Куртка", "Зимняя", true,
                owner.getId(), new ItemRequest(20L, "qwerty", user3, LocalDateTime.now()));
        assertThrows(NotFoundException.class, () -> itemService.createItem(itemWrongRequest.getOwner(),
                ItemMapper.mapTo(itemWrongRequest, new ArrayList<>())));
    }

    @Test
    void shouldPostComment() throws InterruptedException {
        itemService.createItem(item.getOwner(), ItemMapper.mapTo(item, new ArrayList<>()));
        BookingDto bookingDto = BookingDto.builder()
                .start(LocalDateTime.now().plusSeconds(1))
                .end(LocalDateTime.now().plusSeconds(2))
                .itemId(item.getId())
                .build();
        Booking booking = BookingMapper.mapFrom(bookingService.createBooking(bookingDto, user2.getId()), user2, item);
        bookingService.updateBooking(booking.getId(), owner.getId(), true);
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Awesome product!");
        Thread.sleep(10000);
        CommentDto comment = itemService.postComment(
                user2.getId(),
                item.getId(),
                commentDto
        );
        assertEquals(commentRepository.findById(comment.getId()).orElse(null).getText(), comment.getText());
    }

    @Test
    void shouldFailDeleteItemById() {
        assertThrows(NotFoundException.class, () -> itemService.deleteItemById(6L));
    }
}
