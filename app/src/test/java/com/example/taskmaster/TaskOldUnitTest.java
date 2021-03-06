package com.example.taskmaster;


import org.junit.Test;

import static org.junit.Assert.*;


// Unit Test For Task Class
public class TaskOldUnitTest {


    // general tester - there is not method inside the class
    @Test
    public void testTaskClass() {
        Task task = new Task("This is the title", "This is a description", "new");
        assertEquals("There was an error in the getter of the class", task.getTitle(), "This is the title");
        assertEquals("There was an error in the getter of the class", task.getDesc(),
                "This is a description");
        task.setTitle("Another Title");
        assertEquals("There was an error in the setter method", task.getTitle(), "Another Title");
    }
}
