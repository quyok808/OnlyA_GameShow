/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Vong4;

import Context.DBAccess;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author Le Cuong
 */
public class ListScoreBoard {
    private List<ScoreBoard> _scoreBoards;

    public ListScoreBoard() {
        this._scoreBoards = new ArrayList<>();
    }

    public List<ScoreBoard> getScoreBoards() {
        return _scoreBoards;
    }

    public void setScoreBoards(List<ScoreBoard> _scoreBoards) {
        this._scoreBoards = _scoreBoards;
    }
    
    public void addNewPlayer(ScoreBoard newPlayer){
        newPlayer.setScore(0);
        _scoreBoards.add(newPlayer);
    }
    
    public int getScore(String username){
        for (ScoreBoard temp : _scoreBoards){
            if (temp.getUsername().equals(username)){
                return temp.getScore();
            }
        }
        return -1;
    }
    
    public void saveDatabase(){
        DBAccess _context = new DBAccess();
        int kq = 0;
        
        for (ScoreBoard item : _scoreBoards){
            kq = _context.Update("INSERT INTO scoreboard VALUES('"+item.getUserID()+"','"+item.getUsername()+"',"+item.getScore()+")");
            
            if (kq != 1) {
                JOptionPane.showMessageDialog(null, "Có lỗi khi lưu điểm vào database !!!");
            }
        }
    }
    
    public int updateScore(String username, int currentScore){
        for (ScoreBoard item : _scoreBoards){
            if (item.getUsername().equals(username)){
                item.setScore(currentScore);
                return 1;
            }
        }
        return 0;
    }
}
