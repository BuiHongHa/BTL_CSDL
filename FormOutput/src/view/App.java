package view;

import com.formdev.flatlaf.FlatIntelliJLaf;
import view.input.LoginForm;

public class App {
    public static void main(String[] args) {
        FlatIntelliJLaf.setup();
        new LoginForm().setVisible(true);
    }
}
