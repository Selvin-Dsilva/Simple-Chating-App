package javaapplication2;
import java.awt.Graphics;

public class WhiteBoard extends ChatMessage {

    int x1, y1, x2, y2;	

    public String flag;

    public WhiteBoard(int x1, int y1, int x2, int y2) {
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;

    }

    WhiteBoard() {

    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public void paint(Graphics g) {
        g.drawLine(x1, y1, x2, y2);
    }

    public int getx1() {
        return x1;
    }

    public int gety1() {
        return y1;
    }

    public int getx2() {
        return x2;
    }

    public int gety2() {
        return y2;
    }

}
