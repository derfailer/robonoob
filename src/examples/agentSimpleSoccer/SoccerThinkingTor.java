/*******************************************************************************
*  RoboNewbie
* NaoTeam Humboldt
* @author Monika Domanska
* @version 1.0
*******************************************************************************/
/*
        TOR
*/
package examples.agentSimpleSoccer;

import agentIO.PerceptorInput;
import directMotion.LookAroundMotion;
import java.util.HashMap;
import java.util.LinkedList;
import keyframeMotion.KeyframeMotion;
import localFieldView.BallModel;
import localFieldView.GoalPostModel;
import localFieldView.LocalFieldView;
import localFieldView.PlayerModel;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import util.FieldConsts.GoalPostID;
import util.Logger;


 
public class SoccerThinkingTor {
  int a = 2;
  Logger log;
  PerceptorInput percIn;
  LocalFieldView localView;
  KeyframeMotion motion;
  BallModel ball;
  GoalPostModel oppGoalLPost, oppGoalRPost;
  PlayerModel oppPlayer;
  LinkedList playerList;
  private static final double TOLLERATED_DEVIATION = Math.toRadians(10);
  private static final double TOLLERATED_DISTANCE = 0.55; // in meters
  private double lookTime;
  private boolean robotIsWalking;

  /**
   * Constructor.
   *
   * @param All passed object must be already initalized, and correctly synchronized with the simulation server. None of the arguments can be null.
   */
  public SoccerThinkingTor(PerceptorInput percIn, LocalFieldView localView,
          KeyframeMotion kfMotion, Logger log){
    this.percIn = percIn;
    this.localView = localView;
    this.motion = kfMotion;
    this.motion.setLogging(true);
    this.log = log;
    this.ball = this.localView.getBall();
    HashMap<GoalPostID, GoalPostModel> goalPosts = this.localView.getGoals();
    this.oppGoalLPost = goalPosts.get(GoalPostID.G1R);
    this.oppGoalRPost = goalPosts.get(GoalPostID.G2R);
    this.lookTime = LookAroundMotion.LOOK_TIME;
    this.robotIsWalking = false;
    this.playerList = localView.getAllPlayers();   //TODO
    
  }

  /**
   * Evaluate the actual situation the player is in, and set appropriate 
   * motions.
   * The idea for the soccer behavior is described in the comment above the 
   * definition of this class. 
   */
  public void decide() {
    if (motion.ready()) {
      
      log.log("new decision");
      this.playerList = localView.getAllPlayers();   //TODO

      System.out.print(playerList);
      double serverTime = percIn.getServerTime();
      
      // if the robot has fallen down
      if (percIn.getAcc().getZ() < 7) {
        if (percIn.getAcc().getY() > 0) {
          motion.setStandUpFromBack();
        } else {
          motion.setRollOverToBack();
        }
      } 
      
      
          
    
    }
  }
}
