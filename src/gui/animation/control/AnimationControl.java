
package gui.animation.control;


import gui.animation.events.AnimationEvent;
import java.util.ArrayList;
import java.util.LinkedList;
import javafx.animation.Animation;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.StrokeTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

/**
 *
 * @author Viktor
 */
public class AnimationControl {
    private ParallelTransition actualTransition;
 ///               Ovládání Animace            ///
    private IntegerProperty nextTransition = new SimpleIntegerProperty(0);
    private ArrayList<ParallelTransition> transitions;
    private double rate;
    private boolean markedAsStepping;
    private LinkedList<AnimationEvent> finishedEvents;
    private AnimationDescription ad;
    public AnimationControl() {
        transitions = new ArrayList<>();
        markedAsStepping = false;
        finishedEvents = new LinkedList<>();
        ad = AnimationDescription.getAnimationDescription();
    }

    public ArrayList<ParallelTransition> getTransitions() {
        return transitions;
    }

    public void addAnimationFinishedListener(AnimationEvent ae){
        finishedEvents.add(ae);
    }
   
    
    
  public void togglePlaying(){
      if(actualTransition != null){
          if(actualTransition.getStatus() == Animation.Status.RUNNING){
              actualTransition.pause();
          }
          if(actualTransition.getStatus() == Animation.Status.PAUSED ){
              actualTransition.play();
          }
      }
  }
  
    /**
     * krok zpět 
     */
    public void goBack(){
          if(actualTransition.getStatus() == Animation.Status.RUNNING){
            return; // - animace běží, nepřeskočíme
        }
        
          if(nextTransition.get() == 0){
              return;
          }
        /*  
        if(nextTransitionIndex == 0){
            return;
        }
          */
        playBack();
      
    }
    
    /***
     * Krok vpřed, pokud jsme se vraceli navážeme v dopředné animaci tam, kam jsme se vrátili
     */
    public void goForth(){
        
        
        if(actualTransition.getStatus() == Animation.Status.RUNNING){
            return; // - animace běží, nepřeskočíme
        }
        if(nextTransition.get() == transitions.size()){
            return;
        }
       /* 
        if(nextTransitionIndex == transitions.size()-1){
            return;
        }
        */
      //  playNextTransition();
        playForward();
    
        
    }
    
    public void setRate(double rate){
      if(actualTransition != null){
          actualTransition.setRate(actualTransition.getRate()*rate);
      }
      this.rate = rate;
  }
    
   protected EventHandler<ActionEvent> createForwardTransitionHandler(){
     return new EventHandler<ActionEvent>() {

         @Override
         public void handle(ActionEvent t) {
            nextTransition.set(nextTransition.get()+1);
/*            if(inserted){
                
            }*/
            if(!markedAsStepping){
                if(nextTransition.get() < transitions.size()){
                 //   updateDescription(nextTransitionIndex);
                    playNextTransition();
                    
                }else{
                    //nextTransitionIndex--;
                    animationFinished(true);
                }
            }
         }
     };
 }
   protected EventHandler<ActionEvent> createBackTransitionHandler(){
     return new EventHandler<ActionEvent>() {

         @Override
         public void handle(ActionEvent t) {
        
             if(!markedAsStepping){
                 if(nextTransition.get() >= 0){
                    
                     playPrevTransition();
                 }else{
                    
                     animationFinished(false);
                 }
             }else{
                
             }
             nextTransition.set(nextTransition.get()-1);
            
         }
     };
 }
   
   protected void playPrevTransition(){
     int index = nextTransition.get();
     actualTransition = transitions.get(index);
     setNodesToVisible(actualTransition);
     actualTransition.setOnFinished(createBackTransitionHandler());
     actualTransition.setRate(-1);
  //   updateDescription(false);
//     updateDescription(index);
     actualTransition.play();
 }
   protected void playNextTransition(){
     int index = nextTransition.get();
     actualTransition = transitions.get(index);
     setNodesToVisible(actualTransition);
     actualTransition.setOnFinished(createForwardTransitionHandler());
     actualTransition.setRate(1);
 //    updateDescription(index);
  //   updateDescription(true);
     actualTransition.play();
 }
   
   protected void animationFinished(boolean wentForward){
        if(wentForward){
/*
         if(hiding){
            for(NodeViewBlock nvb:layMan.getCreatedBlocks()){
                
                int index = settings.drawingPane.getChildren().indexOf(nvb);
                //settings.drawingPane.getChildren().get(index).disableProperty().set(false);
                settings.drawingPane.getChildren().get(index).visibleProperty().set(true);
            }
            hiding = false;
            layMan.eraseCreatedBlocks();
          }
         
         if(removing){
             animationBlock.setVisible(false);
             actualTransition = multipleMoveLeft(removingKey);
             actualTransition.setRate(1);
             actualTransition.play();
             removing = false;
         }
            */
        }
        for(AnimationEvent ae: finishedEvents){
            ae.handle();
        }
        
   }
   public void markAsStepping(boolean stepping){
       markedAsStepping = stepping;
   }
   
   private void playBack(){
     int index = nextTransition.get()-1;
     actualTransition = transitions.get(index);
     setNodesToVisible(actualTransition);
     actualTransition.setOnFinished(createBackTransitionHandler());
     actualTransition.setRate(-1);
 //    updateDescription(index);
     actualTransition.play();
 }
   public void playForward(){
     int index = nextTransition.get();
     actualTransition = transitions.get(index);
     setNodesToVisible(actualTransition);
     actualTransition.setOnFinished(createForwardTransitionHandler());
     actualTransition.setRate(1);
 //    updateDescription(index);
     actualTransition.play();
 }
  
  ///-----------------------------------------///
   
     private void setNodesToVisible(ParallelTransition pt) {
        for(Animation a: pt.getChildren()){
           
            if(a instanceof ScaleTransition){
                ScaleTransition st = (ScaleTransition) a;
                 st.getNode().setScaleX(0);
                st.getNode().setScaleY(0);
                st.getNode().setVisible(true);
            }
            if(a instanceof TranslateTransition){
                TranslateTransition tt = (TranslateTransition) a;
                
                tt.getNode().setVisible(true);
            }
            if(a instanceof StrokeTransition){
                StrokeTransition st = (StrokeTransition) a;
                
                st.getShape().setVisible(true);
            }
            if(a instanceof ParallelTransition){
                ParallelTransition p = (ParallelTransition) a;
               setNodesToVisible(p);
            }
        }
    }
     
     public void clear(){
         nextTransition.setValue(0);
         transitions.clear();
         finishedEvents.clear();
     }
/*
    private void updateDescription(boolean forward) {
        NodeViewBlock nvb = (NodeViewBlock) actualTransition.getNode();
        if(forward){
            
            nvb.setText(ad.getNextDescription());
        }else{
            nvb.setText(ad.getPreviousDescription());
        }
    }
     */
}
