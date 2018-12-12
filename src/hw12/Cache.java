package hw12;

public class Cache {
    private Account account;
    private boolean read, written, opened;
    private int initialValue, currentValue;

    public Cache(Account account) {
        this.account = account;
        read = false;
        written = false;
        initialValue = account.peek();
        currentValue = account.peek();
    }

    // TODO: This might be a problem
    public int readCache() {
        read = true;
        return currentValue;
    }
    
    public void writeCache(int value) {
        this.written = true;
        currentValue = value;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public void setCache(int value) {
        written = true;
        currentValue = value;
    }

    public void openCache() throws TransactionAbortException {
        boolean readOrWrite = false;
        if (written) readOrWrite = true;
        if (read) readOrWrite = false;
        if (written || read)  {
            opened = true;
            account.open(readOrWrite);
        }
    }

    public void closeIfNeeded() {
        if (opened) account.close();
    }

    public void commit() {
        if (written) account.update(currentValue);
    }

    public void verify() throws TransactionAbortException{
        if (read && opened) {
        	account.verify(currentValue);
        }
    }
}
