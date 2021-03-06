package hw12;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

// TO DO: Task is currently an ordinary class.
// You will need to modify it to make it a task,
// so it can be given to an Executor thread pool.
//
class Task implements Runnable {
    private static final int A = constants.A;
    private static final int Z = constants.Z;
    private static final int numLetters = constants.numLetters;

    private Account[] accounts;
    private String transaction;
    private Cache[] cacheList;
    

    // TO DO: The sequential version of Task peeks at accounts
    // whenever it needs to get a value, and opens, updates, and closes
    // an account whenever it needs to set a value.  This won't work in
    // the parallel version.  Instead, you'll need to cache values
    // you've read and written, and then, after figuring out everything
    // you want to do, (1) open all accounts you need, for reading,
    // writing, or both, (2) verify all previously peeked-at values,
    // (3) perform all updates, and (4) close all opened accounts.

    public Task(Account[] allAccounts, String trans) {
        accounts = allAccounts;
        transaction = trans;
    }
    
    // TO DO: parseAccount currently returns a reference to an account.
    // You probably want to change it to return a reference to an
    // account *cache* instead.
    
	// requires: name != null
	// modifies: cacheList
	// effects: changes the cacheList depending on the input passed in
    private Cache parseAccount(String name) {
        int accountNum = (int) (name.charAt(0)) - (int) 'A';
        if (accountNum < A || accountNum > Z)
            throw new InvalidTransactionError();
        Cache c = cacheList[accountNum];
        
        for (int i = 1; i < name.length(); i++) {
            if (name.charAt(i) != '*') throw new InvalidTransactionError();
            accountNum = (cacheList[accountNum].readCache() % numLetters);          
            c = cacheList[accountNum];
        }
        
        return c;
    }
    
	// requires: name != null
	// modifies: n/a
	// effects: returns a integer value of a string, or returns the value in the cacheList
    private int parseAccountOrNum(String name) {
    	int rtn;
        if (name.charAt(0) >= '0' && name.charAt(0) <= '9') {
             rtn = new Integer(name).intValue();
        }
        else {
        	rtn = parseAccount(name).readCache();
        }
        return rtn;
    }

	// requires: nothing
	// modifies: accounts and cacheList 
	// effects: accounts change according to transactions in inputFile by using the local cache
    // 			in order to process transactions and then using the cache class in order to determine
    //			which transactions can be processed by at what time by each thread in the multithreaded
    // 			process of task.
    public void run() {
        
    	//overall loop which runs until all the transactions have been processed
        while(true) {
        	
        	//instantiate the local cachelist
        	cacheList = new Cache[constants.numLetters];
        	for (int k = A; k <= Z; k++) {
                cacheList[k] = new Cache(accounts[k]);
                
        	}
        	
        	//parsing the input file 
        	String[] commands = transaction.split(";");
        	for (int i = 0; i < commands.length; i++) {
	        	 
	        	String[] words = commands[i].trim().split("\\s");
	            if (words.length < 3) throw new InvalidTransactionError();
	            
	            
	            //sets the read lock for the cache of the account.
	            Cache lhs = parseAccount(words[0]);
	            
	            if (!words[1].equals("="))
	                throw new InvalidTransactionError();
	            
	            int rhs = parseAccountOrNum(words[2]);
	            for (int j = 3; j < words.length; j+=2) {
	                if (words[j].equals("+"))
	                    rhs += parseAccountOrNum(words[j+1]);
	                else if (words[j].equals("-"))
	                    rhs -= parseAccountOrNum(words[j+1]);
	                else
	                    throw new InvalidTransactionError();
	            }
	            //sets the write lock for the account in the cache
	            lhs.writeCache(rhs);
	            
	        }
        	
	        //for loop 
        	/*int i = 0;
	        
	        try {
	        	for (i = A; i <= Z; i++) {
	        		cacheList[i].openCache();
	        	}
			} 
	        catch (TransactionAbortException e) {
	        	for (int j = A; j < i; j++) {
	        		cacheList[j].closeCache();
				}
	        	System.out.println("open abort: " + transaction);
				continue;
			}
	        
	        //try and verify do the same as the open locks 
	        try {
	        	for (i = A; i <= Z; i++) {
	        		cacheList[i].verify();
	        	}
			}
	        
	        catch (TransactionAbortException e) {
	        	for (int j = A; j < i; j++) {
	        		cacheList[j].closeCache();
				}
	        	System.out.println("verify abort: " + transaction);
				continue;
			}
	        
	        //loops to commit
	        for (int k = A; k <= Z; k ++) {
        		cacheList[k].commit();
        	}
	        
	        System.out.println("commit: " + transaction);
	        
	        //loops to close
	        for (int k = A; k <= Z; k ++) {
	        	cacheList[k].closeCache();
        	}
	        
	        break;*/
	        
        	//Code needed for concurrency programming, Loops over the entire cache list 4 different time
        	//and in order first attempts to open each caches locks, and then verifies all caches, then commits
        	//the caches, and finally closes all the locks for the caches. If it any point there is an exception
        	//is thrown then checks will end and all the accounts will be closed and the loop will restart from the top
        	//restart the transaction
        	try {
        		for (int i = A; i <= Z; i++) {
	        		cacheList[i].openCache();
	        	}
        		
        		for (int i = A; i <= Z; i++) {
	        		cacheList[i].verify();
	        	}
        		
        		for (int k = A; k <= Z; k ++) {
            		cacheList[k].commit();
            	}
        		
        		System.out.println("commit: " + transaction);
        		
        		for (int k = A; k <= Z; k ++) {
		        	cacheList[k].closeCache();
	        	}
        		
        		break;
        		
			} catch (TransactionAbortException e) {
				for (int k = A; k <= Z; k ++) {
		        	cacheList[k].closeCache();
	        	}
				continue;
			}
        	
        }//while loop
        
    }//end of run
    
}//end of task class


public class MultithreadedServer {

	// requires: accounts != null && accounts[i] != null (i.e., accounts are properly initialized)
	// modifies: accounts
	// effects: accounts change according to transactions in inputFile
    public static void runServer(String inputFile, Account accounts[])
        throws IOException {

        // read transactions from input file
        String line;
        BufferedReader input =
            new BufferedReader(new FileReader(inputFile));

        // TO DO: you will need to create an Executor and then modify the
        // following loop to feed tasks to the executor instead of running them
        // directly.
        final ExecutorService pool = Executors.newFixedThreadPool(3);
        
        while ((line = input.readLine()) != null) {
            Task t = new Task(accounts, line);
            pool.submit(t);
        }
        
        pool.shutdown();
        try {
        	pool.awaitTermination(60, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
		}
        
        input.close();

    }
    
}


