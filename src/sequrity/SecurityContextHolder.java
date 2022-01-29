package sequrity;

public class SecurityContextHolder {
    private static Principle principle;

    public static Principle getPrinciple(){
        return principle;
    }

    public static void Clear(){
        principle = null;
    }

    public static void setPrinciple(Principle principle){
        SecurityContextHolder.principle = principle;
    }

}
