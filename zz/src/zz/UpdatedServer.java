package zz;
import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.mysql.jdbc.Statement;
import java.util.Arrays;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Vector;

// the server that can be run as a console
public class Server {
	 class abc
	{
		String title;
		String fname;
		String lname;
		String releasedyear;
		abc(String title,String fname,String lname,String releasedyear)
		{
			this.title=title;
			this.fname=fname;
			this.lname=lname;
			this.releasedyear=releasedyear;
		}
	}
	// a unique ID for each connection
	private static int uniqueId;
	// an ArrayList to keep the list of the Client
	private ArrayList<ClientThread> al;
	// to display time
	private SimpleDateFormat sdf;
	// the port number to listen for connection
	private int port;
	// to check if server is running
	private boolean keepGoing;
	// notification
	private String notif = " *** ";
	public ArrayList<abc> rachit=new ArrayList<abc>();
	
	// DECLARING CONTENT OF DYNAMIC ROUND ROBIN
	  static Process p[]; // objects of Process class
	  static ArrayList<Process> p1 = new ArrayList<Process>(0); // hold the current process in the ready queue
	  static int index=0, timeCount, mainCount;

	
	//constructor that receive the port to listen to for connection as parameter
	
	public Server(int port) {
		// the port
		this.port = port;
		// to display hh:mm:ss
		sdf = new SimpleDateFormat("HH:mm:ss");
		// an ArrayList to keep the list of the Client
		al = new ArrayList<ClientThread>();
	}
	
	public void start() {
		keepGoing = true;
		//create socket server and wait for connection requests 
		try 
		{
			// the socket used by the server
			ServerSocket serverSocket = new ServerSocket(port);

			// infinite loop to wait for connections ( till server is active )
			while(keepGoing) 
			{
				System.out.println();
				//display("Server waiting for Clients on port " + port + ".");
				System.out.println();
				// accept connection if requested from client
				Socket socket = serverSocket.accept();
				// break if server stoped
				if(!keepGoing)
					break;
				// if client is connected, create its thread
				ClientThread t = new ClientThread(socket);
				//add this client to arraylist
				al.add(t);
				
				t.start();
			}
			// try to stop the server
			try {
				serverSocket.close();
				for(int i = 0; i < al.size(); ++i) {
					ClientThread tc = al.get(i);
					try {
					// close all data streams and socket
					tc.sInput.close();
					tc.sOutput.close();
					tc.socket.close();
					}
					catch(IOException ioE) {
					}
				}
			}
			catch(Exception e) {
				display("Exception closing the server and clients: " + e);
			}
		}
		catch (IOException e) {
            String msg = sdf.format(new Date()) + " Exception on new ServerSocket: " + e + "\n";
			display(msg);
		}
	}
	
	// to stop the server
	protected void stop() {
		keepGoing = false;
		try {
			new Socket("localhost", port);
		}
		catch(Exception e) {
		}
	}
	
	// Display an event to the console
	private void display(String msg) {
		String time = sdf.format(new Date()) + " " + msg;
		System.out.println(time);
	}
	
