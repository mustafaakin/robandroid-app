package inviso.app;

public enum SERVER_COMMAND {
    STOP(100),
    FORWARD(101),
    REVERSE(102),
    LEFT(103),
    RIGHT(104);	    

    private int numVal;

    SERVER_COMMAND(int numVal) {
        this.numVal = numVal;
    }

    public int getNumVal() {
        return numVal;
    }
}
