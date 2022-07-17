package IntList;

import org.junit.Test;

import static org.junit.Assert.*;

public class exeTest {
    @Test
    public void exeTest1(){
        int []testarr={5,4,0,3,1,6,2};
        int res=exe.arrayNesting(testarr);
        assertEquals(4,res);
    }
}
