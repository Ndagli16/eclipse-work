package hw12;

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
    private int parseAccount(String name, CacheList cl) {
        int accountNum = (int) (name.charAt(0)) - (int) 'A';
        if (accountNum < A || accountNum > Z)
            throw new InvalidTransactionError();
        
        for (int i = 1; i < name.length(); i++) {
            if (name.charAt(i) != '*') throw new InvalidTransactionError();
            cl.caches[accountNum].readCache();
            accountNum = (cl.caches[accountNum].peekCache() % numLetters);          

        }
        
        return accountNum;
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
	            //cl.caches[parseAccount(words[0], cl)].writeCache(0);
	            
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
	        }//for loop
	        
	        //open the read lock before the write lock
	        
	        /*try {
				open lock for all caches
				VerifyError all caches
				commit all caches
				close lock for all caches
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
        final ExecutorService pool = Executors.newCachedThreadPool();

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


