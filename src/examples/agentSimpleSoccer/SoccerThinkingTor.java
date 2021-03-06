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
import util.RobotConsts;
import util.RobotConsts.BodyPartName;


 
public class SoccerThinkingTor {
  int a = 2;
  int i = 0;
  PlayerModel player = null;
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
  PlayerModel specificPlayer = null;
  Vector3D vecBall;
  Vector3D vecGoal;
  Vector3D vecRobotBodyPart;
  Vector3D vecLine;
  
  Vector3D[] vecHeads;
  String[] teams;
  String[] playerID;
  LinkedList<PlayerModel> players; 
  

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
    //LinkedList<PlayerModel> players = localView.getAllPlayers(); 
    
  }

  /**
   * Evaluate the actual situation the player is in, and set appropriate 
   * motions.
   * The idea for the soccer behavior is described in the comment above the 
   * definition of this class. 
   */
  
  public void decide() {
      
      
      this.players = localView.getAllPlayers();      
      if(this.players.isEmpty()){
          System.out.println("Playerlist empty (players == [])");
          System.out.println("Letzter Index: " + this.players.lastIndexOf(a));
      }
      else{
        i = 0;
        for (PlayerModel pm: players){
          
           log.log("Have seen a Player/Players");//specificPlayer = players.getFirst();
           vecHeads[i] = pm.getBodyPart(BodyPartName.Head);
           teams[i] = pm.getTeam();
           playerID[i] = pm.getID();
           
           System.out.println(vecRobotBodyPart);
           

        }
      }
      // Access the data of a sensed player. Keep in mind, that not all
      // parts of the player are visible at any time, depending on the
      // orientations of the agent´s own robot and the sensed player .
      /*  if (specificPlayer != null) for (RobotConsts.BodyPartName bp : RobotConsts.BodyPartName.values()){
            vecRobotBodyPart = specificPlayer.getBodyPart(bp);
            if (vecRobotBodyPart != null){
                log.log("Access to details of robot, e.g. the distance to "+ bp +" of Agent_Dummy: "
                + vecRobotBodyPart.getNorm() + ", and his team: "+ specificPlayer.getTeam() );
                break;
            }
       }*/
      
      if (motion.ready()) {
      
        log.log("new decision");
        //this.playerList = localView.getAllPlayers();   //TODO

        
        double serverTime = percIn.getServerTime();

        // if the robot has fallen down
        if (percIn.getAcc().getZ() < 7) {
          if (percIn.getAcc().getY() > 0) {
            motion.setStandUpFromBack();
          }
          else {
            motion.setRollOverToBack();
          }
        }    
      }
    }
}