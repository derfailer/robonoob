/*******************************************************************************
*  RoboNewbie
* NaoTeam Humboldt
* @author Monika Domanska
* @version 1.0
*******************************************************************************/

package examples.agentSimpleSoccer;

import agentIO.PerceptorInput;
import directMotion.LookAroundMotion;
import java.util.HashMap;
import keyframeMotion.KeyframeMotion;
import localFieldView.BallModel;
import localFieldView.GoalPostModel;
import localFieldView.LocalFieldView;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import util.FieldConsts.GoalPostID;
import util.Logger;


public class SoccerThinking {

  Logger log;
  PerceptorInput percIn;
  LocalFieldView localView;
  KeyframeMotion motion;
  BallModel ball;
  GoalPostModel oppGoalLPost, oppGoalRPost;
  private static final double TOLLERATED_DEVIATION = Math.toRadians(10);
  private static final double TOLLERATED_DISTANCE = 0.55; // in meters
  private double lookTime;
  private boolean robotIsWalking;

  /**
   * Constructor.
   *
   * @param All passed object must be already initalized, and correctly synchronized with the simulation server. None of the arguments can be null.
   */
  public SoccerThinking(PerceptorInput percIn, LocalFieldView localView,
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

      double serverTime = percIn.getServerTime();
      
      // if the robot has fallen down
      if (percIn.getAcc().getZ() < 7) {
        if (percIn.getAcc().getY() > 0) {
          motion.setStandUpFromBack();
        } else {
          motion.setRollOverToBack();
        }
      } 
      
      // if the robot has the actual ball coordinates
      else if ((serverTime - ball.getTimeStamp()) < lookTime) {
        
        Vector3D ballCoords = ball.getCoords();
        log.log("2. robot has the actual ball coordinates, horizontal angle: " 
                + Math.toDegrees(ballCoords.getAlpha()) 
                + " distance: " + ballCoords.getNorm()) ;

        // if the ball is not in front of the robot
        if (Math.abs(ballCoords.getAlpha()) > TOLLERATED_DEVIATION) {
//          log.log("3. the ball is not in front of the robot. ") ;
          if (robotIsWalking) {
            motion.setStopWalking();
            robotIsWalking = false;
          } else {
            if (ballCoords.getAlpha() > 0) {
              motion.setTurnLeftSmall();
            } else {
              motion.setTurnRightSmall();
            }
          }
        } 
        
        // if the robot is far away from the ball
        else if (ballCoords.getNorm() > TOLLERATED_DISTANCE) {
          log.log("3. the robot is far away from the ball.");
          motion.setWalkForward();
          robotIsWalking = true;
        } 
        
        // if the robot has the actual goal coordinates
        else if ((serverTime - oppGoalLPost.getTimeStamp() < lookTime)
                && (serverTime - oppGoalRPost.getTimeStamp() < lookTime)) {
          log.log("5. the robot has the actual goal coordinates");

          // if the ball does not lie between the robot and the goal
          if ((oppGoalLPost.getCoords().getAlpha() <= ballCoords.getAlpha())
                  || (oppGoalRPost.getCoords().getAlpha() >= ballCoords.getAlpha())) {
            log.log("6. the ball does not lie between the robot and the goal");
            if (robotIsWalking) {
              motion.setStopWalking();
              robotIsWalking = false;
            } else {
              if (oppGoalLPost.getCoords().getAlpha() <= ballCoords.getAlpha()) {
                motion.setSideStepLeft();
              } else {
                motion.setSideStepRight();
              }
            }
          } 
          
          // if the robot is in a good dribbling position
          else {
            log.log("7. the robot is in a good dribbling position");
            motion.setKick_right();
            robotIsWalking = true;
          }
        } 
        
        // if the robot cannot sense the goal coordinates from its actual position
        else {
          log.log("5. goal coordinates missing");
          motion.setTurnLeft();
        }
      } 
      
      // if the robot cannot sense the ball coordinates from its actual position
      else {
        motion.setTurnLeft();
        log.log("1. ball coordinates missing");
      }
    }
  }
}
