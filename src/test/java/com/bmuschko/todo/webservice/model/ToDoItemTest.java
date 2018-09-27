package com.bmuschko.todo.webservice.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ToDoItemTest {

    private static final Long ITEM_ID = 123L;
    private static final String ITEM_NAME = "Buy milk";

    @Test
    void testStringRepresentation() {
        ToDoItem toDoItem = createToDoItem(ITEM_ID, ITEM_NAME, true);
        assertEquals("123: Buy milk [completed: true]", toDoItem.toString());
    }

    @Test
    void testCanCompare() {
        ToDoItem toDoItem1 = createToDoItem(ITEM_ID, ITEM_NAME, true);
        ToDoItem toDoItem2 = createToDoItem(ITEM_ID, ITEM_NAME, true);
        assertTrue(toDoItem1.equals(toDoItem2));

        toDoItem2.setName("Wash dishes");
        assertFalse(toDoItem1.equals(toDoItem2));
    }

    private static ToDoItem createToDoItem(Long id, String name, boolean completed) {
        ToDoItem toDoItem = new ToDoItem();
        toDoItem.setId(id);
        toDoItem.setName(name);
        toDoItem.setCompleted(completed);
        return toDoItem;
    }
}
