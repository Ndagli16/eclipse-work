package hw12;

import javax.security.auth.kerberos.KerberosKey;
import javax.transaction.InvalidTransactionException;
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
        if (name.charAt(0) >= '0' && name.charAt(0) <= '9') {
            return new Integer(name).intValue();
        }
        return 0;
    }

    public void run() {
        // tokenize transaction
        String[] commands = transaction.split(";");
        
        while(true) {
	        for (int i = 0; i < commands.length; i++) {
	        	 CacheList cl = new CacheList();
	        	
	        	String[] words = commands[i].trim().split("\\s");
	            
	            if (words.length < 3) throw new InvalidTransactionError();
	
	            int accountNum = (int) (words[0].charAt(0)) - (int) 'A';
	            
	            if (accountNum < A || accountNum > Z)
	                throw new InvalidTransactionError();
	            
	            //set the rhs to be able to be written after processing
	            cl.caches[parseAccount(words[0], cl)];
	            
	            if (!words[1].equals("="))
	                throw new InvalidTransactionError();
	            
	            int rhs_temp = 0;
	
	            if (words[2].charAt(0) >= '0' && words[2].charAt(0) <= '9') {
	                rhs_temp = parseAccountOrNum(words[2]);
	                
	            } 
	            else if (words[2].charAt(0) >= 'A' && words[2].charAt(0) <= 'Z') {
	                
	            	int accountNum2 = parseAccount(words[2], cl);
	                cl.caches[accountNum2].peekCache();
	                rhs_temp = cl.caches[accountNum2].peekCache();
	                
	            } 
	            else {
	                throw new InvalidTransactionError();
	            }
	            
	            if (words.length == 5) {
	                if (words[4].charAt(0) >= '0' && words[4].charAt(0) <= '9') {
	                    if (words[3].equals("+")){
	                        rhs_temp += parseAccountOrNum(words[4]);
	                    }
	                    else if(words[3].equals("-")){
	                        rhs_temp -= parseAccountOrNum(words[4]);
	                    }
	                    else{
	                        throw new InvalidTransactionError();
	                    }
	
	                }
	                else if (words[4].charAt(0) >= 'A' && words[4].charAt(0) <= 'Z') {
	                    int accountNum3 = parseAccount(words[4], cl);
	                    cl.caches[accountNum3].readCache();
	                    
	                    if (words[3].equals("+")){
	                        rhs_temp += cl.caches[accountNum3].peekCache();
	                    } 
	                    else if(words[3].equals("-")){
	                        rhs_temp -= cl.caches[accountNum3].peekCache();
	                    }
	                    else{
	                        throw new InvalidTransactionError();
	                    }
	                }
	                else{
	                    throw new InvalidTransactionError();
	                }
	            }
	        }
	        //for loop
	        
	        //open the read lock before the write lock
	        //open locks, if an exception is thrown then stop and close all accounts following this account
	        int i = constants.A;
	        try {
	        	
	        	for (i = constants.A; i <= constants.Z; i++) {
	        		
	        	}
			} 
	        	catch (Exception TransactionAbortExceptionn ) {
				for (int j = A; j < i; j++) {
					
				}
				continue;
			}
	        
	        //try and verify do the same as the open locks 
	        try {
	        	for (int i = constants.A; i <= constants.Z; i ++) {
	        		
	        	}
			} 
	        	catch (Exception TransactionAbortException) {
				// TODO: handle exception
			}
	        
	        //loops to commit
	        for (int i = constants.A; i <= constants.Z; i ++) {
        		
        	}
	        //loops to close
	        for (int i = constants.A; i <= constants.Z; i ++) {
        		
        	}
	        break;
	        
	        
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
	        
	        //Must open a read lock before a right lock
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
            pool.execute(t);
        }
        pool.shutdown();
        try {
        	pool.awaitTermination(60, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			System.out.println(e);
		}
        
        input.close();

    }
    }


