package client;
import common.User;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class RankDialog extends JDialog {
    public RankDialog(JFrame owner, List<User> users){
        super(owner, "Bảng xếp hạng", true);
        String[] cols = {"#","Username","Win","Lose","Rank","Balance"};
        Object[][] data = new Object[users.size()][cols.length];
        for(int i=0;i<users.size();i++){
            var u = users.get(i);
            data[i] = new Object[]{i+1, u.username, u.totalWin, u.totalLose, u.rankPoint, u.balance};
        }
        JTable t = new JTable(data, cols);
        add(new JScrollPane(t));
        setSize(520,320); setLocationRelativeTo(owner);
    }
}
