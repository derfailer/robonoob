/*******************************************************************************
*  RoboNewbie
* NaoTeam Humboldt
* @author Monika Domanska
* @version 1.0
*******************************************************************************/

package examples.agentSimpleSoccer;


import agentIO.EffectorOutput;
import agentIO.PerceptorInput;
import agentIO.ServerCommunication;
import directMotion.LookAroundMotion;
import java.io.IOException;
import java.util.logging.Level;
import keyframeMotion.KeyframeMotion;
import localFieldView.LocalFieldView;
import util.Logger;

public class Agent_SimpleSoccerTor {

  public static void main(String args[]) {
    
    Agent_SimpleSoccerTor agent = new Agent_SimpleSoccerTor();
    
    agent.init();
    
    agent.run();
    
    agent.printlog();
    
    System.out.println("Agent stopped.");
  }

  private Logger log;
  private PerceptorInput percIn;
  private EffectorOutput effOut;
  private KeyframeMotion kfMotion;
  private LocalFieldView localView;
  private SoccerThinkingTor soccerThinking;
  private LookAroundMotion lookAround;
  

  final String id = "1";
  final String team = "simpleSoccer";
  
  final double beamX =    -0.5;
  final double beamY =     0.4;
  final double beamRot =   -40;

  /**
   * Initialize the connection to the server, the internal used classes and 
   * their relations to each other, and create the robot at a specified position 
   * on the field. 
   */
  private void init() {


    ServerCommunication sc = new ServerCommunication();

    log = new Logger();
    percIn = new PerceptorInput(sc);
    effOut = new EffectorOutput(sc);
    kfMotion = new KeyframeMotion(effOut, percIn, log);
    localView = new LocalFieldView(percIn, log, team, id);
    lookAround = new LookAroundMotion(percIn, effOut, log);
    
    soccerThinking = new SoccerThinkingTor(percIn, localView, kfMotion, log);

    sc.initRobot(id, team, beamX, beamY, beamRot);
    
  }
  

  /**
   * Main loop of the agent program, where it is synchronized with the 
   * simulation server. 
   * 
   * How long the agent program will run can be changed in variable 
   * "agentRunTimeInSeconds". This is just an approximation, because this value 
   * is used to calculate a number of server cycles, and the agent will 
   * participate in this amount of cycles. 
   */
  public void run(){
    
    int agentRunTimeInSeconds = 120000;
    
    // The server cycle represents 20ms, so the agent has to execute 50 cycles 
    // to run 1s. 
    int totalServerCycles = agentRunTimeInSeconds * 50;
    
    // This loop synchronizes the agent with the server.
    for (int i = 0; i < totalServerCycles; i++) {
      
      //check for aborting the agent from the console (by hitting return)
      try {
        if (System.in.available() != 0)
          break;
      } catch (IOException ex) {
        java.util.logging.Logger.getLogger(Agent_SimpleSoccerTor.class.getName()).log(Level.SEVERE, null, ex);
      }
      
      sense();     
      
      think();
      
      act();
    }
  }
  
  /**
   * Update the world and robot hardware informations, that means process 
   * perceptor values provided by the server.
   */
  private void sense() {
    // Receive the server message and parse it to get the perceptor values. 
    percIn.update();
    // Proceed and store values of the vision perceptor.
    localView.update();
  }
  
  /**
   * Decide, what is sensible in the actual situation. 
   * Use the knowledge about the field situation updated in sense(), and choose
   * the next movement - it will be realized in act().
   */
  private void think(){
    soccerThinking.decide();
  }
  
  /**
   * Move the robot hardware, that means send effector commands to the server. 
   */
  private void act(){
    // Calculate effector commands and send them to the server, this method
    // of class KeyframeMotion has to be called in every server cycle. 
    kfMotion.executeKeyframeSequence();
    lookAround.look(); // No matter, which move the robot executes, it should
                       // always turn its head around. So the LookAroundMotion
                       // is called after the KeyframeMotion, to overwrite the 
                       // commands for the head. 
    // Send agent message with effector commands to the server.
    effOut.sendAgentMessage();
  }

  /**
   * Print log informations - if there where any. 
   */
  private void printlog() {
    log.printLog();
  }
  
}
