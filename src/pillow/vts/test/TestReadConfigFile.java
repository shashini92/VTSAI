package pillow.vts.test;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

import pillow.vts.aiengine.dataaccess.DataAccess;

public class TestReadConfigFile {
	
	String host="localhost";
	String port="3306";
	String username="root";
	
	DataAccess dataAccess=new DataAccess();
	
	@Test
	public void test() {
		String [] configData=dataAccess.readConfigFile();
		
		assertEquals(host,configData[0]);		
		assertEquals(port,configData[1]);		
		assertEquals(username,configData[2]);
	}

}
