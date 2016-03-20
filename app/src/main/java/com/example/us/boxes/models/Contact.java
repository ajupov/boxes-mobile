package com.example.us.boxes.models;

public class Contact {
    public Position begin;
    public Position end;

    public Contact(Contact contact) {
        begin = contact.begin;
        end = contact.end;
    }
    public Contact(Position begin, Position end) {
        this.begin = begin;
        this.end = end;
    }

    @Override
    public boolean equals(Object obj){
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final Contact other = (Contact) obj;
        if (begin != other.begin) return false;
        if (end != other.end) return false;
        return true;
    }

    public void move(int x, int y){
        begin.x += x;
        begin.y += y;
        end.x += x;
        end.y += y;
    }
    public boolean contact(Contact contact){
        //Вертикальное соприкосновение
        if (begin.x == contact.begin.x &&  end.x == contact.end.x) {
            if(begin.y >= contact.begin.y &&  begin.y < contact.end.y) {
                return true;
            }
            if(end.y > contact.begin.y && end.y <= contact.end.y) {
                return true;
            }
            if(begin.y < contact.begin.y && end.y > contact.end.y){
                return true;
            }
        }
        //Горизонтальное соприкосновение
        if (begin.y == contact.begin.y && end.y == contact.end.y) {
            if(begin.x >= contact.begin.x && begin.x < contact.end.x) {
                return true;
            }
            if(end.x > contact.begin.x && end.x <= contact.end.x) {
                return true;
            }
        }
        return false;
    }
    public static boolean contacted(Contact first, Contact second){
        //Вертикальное соприкосновение
        if (first.begin.x == second.begin.x && first.end.x == second.end.x) {
            if(first.begin.y >= second.begin.y && first.begin.y < second.end.y) {
                return true;
            }
            if(first.end.y > second.begin.y && first.end.y <= second.end.y) {
                return true;
            }
            if(first.begin.y < second.begin.y && first.end.y > second.end.y){
                return true;
            }
        }
        //Горизонтальное соприкосновение
        if (first.begin.y == second.begin.y && first.end.y == second.end.y) {
            if(first.begin.x >= second.begin.x && first.begin.x < second.end.x) {
                return true;
            }
            if(first.end.x > second.begin.x && first.end.x <= second.end.x) {
                return true;
            }
        }
        return false;
    }
    public static boolean contactedPlayerUp(Contact first, Contact second){
        //Горизонтальное соприкосновение
        if(Math.abs(second.begin.y - first.begin.y) < 3) {
            if(first.begin.x >= second.begin.x && first.begin.x < second.end.x) {
                return true;
            }
            if(first.end.x > second.begin.x && first.end.x <= second.end.x) {
                return true;
            }
        }
        return false;
    }
}
