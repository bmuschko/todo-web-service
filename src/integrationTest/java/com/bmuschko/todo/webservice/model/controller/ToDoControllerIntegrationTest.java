package com.bmuschko.todo.webservice.model.controller;

import com.bmuschko.todo.webservice.controller.ToDoController;
import com.bmuschko.todo.webservice.model.ToDoItem;
import com.bmuschko.todo.webservice.repository.ToDoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ToDoController.class)
public class ToDoControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ToDoRepository toDoRepository;

    @Test
    @DisplayName("can retrieve all items")
    void retrieveAllItems() throws Exception {
        ToDoItem toDoItem1 = createToDoItem(123L, "Buy milk", true);
        ToDoItem toDoItem2 = createToDoItem(456L, "Wash dishes", false);
        ToDoItem toDoItem3 = createToDoItem(789L, "Go shopping", true);
        List<ToDoItem> items = new ArrayList<>();
        items.add(toDoItem1);
        items.add(toDoItem2);
        items.add(toDoItem3);
        given(toDoRepository.findAll()).willReturn(items);
        verifyNoMoreInteractions(toDoRepository);
        mvc.perform(get("/todos"))
                .andExpect(content().json("[{\"id\":123,\"name\":\"Buy milk\",\"completed\":true},{\"id\":456,\"name\":\"Wash dishes\",\"completed\":false},{\"id\":789,\"name\":\"Go shopping\",\"completed\":true}]"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("can retrieve a single existing item")
    void retrieveExistingItem() throws Exception {
        Optional<ToDoItem> toDoItem = Optional.of(createToDoItem(123L, "Buy milk", true));
        given(toDoRepository.findById(123L)).willReturn(toDoItem);
        verifyNoMoreInteractions(toDoRepository);
        mvc.perform(get("/todos/123"))
                .andExpect(content().json("{\"id\":123,\"name\":\"Buy milk\",\"completed\":true}"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("can delete existing item")
    void deleteItem() throws Exception {
        mvc.perform(delete("/todos/123"))
                .andExpect(status().isOk());
        verify(toDoRepository, times(1)).deleteById(123L);
    }

    @Test
    @DisplayName("can create new item")
    void createItem() throws Exception {
        ToDoItem toDoItemBeforeSave = createToDoItem(null, "Buy milk", true);
        ToDoItem toDoItemAfterSave = createToDoItem(1L, "Buy milk", true);
        given(toDoRepository.save(toDoItemBeforeSave)).willReturn(toDoItemAfterSave);
        verifyNoMoreInteractions(toDoRepository);
        mvc.perform(post("/todos").content("{\"name\":\"Buy milk\",\"completed\":true}").contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("can update existing item")
    void updateExistingItem() throws Exception {
        Optional<ToDoItem> toDoItem = Optional.of(createToDoItem(123L, "Buy milk", true));
        given(toDoRepository.findById(123L)).willReturn(toDoItem);
        given(toDoRepository.save(toDoItem.get())).willReturn(toDoItem.get());
        verifyNoMoreInteractions(toDoRepository);
        mvc.perform(put("/todos/123").content("{\"name\":\"Read book\",\"completed\":true}").contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("can handle updating non-existing item")
    void updateNonExistingItem() throws Exception {
        given(toDoRepository.findById(123L)).willReturn(Optional.empty());
        verifyNoMoreInteractions(toDoRepository);
        mvc.perform(put("/todos/123").content("{\"name\":\"Read book\",\"completed\":true}").contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isNotFound());
    }

    private static ToDoItem createToDoItem(Long id, String name, boolean completed) {
        ToDoItem toDoItem = new ToDoItem();
        toDoItem.setId(id);
        toDoItem.setName(name);
        toDoItem.setCompleted(completed);
        return toDoItem;
    }
}