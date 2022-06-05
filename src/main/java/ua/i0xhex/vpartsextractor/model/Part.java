package ua.i0xhex.vpartsextractor.model;

public class Part {
    
    private int id;
    private int start;
    private int end;
    
    public Part(int id, int start, int end) {
        this.id = id;
        this.start = start;
        this.end = end;
    }
    
    // getters
    
    public int getId() {
        return id;
    }
    
    public int getStart() {
        return start;
    }
    
    public int getEnd() {
        return end;
    }
}
