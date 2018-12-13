package hw12;


public class Cache {
    private Account account;
    private boolean read, written, open;
    private int initialValue, currentValue;
    
    // requires: name != null
 	// modifies: cacheList
 	// effects: changes the cacheList depending on the input passed in
    public Cache(Account account) {
        this.account = account;
        read = false;
        written = false;
        open = false;
        initialValue = account.peek();
        currentValue = account.peek();
    }
    
    /*public int peekCache() {
    	if(read || written) {
    		return currentValue;
    	}
    	else {
    		initialValue = account.peek();
    		currentValue = initialValue;
    		read = true;
    	}
    	return currentValue;
    	
    }*/

    // requires: name != null
 	// modifies: cacheList
 	// effects: changes the cacheList depending on the input passed in
    public int readCache() {
    	read = true;
    	return currentValue;
    }
    
    // requires: name != null
 	// modifies: cacheList
 	// effects: changes the cacheList depending on the input passed in
    public void writeCache(int value) {
        this.written = true;
        currentValue = value;
    }

    // requires: name != null
 	// modifies: cacheList
 	// effects: changes the cacheList depending on the input passed in
    public void openCache() throws TransactionAbortException {
        if (read) {
        	account.open(false);
        	open = true;
        }
        if(written) {
        	account.open(true);
        	open = true;
        }
    }

    // requires: name != null
 	// modifies: cacheList
 	// effects: changes the cacheList depending on the input passed in
    public void closeCache() {
        if (open) account.close();
    }

    // requires: name != null
 	// modifies: cacheList
 	// effects: changes the cacheList depending on the input passed in
    public void commit() {
        if (written) {
        	account.update(currentValue);
        }
    }

    // requires: name != null
 	// modifies: cacheList
 	// effects: changes the cacheList depending on the input passed in
    public void verify() throws TransactionAbortException{
        if (read) {
        	account.verify(initialValue);
        }
    }
}
