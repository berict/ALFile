/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package alfile;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javax.swing.JOptionPane;

/**
 *
 * @author Bedrock Pictures
 */
public class FXMLDocumentController implements Initializable {
    
    @FXML
    private Label label;
    @FXML
    private Button button;
    
    @FXML
    private void handleButtonAction(ActionEvent event) {
        System.out.println("You clicked me!");
        label.setText("Set zum.com as your start page!");
    }
    
    @FXML
    private void fileExtensionButtonAction(ActionEvent event) {
        
    }
    
    @FXML
    private void quitButtonAction(ActionEvent event) {
        int result = JOptionPane.showConfirmDialog(null, 
                "Would you want to Save and Quit?", "Save and Quit", 
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        
        if (result == JOptionPane.YES_OPTION) {
            // save changes
            System.exit(0);
        }
    }
    
    @FXML
    private void applyButtonAction(ActionEvent event) {
        // save changes
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
