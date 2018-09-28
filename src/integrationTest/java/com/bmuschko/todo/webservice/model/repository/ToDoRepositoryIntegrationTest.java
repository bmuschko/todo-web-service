package com.bmuschko.todo.webservice.model.repository;

import com.bmuschko.todo.webservice.model.ToDoItem;
import com.bmuschko.todo.webservice.repository.ToDoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class ToDoRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ToDoRepository repository;

    @Test
    public void testCanFindSingleNonExistingToDoItem() {
        assertFalse(repository.findById(20000L).isPresent());
    }

    @Test
    public void testCanFindSingleExistingToDoItem() {
        ToDoItem toDoItem = persistToDoItem("Buy milk");
        assertNotNull(repository.findById(toDoItem.getId()));
    }

    @Test
    public void testCanGetMultipleExistingToDoItem() {
        ToDoItem toDoItem1 = persistToDoItem("Buy milk");
        ToDoItem toDoItem2 = persistToDoItem("Clean dishes");
        List<Long> ids = new ArrayList<>();
        ids.add(toDoItem1.getId());
        ids.add(toDoItem2.getId());
        assertEquals(repository.findAllById(ids).size(),2);
    }

    @Test
    public void testCanSaveNewToDoItem() {
        ToDoItem toDoItem = createToDoItem("Buy milk");
        assertNull(toDoItem.getId());
        repository.save(toDoItem);
        assertNotNull(toDoItem.getId());
    }

    @Test
    public void testCanDeleteExistingToDoItem() {
        ToDoItem toDoItem = persistToDoItem("Buy milk");
        assertNotNull(toDoItem.getId());
        repository.delete(toDoItem);
        assertFalse(repository.existsById(toDoItem.getId()));
    }

    @Test
    public void testCanUpdateExistingToDoItem() {
        ToDoItem toDoItem = persistToDoItem("Buy milk");
        assertNotNull(toDoItem.getId());
        toDoItem.setName("Clean dishes");
        repository.save(toDoItem);
        assertEquals(repository.findById(toDoItem.getId()).get().getName(), "Clean dishes");
    }

    private ToDoItem persistToDoItem(String name) {
        ToDoItem toDoItem = createToDoItem(name);
        entityManager.persist(toDoItem);
        return toDoItem;
    }

    private ToDoItem createToDoItem(String name) {
        ToDoItem toDoItem = new ToDoItem();
        toDoItem.setName(name);
        return toDoItem;
    }
}