package ut.com.edwardawebb.jira.assignescalate;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.edwardawebb.jira.assignescalate.MyPluginComponent;
import com.edwardawebb.jira.assignescalate.MyPluginComponentImpl;

public class MyComponentUnitTest
{
    @Test
    public void testMyName()
    {
        MyPluginComponent component = new MyPluginComponentImpl(null);
        assertEquals("names do not match!", "myComponent",component.getName());
    }
}