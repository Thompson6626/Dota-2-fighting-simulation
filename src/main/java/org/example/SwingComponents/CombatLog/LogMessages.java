package org.example.SwingComponents.CombatLog;


public enum LogMessages {

    KILL_LOG("""
             <html>
                <font color='%s'>%s</font>\s
                is killed by\s
                <font color='%s'>%s</font>!
             </html>
             """
    ),

    ATTACK_LOG( """
             <html>
                <font color='%s'>%s</font>\s
                hits\s
                <font color='%s'>%s</font>\s
                for\s
                <font color='rgb(250, 4, 10)'>%s</font>\s
                <font color='rgb(30, 255, 0)'>%s</font>
             </html>
             """);

    private final String message;
    LogMessages(String message){
        this.message = message;
    }
    public String getMessage(){
        return message;
    }
}
