package gameProject;

import java.io.Serializable;

public class DoubleSideCard implements Serializable{

    int front;
    int back;
    int index;

    DoubleSideCard(int front, int back, int index){
        this.front = front;
        this.back  = back;
        this.index = index;
    }
}