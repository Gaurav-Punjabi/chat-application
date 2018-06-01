/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UserInterface.components;

import java.awt.Color;
import java.awt.Font;
import javax.swing.ImageIcon;

/**
 *
 * @author gauravpunjabi
 */
public interface ComponentConstants {
    ImageIcon READ_LOGO = new ImageIcon("/images/read.png");
    ImageIcon SENT_LOGO = new ImageIcon("/images/sent.png");
    ImageIcon DELIVERED_LOGO = new ImageIcon("/images/sent.png");
    Color MESSAGE_COLOR_SENT = new Color(242,245,234);
    Color MESSAGE_COLOR_RECIEVED =new Color(48,188,237);
    Color MESSAGE_COLOR_FONT = new Color(51,55,69);
    Color CONTACT_COLOR_BACKGROUND = new Color(61,90,128).darker();
    Color CONTACT_COLOR_MESSAGE = new Color(189, 195, 199);
    Color CONTACT_COLOR_HOVER = new Color(56, 95, 144);
    Color CONTACT_COLOR_CLICKED = new Color(46, 57, 71);
    Color CONTACT_COLOR_FOREGROUND = Color.WHITE;
    Font MESSAGE_FONT = new Font("Raleway",0,20);
    int CONTACT_DP_WIDTH = 90;
    int CONTACT_DP_HEIGHT = 90;
    int CONTACT_WIDTH = 424;
    int CONTACT_HEIGHT = 102;
}
