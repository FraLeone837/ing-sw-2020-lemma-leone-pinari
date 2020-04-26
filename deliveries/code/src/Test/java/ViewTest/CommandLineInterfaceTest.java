package ViewTest;

import Model.Index;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertEquals;

public class CommandLineInterfaceTest {
    //CommandLineInterface cli;
    @Before
    public void setUp() throws Exception{
        //cli = new CommandLineInterface();
    }

    @Test
    public void testCorrespondingIndex(){
        Random rand = new Random();
        int x = rand.nextInt(5);
        int y = rand.nextInt(5);
        char first = (char) (x+97);
        String input = Character.toString(first) + y;
        System.out.println(input);
        Index index = new Index(x, y, 0);
        //assertEquals(index.getX(), cli.correspondingIndex(input).getX());
        //assertEquals(index.getY(), cli.correspondingIndex(input).getY());
    }
}
