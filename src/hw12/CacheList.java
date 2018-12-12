package hw12;

/**
 * Singleton class to generate an array of Cachaes
 */
public class CacheList {
 
    public Cache[] caches;

    public CacheList (){
        caches = new Cache[constants.numLetters];
        for (int i = constants.A; i <= constants.Z; i ++) {
            caches[i] = new Cache(new Account(constants.Z - i));
        }
    }
}
