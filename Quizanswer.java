import javax.swing.*;


public class Quizanswer {
private int x;
private int y;
private int width;
private int height;
private ImageIcon pic;
private int num;


public Quizanswer(){
    x=0;
    y=0;
    pic = new ImageIcon();
    width= 100;
    height=100;
    num=0;

 
}
public Quizanswer( int xV, int yV, ImageIcon p, int w, int h, int num ){
    x=xV;
    y=yV;
    pic=p;
    width=w;
    height=h;

     this.num=num;

}

public int getnum() {
    return num;
}
public void setnum(int num) {
    this.num = num;
}

public int getX() {
    return x;
}
public void setX(int x) {
    this.x = x;
}
public int getY() {
    return y;
}
public void setY(int y) {
    this.y = y;
}
public int getWidth() {
    return width;
}
public void setWidth(int width) {
    this.width = width;
}
public int getHeight() {
    return height;
}
public void setHeight(int height) {
    this.height = height;
}
public ImageIcon getPic() {
    return pic;
}
public void setPic(ImageIcon pic) {
    this.pic = pic;
}






}

