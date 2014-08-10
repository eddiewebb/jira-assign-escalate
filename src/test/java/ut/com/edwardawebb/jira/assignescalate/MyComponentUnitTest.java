package ut.com.edwardawebb.jira.assignescalate;

import org.junit.Test;
import com.edwardawebb.jira.assignescalate.MyPluginComponent;
import com.edwardawebb.jira.assignescalate.MyPluginComponentImpl;

import static org.junit.Assert.assertEquals;

public class MyComponentUnitTest
{
    @Test
    public void testMyName()
    {
        MyPluginComponent component = new MyPluginComponentImpl(null);
        assertEquals("names do not match!", "myComponent",component.getName());
    }
}