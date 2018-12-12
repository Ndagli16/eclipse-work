package hw12;

import javax.imageio.metadata.IIOInvalidTreeException;

public class Cache {
    private Account account;
    private boolean read, written;
    private int initialValue, currentValue;

    public Cache(Account account) {
        this.account = account;
        read = false;
        written = false;
    }

    public int readCache() {
        read = true;
        return currentValue;
    }
    
    public int peekCache() {
    	if(read || written) {
    		return currentValue;
    	}
    	else {
    		initialValue = account.peek();
    		currentValue = initialValue;
    		read = true;
    		return currentValue;
    	}
    	
    }
    
    public void writeCache(int value) {
        this.written = true;
        currentValue = value;
    }

    public void openCache() throws TransactionAbortException {
        boolean readOrWrite = false;
        if (written) readOrWrite = true;
        if (read) readOrWrite = false;
        if (written || read)  {
            written = true;
            account.open(readOrWrite);
        }
    }

    public void closeCache() {
        if (read || written) account.close();
    }

    public void commit() {
        if (written) account.update(currentValue);
    }

    public void verify() throws TransactionAbortException{
        if (read) {
        	account.verify(currentValue);
        }
    }
}
