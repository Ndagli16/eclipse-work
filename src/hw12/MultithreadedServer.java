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
    //
    private Cache parseAccount(String name) {
        int accountNum = (int) (name.charAt(0)) - (int) 'A';
        if (accountNum < A || accountNum > Z)
            throw new InvalidTransactionError();
        Cache c = cacheList[accountNum];
        
        for (int i = 1; i < name.length(); i++) {
            if (name.charAt(i) != '*') throw new InvalidTransactionError();
            accountNum = (cacheList[accountNum].peekCache() % numLetters);          
            c = cacheList[accountNum];
        }
        
        return c;
    }

    private int parseAccountOrNum(String name) {
    	int rtn;
        if (name.charAt(0) >= '0' && name.charAt(0) <= '9') {
             rtn = new Integer(name).intValue();
        }
        else {
        	rtn = parseAccount(name).peekCache();
        }
        return rtn;
    }

    public void run() {
        // tokenize transaction
        
        while(true) {
        	cacheList = new Cache[constants.numLetters];
        	for (int k = A; k <= Z; k++) {
                cacheList[k] = new Cache(accounts[k]);
                
        	}
        	
        	String[] commands = transaction.split(";");
        	for (int i = 0; i < commands.length; i++) {
	        	 
	        	String[] words = commands[i].trim().split("\\s");
	            if (words.length < 3) throw new InvalidTransactionError();
	
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
	            lhs.writeCache(rhs);
	            
	        } 
	        //for loop
	        
	        /*try {
			open lock for all caches wrap in a try catch else break if an error is found
			then close all accounts from that one foward 
			VerifyError all caches wrap in a try catch else break 
			commit all caches dont wrap in a try catch
			close lock for all caches dont wrap in a try catch
			break
		} catch (Exception e) {
			close lock for all caches
			continue
		}*/
	        
	        //open the read lock before the write lock
	        //open locks, if an exception is thrown then stop and close all accounts following this account
	        int i = 0;
	        
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
	        
	        break;
	        
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


