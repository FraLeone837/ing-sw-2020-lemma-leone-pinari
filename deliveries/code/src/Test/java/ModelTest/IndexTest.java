package ModelTest;

import Model.Index;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.*;

public class IndexTest {
    private Index index;
    private int x;
    private int y;
    private int z;
    @Before
    public void setUp() throws Exception{
        Random rand = new Random();
        this.x = rand.nextInt(5) + 1;
        this.y = rand.nextInt(5) + 1;
        this.z = rand.nextInt(4) + 1;
        index = new Index(x, y, z);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testGetX_sameX_expectedEquals(){
        assertEquals(x, index.getX());
    }

    @Test
    public void testGetX_differentX_expectedNotEquals(){
        Random rand = new Random();
        int notX;
        do{
            notX = rand.nextInt(5) + 1;
        } while(notX==x);
        assertNotEquals(notX, index.getX());
    }

    @Test
    public void testGetY_sameY_expectedEquals(){
        assertEquals(y, index.getY());
    }

    @Test
    public void testGetY_differentY_expectedNotEquals(){
        Random rand = new Random();
        int notY;
        do{
            notY = rand.nextInt(5) + 1;
        } while(notY==y);
        assertNotEquals(notY, index.getY());
    }

    @Test
    public void testGetZ_sameZ_expectedEquals(){
        assertEquals(z, index.getZ());
    }

    @Test
    public void testGetZ_differentZ_expectedNotEquals(){
        Random rand = new Random();
        int notZ;
        do{
            notZ = rand.nextInt(4) + 1;
        } while(notZ==z);
        assertNotEquals(notZ, index.getZ());
    }
    @Test
    public void testEquals_sameIndexes_expectedTrue(){
        Index other = new Index(index.getX(),index.getY(),index.getZ());

//        assertTrue(other.equals(index));a
        assertEquals(other,index);
    }
    @Test
    public void testEquals_differentIndexes_expectedFalse(){
        Index other = new Index(x,y+1,z);
        assertFalse(other.equals(index));
    }
    @Test
    public void testEquals_differentClasses_expectedFalse(){
        Object other = new String();
        assertFalse(other.equals(index));
    }
    @Test
    public void testToString(){
        assertFalse(index.toString().isEmpty());
    }

}
