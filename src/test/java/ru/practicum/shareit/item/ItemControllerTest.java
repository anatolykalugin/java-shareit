package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemController.class)
@AutoConfigureMockMvc
public class ItemControllerTest {

    @MockBean
    private ItemService itemService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    private final UserDto userDto1 = new UserDto(1L, "Ruslan", "ruslan@mail.ru");
    private final ItemDto itemDto1 = new ItemDto(1L, "Macbook", "Fast", true,
            1L, new ArrayList<>(), null);

    private final ItemWithBookingsDto itemDtoWithBooking = new ItemWithBookingsDto(2L, "Another Macbook",
            "Even faster", true, null, null, null);
    private final CommentDto commentDto1 = new CommentDto(1L, "What a fast macbook!", 1L,
            userDto1.getName(), LocalDateTime.now());

    @Test
    void shouldCreateItem() throws Exception {
        when(itemService.createItem(anyLong(), any()))
                .thenReturn(itemDto1);
        mockMvc.perform(post("/items")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemDto1))
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userDto1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto1.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto1.getName()), String.class))
                .andExpect(jsonPath("$.description", is(itemDto1.getDescription()), String.class))
                .andExpect(jsonPath("$.available", is(itemDto1.getAvailable()), Boolean.class));
        verify(itemService, times(1))
                .createItem(anyLong(), any());
    }

    @Test
    void shouldUpdateItem() throws Exception {
        when(itemService.updateItem(anyLong(), anyLong(), any()))
                .thenReturn(itemDto1);
        mockMvc.perform(patch("/items/" + itemDto1.getId())
                        .content(mapper.writeValueAsString(itemDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userDto1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto1.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto1.getName()), String.class))
                .andExpect(jsonPath("$.description", is(itemDto1.getDescription()), String.class))
                .andExpect(jsonPath("$.available", is(itemDto1.getAvailable()), Boolean.class));
        verify(itemService, times(1))
                .updateItem(anyLong(), anyLong(), any());
    }

    @Test
    void shouldGetItemById() throws Exception {
        when(itemService.getItemById(anyLong(), anyLong()))
                .thenReturn(itemDtoWithBooking);
        mockMvc.perform(get("/items/" + itemDto1.getId())
                        .header("X-Sharer-User-Id", userDto1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDtoWithBooking.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDtoWithBooking.getName()), String.class))
                .andExpect(jsonPath("$.description", is(itemDtoWithBooking.getDescription()), String.class))
                .andExpect(jsonPath("$.available", is(itemDtoWithBooking.getAvailable()), Boolean.class));
        verify(itemService, times(1))
                .getItemById(itemDto1.getId(), userDto1.getId());
    }

    @Test
    void shouldGetAllUsersItems() throws Exception {
        when(itemService.getUsersItems(anyLong()))
                .thenReturn(Collections.emptyList());
        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userDto1.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
        verify(itemService, times(1))
                .getUsersItems(anyLong());
    }

    @Test
    void shouldFindByText() throws Exception {
        when(itemService.searchItemsByText(anyString()))
                .thenReturn(List.of(itemDto1));
        mockMvc.perform(get("/items/search").param("text", "item1")
                        .content(mapper.writeValueAsString(itemDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userDto1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemDto1.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemDto1.getName()), String.class));
        verify(itemService, times(1))
                .searchItemsByText(anyString());
    }

    @Test
    void shouldPostComment() throws Exception {
        when(itemService.postComment(anyLong(), anyLong(), any()))
                .thenReturn(commentDto1);
        mockMvc.perform(post("/items/{id}/comment", itemDto1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(commentDto1))
                        .header("X-Sharer-User-Id", userDto1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto1.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDto1.getText())))
                .andExpect(jsonPath("$.authorName", is(userDto1.getName())));
    }

    @Test
    void shouldDeleteItem() throws Exception {
        mockMvc.perform(delete("/items/" + itemDto1.getId())
                        .content(mapper.writeValueAsString(itemDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(itemService, times(1))
                .deleteItemById(itemDto1.getId());
    }
}