	// to broadcast a message to all Clients
	private synchronized boolean broadcast(String message) {
		// add timestamp to the message
		String time = sdf.format(new Date());
	   //System.out.println("first broadcast print "+message);
		//System.out.println();
	
		String[] w = message.split(" ",3);
		
		boolean isPrivate = false;
		if(w[1].charAt(0)=='@') 
			isPrivate=true;
		
		
		// if private message, send message to mentioned username only
		if(isPrivate==true)
		{
			String tocheck=w[1].substring(1, w[1].length());
			
			message=w[0]+w[2];
			String messageLf = time + " " + message + "\n";
			
			boolean found=false;
			// we loop in reverse order to find the mentioned username
			for(int y=al.size(); --y>=0;)
			{
				ClientThread ct1=al.get(y);
				String check=ct1.getUsername();
				if(check.equals(tocheck))
				{
					// try to write to the Client if it fails remove it from the list
					if(!ct1.writeMsg(messageLf)) {
						al.remove(y);
						//display("Disconnected Client " + ct1.username + " removed from list.");
					}
					// username found and delivered the message
					found=true;
					break;
				}
				
				
				
			}
			// mentioned user not found, return false
			if(found!=true)
			{
				return false; 
			}
		}
		// if message is a broadcast message
		else
		{
			 Connection connection=null;
		     String databaseName="";
		     String url="jdbc:mysql://localhost:3306/studentdatabase?characterEncoding=latin1";
		     String username="root";
		     String password="Rashmiraj@0326";
		     String messageLf = time + " " + message + "\n";
				// display message
			String st[]=message.split(":");  
			try
			{
		Class.forName("com.mysql.jdbc.Driver").getDeclaredConstructor().newInstance();
	    connection=DriverManager.getConnection(url,username,password);
	    System.out.println("database connected");
	    Statement mystatement = (Statement) connection.createStatement();
	    ResultSet codespeedy=mystatement.executeQuery("select * from student");
	    System.out.println();
	    if(st.length>1)
	    {
	    while(codespeedy.next())
	    {
	    	//System.out.println(st[1].getClass().getName()+" "+codespeedy.getString("title").getClass().getName());
	    	String res1=st[1].trim();
	    	String res2=codespeedy.getString("title");
	    	if(res1.equals(res2))
	    	{
	      
	    	System.out.println("The title is "+codespeedy.getString("title"));
	    	//System.out.println("The first name is "+codespeedy.getString("author_fname"));
	    	//System.out.println("The last name is "+codespeedy.getString("author_lname"));
	    	//System.out.println("The released year is "+codespeedy.getString("released_year"));
	    	//System.out.println("END");
	    	abc ob=new abc(codespeedy.getString("title"),codespeedy.getString("author_fname"),codespeedy.getString("author_lname"),codespeedy.getString("released_year"));
	    	rachit.add(ob);
	    	
	    	}
	    }
			}
			}
			catch (Exception e){
			     System.out.println(e);
			    }
			System.out.println();
			
			
			
			System.out.print(messageLf);
			
			// we loop in reverse order in case we would have to remove a Client
			// because it has disconnected
			for(int i = al.size(); --i >= 0;) {
				ClientThread ct = al.get(i);
				// try to write to the Client if it fails remove it from the list
				//System.out.println("inside for loop of broadcast method for client "+ct.username);
				//System.out.println("just for testing "+ct.writeMsg("Hello world"));
				
			}
		}
		return true;
		
		
	}

