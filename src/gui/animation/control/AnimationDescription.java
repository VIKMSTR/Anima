
package gui.animation.control;

import java.util.ArrayList;

/**
 *
 * @author Viktor
 */
public class AnimationDescription {

    private static AnimationDescription instance;
    private int pointer;
    private ArrayList<String> strings;
    
    
    private AnimationDescription(){
        refresh();
    }
    public static AnimationDescription getAnimationDescription(){
        if(instance == null){
            instance = new AnimationDescription();
        }
        return instance;
    }
    
    public void refresh(){
        pointer = 0;
        strings = new ArrayList<>();
    }
    public String getPreviousDescription(){
        if(pointer > 0){
            return strings.get(pointer--);
        }
        else return "";
    }
    public String getNextDescription(){
        if(pointer < strings.size()){
            return strings.get(pointer++);
        }
        else return "";
    }
    
    public void addDescription(String s){
         strings.add(s);
    }
    
    
}
