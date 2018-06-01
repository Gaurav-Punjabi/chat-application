/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UserInterface.components;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 *
 * @author gauravpunjabi
 */
public class ContactMouseEventHandling extends MouseAdapter implements ComponentConstants {

    public ContactMouseEventHandling(ContactComponent ref) {
        this.ref = ref;
    }
    
    @Override
    public void mousePressed(MouseEvent e) {
        ref.setBackground(CONTACT_COLOR_CLICKED);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        ref.setBackground(CONTACT_COLOR_HOVER);
    }
    
    @Override
    public void mouseEntered(MouseEvent e) {
        ref.setBackground(CONTACT_COLOR_HOVER);
    }

    @Override
    public void mouseExited(MouseEvent e) {
        ref.setBackground(CONTACT_COLOR_BACKGROUND);
    }
    private ContactComponent ref;
}