	// if client sent LOGOUT message to exit
	synchronized void remove(int id) {
		
		String disconnectedClient = "";
		// scan the array list until we found the Id
		for(int i = 0; i < al.size(); ++i) {
			ClientThread ct = al.get(i);
			// if found remove it
			if(ct.id == id) {
				disconnectedClient = ct.getUsername();
				al.remove(i);
				break;
			}
		}
		System.out.println(" Here Process Scheduling will take place  ");
		//broadcast(notif + disconnectedClient + " has left." + notif);
		
		
		
		// writing the dynamic round robin coding: 
		
		Scanner sc = new Scanner(System.in);
	    //int sum = 0;
	    int ganttTime[];
	    int ganttProcNo[];
	    int ganttCount = 0;
	    int ganttTimeCount = 0;
	    int maxBurstTime;
	    float timeQuantum = 0;
	    float avgWaitingTime = 0;
	    float avgTurnaroundTime = 0;
	    boolean flag = true;  // flag used to check the cases and change the timeQuantum accordingly
	    Vector<Integer> v = new Vector<Integer>();
	    ArrayList<Process> p2;
	    
	    
	    System.out.println("\nProvide the arrival and burst time ");
	    int noOfProc = 3;

	    ganttTime = new int[noOfProc];
	    ganttProcNo = new int[noOfProc];

	    p = new Process[noOfProc];
	    for(int i=0; i<noOfProc; i++) { // initialising the Process objects
	      p[i] = new Process(i);
	    }

	    // setting the various attributes using getters and setters
	    for(int i=0; i<noOfProc; i++) {
	      System.out.println("\nEnter the Arrival Time and Burst Time of "+i+" process: ");
	      p[i].setArrivalTime(sc.nextInt());
	      p[i].setBurstTime(sc.nextInt());
	      //sum += p[i].getArrivalTime();
	      v.add(i+1); // adding all process numbers to vector
	    }

	    Arrays.sort(p); // sorting the processes based on arrival timeCount
	    mainCount = p[0].getArrivalTime();  // setting mainCount to the value of arrival time of the process that came first

	    for(timeCount=0; ; ) {
	      checkForProcess(timeCount, index); // check for the process that entered the system until the current time

	      p2 = (ArrayList<Process>)p1.clone();
	      p2.sort(new Process()); // sorting process base on their burst times

	      if(flag) {
	        maxBurstTime = p2.get(p2.size()-1).getBurstTime();
	        //System.out.println(maxBurstTime);
	        timeQuantum = 0.8f * maxBurstTime;
	        //System.out.println("\n\nTime Quantum now: " + timeQuantum);
	      }
	      
	      
	      for(int i=0; i<p1.size(); i++) {
	          // if the process's burst time less that current timeQuantum and not yet finished
	          if(p1.get(i).getBurstTime() <= timeQuantum && !p1.get(i).isFinish()) {
	            //System.out.print("Process finished: "+(p1.get(i).getProcessNo()+1)+"\n");
	            p1.get(i).setWaitingTime(mainCount-p1.get(i).getArrivalTime());
	            timeCount += p1.get(i).getBurstTime();
	            mainCount += p1.get(i).getBurstTime();
	            ganttProcNo[ganttCount] = p1.get(i).getProcessNo();
	            ganttTimeCount += p1.get(i).getBurstTime();
	            ganttTime[ganttCount++] = ganttTimeCount;
	            //System.out.println(ganttTime);
	            p1.get(i).setFinish(true);
	            p1.get(i).setTurnaroundTime(mainCount-p1.get(i).getArrivalTime());
	            v.removeElement(i+1);
	          }
	        }

	        if(p1.size() == noOfProc) {
	          timeQuantum = p2.get(p2.size()-1).getBurstTime();
	          // System.out.println("Time Quantum now: " + timeQuantum);
	          flag = false;
	        }

	        if(v.isEmpty()) {
	          break;
	        }
	      }

	      System.out.println("\nGantt Chart: \n");
	      for(int i=0; i<noOfProc; i++) {
	        System.out.print("|\tP"+ganttProcNo[i]+"\t|");
	      }

	      System.out.print("\n" + p1.get(0).getArrivalTime() + "\t");
	      for(int i=0; i<noOfProc; i++) {
	        System.out.print("\t"+ganttTime[i]+"\t");
	      }

	      System.out.println("\n\nProcess\t\tArrival Time\tBurst Time\tWaiting Time\tTurnaround Time");
	      for(int i=0; i<noOfProc; i++) {
	        System.out.println("   "+p[i].getProcessNo()+"\t\t   "+p[i].getArrivalTime()+"\t\t   "+p[i].getBurstTime()+"\t\t   "+p[i].getWaitingTime()+"\t\t  "+p[i].getTurnaroundTime());
	        avgWaitingTime += p[i].getWaitingTime();
	        avgTurnaroundTime += p[i].getTurnaroundTime();
	      }

	      avgWaitingTime /= noOfProc;
	      avgTurnaroundTime /= noOfProc;
	      System.out.println("\nAverage Waiting Time: "+avgWaitingTime);
	      System.out.println("Average Turnaround Time: "+avgTurnaroundTime);

	    
	      for(int i=0; i<noOfProc; i++) {
		        //System.out.print("|\tP"+ganttProcNo[i]+"\t|");
		        abc ob=(rachit.get(ganttProcNo[i]));
		        System.out.println("Process Number is "+ganttProcNo[i]);
		        System.out.println(ob.title);
		        System.out.println(ob.fname);
		        System.out.println(ob.lname);
		        System.out.println(ob.releasedyear);
		        System.out.println("\n");
		      }
		
		// writing the dynamic round robin coding: 
		
	}
	
	// WRITING ROUND ROBIN HEPLER FUNCTION
	
	
	public  void checkForProcess(int time, int ind)
	  {
	    // this method used to add the processes that arrive till current time to p1 list
	    for(int j=ind; j<p.length; j++)
	    {
	      if(p[j].getArrivalTime() <= time)
	      {
	        p1.add(p[j]);
	        index++;
	      }
	    }
	    if(index > ind)
	      return;
	    timeCount++;
	    return;
	  }
	
	
	//WRITING ROUND ROBIN HELPER FUNCTION
	
