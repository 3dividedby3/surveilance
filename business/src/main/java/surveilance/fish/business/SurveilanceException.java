package surveilance.fish.business;

public class SurveilanceException extends RuntimeException {

    private static final long serialVersionUID = 1546532934034402627L;

    public SurveilanceException(Throwable t) {
        super(t);
    }
    
    public SurveilanceException(String message, Throwable t) {
        super(message, t);
    }
}
