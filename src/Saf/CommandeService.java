public class CommandeService {
    

    public CommandeService(){
        // constructeur bidon ....
    }   

    public String generateId(String prefix) {
        int n = (int)(Math.random() * 1_000_0000);
        return prefix + String.format("%07d", n);
    }
    
}