	/*
	 *  To run as a console application
	 * > java Server
	 * > java Server portNumber
	 * If the port number is not specified 1500 is used
	 */ 
	public static void main(String[] args) {
		// start server on port 1500 unless a PortNumber is specified 
		int portNumber = 1500;
		switch(args.length) {
			case 1:
				try {
					portNumber = Integer.parseInt(args[0]);
				}
				catch(Exception e) {
					System.out.println("Invalid port number.");
					System.out.println("Usage is: > java Server [portNumber]");
					return;
				}
			case 0:
				break;
			default:
				System.out.println("Usage is: > java Server [portNumber]");
				return;
				
		}
		// create a server object and start it
		Server server = new Server(portNumber);
		server.start();
	}

	// One instance of this thread will run for each client
	class ClientThread extends Thread {
		// the socket to get messages from client
		Socket socket;
		ObjectInputStream sInput;
		ObjectOutputStream sOutput;
		// my unique id (easier for deconnection)
		int id;
		// the Username of the Client
		String username;
		// message object to recieve message and its type
		ChatMessage cm;
		// timestamp
		String date;

		// Constructor
		ClientThread(Socket socket) {
			// a unique id
			id = ++uniqueId;
			this.socket = socket;
			//Creating both Data Stream
			System.out.println("Thread trying to create Object Input/Output Streams");
			try
			{
				sOutput = new ObjectOutputStream(socket.getOutputStream());
				sInput  = new ObjectInputStream(socket.getInputStream());
				// read the username
				//System.out.println("inside ClientThread class in server java file");
				username = (String) sInput.readObject();
				System.out.println();
				broadcast(notif + username + " has joined." + notif);
				System.out.println();
			}
			catch (IOException e) {
				display("Exception creating new Input/output Streams: " + e);
				return;
			}
			catch (ClassNotFoundException e) {
			}
            date = new Date().toString() + "\n";
		}
		
		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		// infinite loop to read and forward message
		public void run() {
			// to loop until LOGOUT
			boolean keepGoing = true;
			while(keepGoing) {
				// read a String (which is an object)
				try {
					cm = (ChatMessage) sInput.readObject();
				}
				catch (IOException e) {
					display(username + " Exception reading Streams: " + e);
					break;				
				}
				catch(ClassNotFoundException e2) {
					break;
				}
				// get the message from the ChatMessage object received
				String message = cm.getMessage();

				// different actions based on type message
				switch(cm.getType()) {

				case ChatMessage.MESSAGE:
					boolean confirmation =  broadcast(username + ": " + message);
					if(confirmation==false){
						String msg = notif + "Sorry. No such user exists." + notif;
						writeMsg(msg);
					}
					break;
				case ChatMessage.LOGOUT:
					//display(username + " disconnected with a LOGOUT message.");
					keepGoing = false;
					break;
				case ChatMessage.WHOISIN:
					System.out.println();
					writeMsg("List of the users/clients connected at " + sdf.format(new Date()) + "\n");
					// send list of active clients
					System.out.println();
					for(int i = 0; i < al.size(); ++i) {
						ClientThread ct = al.get(i);
						writeMsg((i+1) + ") " + ct.username + " since " + ct.date);
					}
					break;
				}
			}
			// if out of the loop then disconnected and remove from client list
			remove(id);
			close();
		}
		
		// close everything
		private void close() {
			try {
				if(sOutput != null) sOutput.close();
			}
			catch(Exception e) {}
			try {
				if(sInput != null) sInput.close();
			}
			catch(Exception e) {};
			try {
				if(socket != null) socket.close();
			}
			catch (Exception e) {}
		}

		// write a String to the Client output stream
		private boolean writeMsg(String msg) {
			// if Client is still connected send the message to it
			if(!socket.isConnected()) {
				close();
				return false;
			}
			// write the message to the stream
			try {
				sOutput.writeObject(msg);
			}
			// if an error occurs, do not abort just inform the user
			catch(IOException e) {
				display(notif + "Error sending message to " + username + notif);
				display(e.toString());
			}
			return true;
		}
	}
}

