package pillow.vts.aiengine.dataaccess;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import pillow.vts.aiengine.bean.DataSet;
import pillow.vts.aiengine.bean.Weight;

public class DataAccess {

	String host;
	String port;
	String username;
	String password;
		
	public  DataAccess(){
		
		String[] configData=readConfigFile();
		this.host=configData[0];
		this.port=configData[1];
		this.username=configData[2];
				
		if("no".equals(configData[3])){
			this.password="";
		}else{
			this.password=configData[3];
		}
	}
	
	private Connection getDBConnection(){
		 
		Connection con=null;
						
		try{
			
			Class.forName("com.mysql.jdbc.Driver");
				    
			con = DriverManager.getConnection("jdbc:mysql://"+host+":"+port+"/vtsai?"
		              + "user="+username+"&password="+password+"&autoReconnect=true&useSSL=false");
		}
		catch(Exception e){
			System.out.println(e.toString());
		}
		
		return con;
	}
	
	private void close(Connection con){
		 						
		try{
			con.close();
		}
		catch(Exception e){
			System.out.println(e.toString());
		}
	}
			
	public String[] readConfigFile(){
		
		String[] configData=null;
		try{
			FileReader file=new FileReader("AIConfig.config");
			FileReader fileLc=new FileReader("AIConfig.config");
			BufferedReader dataReader=new BufferedReader(file);
			BufferedReader lineCountReader=new BufferedReader(fileLc);
						
			int lineCount=getNumberOfLines(lineCountReader);
			String[] data=new String[lineCount];
			
			for(int line=0;line<lineCount;line++){
				data[line]=dataReader.readLine();
			}
			
			dataReader.close();
			lineCountReader.close();
			
			configData=parseData(data);
			
		}catch(IOException e){
			System.out.println(e.toString());
		}
		return configData;
	}
	
	private int getNumberOfLines(BufferedReader reader) throws IOException{
		
		int lineCount=0;
		while(reader.readLine()!=null){
			lineCount++;
		}
		
		return lineCount;
	}
	
	private String[] parseData(String[] data){
		
		String[] configData=new String[data.length];
		
		for(int i=0; i<data.length; i++){
			String[] lineData=data[i].trim().split(":");			
			configData[i]=lineData[1];
		}
		
		return configData;
	}
	
	public DataSet[] getInputData(){
		
		DataSet[] dataSetArr=null;
		Connection con=getDBConnection();
		try{
						
			PreparedStatement statment=con.prepareStatement("SELECT * FROM inputdata");
			ResultSet rs=statment.executeQuery();
			
			int rowcount = 0;
			if (rs.last()) {
			  rowcount = rs.getRow();
			  rs.beforeFirst();
			}
			
			dataSetArr=new DataSet[rowcount];
			int i=0;
			
			while (rs.next()) {
				DataSet dataSet=new DataSet();
				dataSet.setDefinedSpeed(rs.getDouble(2));
				dataSet.setNotifiedSpeed(rs.getDouble(3));
				dataSet.setDateOfWeek(rs.getDouble(4));
				dataSet.setTime(rs.getDouble(5));
				dataSetArr[i]=dataSet;
				i++;
			}
			
		}catch(Exception e){
			System.out.println(e.toString());
		}
		finally {
			close(con);
		}
		return dataSetArr;
	}
	
	public void insertWeights(Weight[] weights){
		
		Connection con=getDBConnection();
		try{
					
			for(Weight weight:weights){
				PreparedStatement statment=con.prepareStatement("INSERT INTO WEIGHT VALUES(?,?)");
				statment.setString(1, weight.getName());
				statment.setDouble(2, weight.getValue());
				statment.executeUpdate();
			}
				
		}catch(Exception e){
			System.out.println(e.toString());
		}
		finally {
			close(con);
		}
	}
	
	public void deleteWeights(){
		
		Connection con=getDBConnection();
		try{					
			PreparedStatement statment=con.prepareStatement("DELETE FROM WEIGHT");
			statment.executeUpdate();	
				
		}catch(Exception e){
			System.out.println(e.toString());
		}
		finally {
			close(con);
		}
	}
	
	public Weight[] getWeights(){
		
		Weight[] weights=null;
		Connection con=getDBConnection();
		try{
						
			PreparedStatement statment=con.prepareStatement("SELECT * FROM WEIGHT");
			ResultSet rs=statment.executeQuery();
			
			int rowcount = 0;
			if (rs.last()) {
			  rowcount = rs.getRow();
			  rs.beforeFirst();
			}
			
			weights=new Weight[rowcount];
			
			int i=0;					
			while (rs.next()) {
				Weight weight=new Weight();
				weight.setName(rs.getString(1));
				weight.setValue(rs.getDouble(2));
				weights[i]=weight;
				i++;
			}
			
		}catch(Exception e){
			System.out.println(e.toString());
		}
		finally {
			close(con);
		}
		return weights;
	}
}
