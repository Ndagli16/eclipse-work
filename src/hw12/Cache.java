package hw12;

public class Cache {
    private Account account;
    private boolean read, written;
    private int initialValue, currentValue;

    public Cache(Account account) {
        this.account = account;
        read = false;
        written = false;
        initialValue = account.peek();
        currentValue = account.peek();
    }
    
    public int peekCache() {
    	if(read || written) {
    		return currentValue;
    	}
    	else {
    		initialValue = account.peek();
    		currentValue = initialValue;
    		read = true;
    	}
    	return currentValue;
    	
    }
    
    public void writeCache(int value) {
        this.written = true;
        currentValue = value;
    }

    public void openCache() throws TransactionAbortException {
        if (read) {
        	account.open(false);
        }
        if(written) {
        	account.open(true);
        }
    }

    public void closeCache() {
        if (read || written) account.close();
    }

    public void commit() {
        if (written) {
        	account.update(currentValue);
        }
    }

    public void verify() throws TransactionAbortException{
        if (read) {
        	account.verify(initialValue);
        }
    }
}
