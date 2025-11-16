
package library;

import library.controller.MainController;
import library.view.MainView;

import javax.swing.*;

public class MainApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainView view = new MainView();
            new MainController(view);
            view.setVisible(true);
        });
    }
}
