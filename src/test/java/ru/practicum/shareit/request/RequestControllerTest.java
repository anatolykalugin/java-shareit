package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDtoMapper;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
@AutoConfigureMockMvc
public class RequestControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ItemRequestService itemRequestService;
    private final User user = new User(1L, "Anton", "anton@mail.ru");
    private final ItemRequest itemRequest = new ItemRequest(1L, "New TV", user, LocalDateTime.now());
    private final Item item = new Item(1L, "TV", "Quite new", true,
            user.getId(), itemRequest);

    @Test
    void shouldCreateRequest() throws Exception {
        when(itemRequestService.createRequest(any(), anyLong()))
                .thenReturn(ItemRequestDtoMapper.mapTo(itemRequest, List.of(item)));
        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequest))
                        .header("X-Sharer-User-Id", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(notNullValue())))
                .andExpect(jsonPath("$.description", is(itemRequest.getDescription())));
    }

    @Test
    void shouldGetRequests() throws Exception {
        when(itemRequestService.getRequests(anyLong()))
                .thenReturn(List.of(ItemRequestDtoMapper.mapTo(itemRequest, List.of(item))));
        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(notNullValue())))
                .andExpect(jsonPath("$[0].description", is(itemRequest.getDescription())));
    }

    @Test
    void shouldGetOthersRequests() throws Exception {
        when(itemRequestService.getOthersRequests(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(ItemRequestDtoMapper.mapTo(itemRequest, List.of(item))));
        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(notNullValue())))
                .andExpect(jsonPath("$[0].description", is(itemRequest.getDescription())));
    }

    @Test
    void shouldGetRequestById() throws Exception {
        when(itemRequestService.getRequestById(anyLong(), anyLong()))
                .thenReturn(ItemRequestDtoMapper.mapTo(itemRequest, List.of(item)));
        mockMvc.perform(get("/requests/{id}", itemRequest.getId())
                        .header("X-Sharer-User-Id", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(notNullValue())))
                .andExpect(jsonPath("$.description", is(itemRequest.getDescription())));
    }

}
