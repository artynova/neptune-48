package UI;

import java.awt.*;
import UI.LevelsMenu.TopPanel;
import UI.miscellaneous.*;
import data.DataManager;
import data.PlayerData;
import models.App;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.*;

import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class AbilityInfoPanel {

    public static AbilityBar[] passive = new AbilityBar[5];
    public static AbilityBar[] active = new AbilityBar[7];
    public TopPanel topPanel = LevelsMenu.topPanel;
    public static JLayeredPane pane;
    static boolean fadeThird = false;
    static boolean fadeSecond = false;
    

    public static JLayeredPane getAbilitiesPanel(){

        pane = new JLayeredPane();
        pane.setBounds(15,152,770,796);
        FilledBox backStroke = new FilledBox(new Color(56,151,74));
        backStroke.setBounds(15,152,770,796);
        pane.add(backStroke, new Integer(1));
        FilledBox back = new FilledBox(new Color(23,63,31));
        back.setBounds(20,157,760,786);
        pane.add(back, new Integer(2));



        AbilityInfoPanel abilityInfoPanel = new AbilityInfoPanel();

        passive[0] = abilityInfoPanel.new AbilityBar("bonusTurns", true);
        passive[1] = abilityInfoPanel.new AbilityBar("resistance", true);
        passive[2] = abilityInfoPanel.new AbilityBar("betterBaseLevel", true);
        passive[3] = abilityInfoPanel.new AbilityBar("cooldownReduction", true);
        passive[4] = abilityInfoPanel.new AbilityBar("bonusDamage", true);
        
        active[0] = abilityInfoPanel.new AbilityBar("swap", false);
        active[1] = abilityInfoPanel.new AbilityBar("crit", false);
        active[2] = abilityInfoPanel.new AbilityBar("merge", false);
        active[3] = abilityInfoPanel.new AbilityBar("dispose", false);
        active[4] = abilityInfoPanel.new AbilityBar("safeAttack", false);
        active[5] = abilityInfoPanel.new AbilityBar("upgrade", false);
        active[6] = abilityInfoPanel.new AbilityBar("scramble", false);
        
        revalidate();
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.add(saveButton());
        
        try {
            JLabel passiveLabel = new JLabel(new ImageIcon(ImageIO.read(App.class.getResourceAsStream("/images/levelInfo/passive.png"))));  
            passiveLabel.setPreferredSize(new Dimension(707,42));
            centerPanel.add(passiveLabel);
            for (AbilityBar ability : passive) {
                centerPanel.add(ability);
            }
            JLabel activeLabel = new JLabel(new ImageIcon(ImageIO.read(App.class.getResourceAsStream("/images/levelInfo/active.png"))));
            activeLabel.setPreferredSize(new Dimension(707,70));
            centerPanel.add(activeLabel);
            for (AbilityBar ability : active) {
                centerPanel.add(ability);
            }
            //centerPanel.add(UI.InfoPanels.buttonNext());
        } catch (Exception e) {}
        centerPanel.setBackground(new Color(23,63,31));
        
        

        JScrollPane scroll = new JScrollPane(centerPanel);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBounds(30,157,750,786);
        
        scroll.getVerticalScrollBar().setUI(new LevelsMenu.CustomScrollBarUI());
        scroll.getVerticalScrollBar().setUnitIncrement(LevelsMenu.scrollSpeed);

        pane.add(scroll, new Integer(3));
        pane.setVisible(true);
        secondInit();
        return pane;
    }
        

    private static void secondInit() {
        if(fadeThird){
            for (AbilityBar abilityBar : passive) {
                if(abilityBar.state.equals("selectable")){
                    abilityBar.state = "unavailable";
                    abilityBar.makeDark();
                }
            }
        }
        if(fadeSecond){
            for (AbilityBar abilityBar : active) {
                try {
                    if(abilityBar.nameID.equals(DataManager.loadPlayerData().getActiveAbility1())){
                        abilityBar.state = "chosen";
                        abilityBar.makeByState();
                    }
                } catch (Exception e) {}
            }
            for (AbilityBar abilityBar : active) {
                try {
                    if(abilityBar.nameID.equals(DataManager.loadPlayerData().getActiveAbility2())){
                        abilityBar.state = "chosen";
                        abilityBar.makeByState();
                    }
                } catch (Exception e) {}
            }
            for (AbilityBar abilityBar : active) {
                if(abilityBar.state.equals("selectable")){
                    abilityBar.state = "unavailable";
                    abilityBar.makeDark();
                }
            }
        }
        try {
            if(DataManager.loadPlayerData().getActiveAbility1() != null){
                for (AbilityBar abilityBar : active) {
                    if(abilityBar.nameID.equals(DataManager.loadPlayerData().getActiveAbility1())){
                        abilityBar.state = "chosen";
                        abilityBar.makeByState();
                    }
                }
            }
        } catch (Exception e) { }
        try {
            if(DataManager.loadPlayerData().getActiveAbility2() == null){
                for (AbilityBar abilityBar : active) {
                    if(abilityBar.state.equals("unavailable")){
                        abilityBar.state = "selectable";
                        abilityBar.makeNormal();
                    }
                }
            }
        } catch (Exception e) { }
        try {
            if(DataManager.loadPlayerData().getPassiveAbility() == null){
                for (AbilityBar abilityBar : passive) {
                    if(abilityBar.state.equals("unavailable")){
                        abilityBar.state = "selectable";
                        abilityBar.makeNormal();
                    }
                }
            }
        } catch (Exception e) { }
        try {
            if(DataManager.loadPlayerData().getPassiveAbility() != null){
                for (AbilityBar abilityBar : passive) {
                    if(abilityBar.nameID.equals(DataManager.loadPlayerData().getPassiveAbility())){
                        abilityBar.state = "chosen";
                        abilityBar.makeByState();
                    }
                }
            }
        } catch (Exception e) { }
    }

    static void revalidate(){
        boolean fadePassive = false;
        for (AbilityBar abilityBar : passive) {
            if(abilityBar.state.equals("chosen")){
                fadePassive = true;
                break;
            }
        }
        if(fadePassive){
            for (AbilityBar abilityBar : passive) {
                if(abilityBar.state.equals("selectable")){
                    abilityBar.state = "unavailable";
                    abilityBar.makeDark();
                }
                
            }
        }
    }

    public static JLayeredPane saveButton(){
        JLayeredPane panel = new JLayeredPane();
        panel.setBackground(new Color(23,63,31));
        panel.setPreferredSize(new Dimension(750,125));
        try {
            ImageIcon save = new ImageIcon(ImageIO.read(App.class.getResourceAsStream("/images/levelInfo/saveAbilities.png")));
            ImageIcon saveLight = new ImageIcon(ImageIO.read(App.class.getResourceAsStream("/images/levelInfo/saveAbilitiesLight.png")));
            JLabel saveButton = new JLabel(save);
            saveButton.setBounds(21,20,707,82);
            saveButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    saveButton.setIcon(saveLight);
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    saveButton.setIcon(save);
                }
                @Override
                public void mouseClicked(MouseEvent e) {
                    pane.getParent().remove(LevelsMenu.overlayPane);
                    LevelsMenu.overlayPane = null;
                    App.getLevelsMenu().revalidate();
                    App.getLevelsMenu().repaint();
                }
            });
            panel.add(saveButton);

        } catch (Exception e) {System.out.println(e);}
        return panel;
    } 


    public class AbilityBar extends JLayeredPane{
        ImageIcon light;
        ImageIcon normal;
        ImageIcon dark;
        ImageIcon lock;
        ImageIcon checked;
        ImageIcon iconForTopPanel;
        JLabel image;
        /*
         * unavailable
         * selectable
         * chosen
         * locked
         */
        public String state;
        JLabel overlay;
        String title;
        String nameID;


        public AbilityBar(String title, boolean isPassive){
            String folder;
            this.title = title;
            nameID = title;
            try {     
                iconForTopPanel = new ImageIcon(ImageIO.read(App.class.getResourceAsStream("/images/level/" + title + ".png")));
            } catch (Exception ex) { }
            setPreferredSize(new Dimension(600,131));
            if(isPassive){
                title = "passive/" + title;
                folder = "passive/";
            }else{
                title = "active/" + title;
                folder = "active/";
            }

            try {
                light = new ImageIcon(ImageIO.read(App.class.getResourceAsStream("/images/levelInfo/abilities/" + title + "Light.png")));
                normal = new ImageIcon(ImageIO.read(App.class.getResourceAsStream("/images/levelInfo/abilities/" + title + ".png")));
                dark = new ImageIcon(ImageIO.read(App.class.getResourceAsStream("/images/levelInfo/abilities/" + title + "Dark.png")));

                lock = new ImageIcon(ImageIO.read(App.class.getResourceAsStream("/images/levelInfo/abilities/" + folder + "lock.png")).getScaledInstance(104, 104, Image.SCALE_SMOOTH));
                checked = new ImageIcon(ImageIO.read(App.class.getResourceAsStream("/images/levelInfo/abilities/" + folder + "checked.png")).getScaledInstance(104, 104, Image.SCALE_SMOOTH));
                
                overlay = new JLabel();
                overlay.setBounds(26,14,104,104);
                image = new JLabel(normal);
                image.setVisible(true);
                image.setBounds(0,0,600,131);
                add(image);
                add(overlay,0);
            } catch (Exception e) {e.printStackTrace();}

            image.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    if(state.equals("selectable")){
                        image.setIcon(light);
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    if(state.equals("selectable")){
                        image.setIcon(normal);
                    }
                }
                @Override
                public void mouseClicked(MouseEvent e) {
                    //right mouse button
                    if (e.getButton() == MouseEvent.BUTTON3) {
                        pane.getParent().remove(LevelsMenu.overlayPane);
                        LevelsMenu.overlayPane = null;
                        App.getLevelsMenu().revalidate();
                        App.getLevelsMenu().repaint();
                    }
                    else if(e.getButton() == MouseEvent.BUTTON1){   
                        if(state.equals("selectable")){
                            AbilityBar[] set = isPassive ? passive : active;
                            if(isPassive){
                                for (AbilityBar abilityBar : set) {
                                    if(abilityBar.state.equals("selectable")){
                                        abilityBar.makeDark();
                                        abilityBar.state = "unavailable";
                                    }
                                }
                                makeChecked();
                                try {
                                    PlayerData playerData = DataManager.loadPlayerData();
                                    playerData.setPassiveAbility(nameID);
                                    DataManager.savePlayerData(playerData);
                                } catch (Exception ex) {}
                                
                                try {
                                    topPanel.setAbility(3, iconForTopPanel);
                                } catch (Exception ex) {}
                            }
                            else{//is active
                                //is second
                                if(topPanel.abilityFirst.isVisible()){
                                    for (AbilityBar abilityBar : set) {
                                        if(abilityBar.state.equals("selectable")){
                                            abilityBar.makeDark();
                                            abilityBar.state = "unavailable";
                                        }
                                    }
                                    makeChecked();
                                    try {
                                        PlayerData playerData = DataManager.loadPlayerData();
                                        playerData.setActiveAbility2(nameID);
                                        DataManager.savePlayerData(playerData);
                                    } catch (Exception ex) {}
                                    topPanel.setAbility(2, iconForTopPanel);
                                }
                                else{//first chosen
                                    makeChecked();
                                    try {
                                        PlayerData playerData = DataManager.loadPlayerData();
                                        playerData.setActiveAbility1(nameID);
                                        DataManager.savePlayerData(playerData);
                                    } catch (Exception ex) {}
                                    topPanel.setAbility(1, iconForTopPanel);
                                }
                            }
                        }else if(state.equals("chosen")){//unchecking
                            if(isPassive){
                                state = "selectable";
                                makeNormal();
                                try {
                                    PlayerData playerData = DataManager.loadPlayerData();
                                    playerData.removePassiveAbility();
                                    DataManager.savePlayerData(playerData);
                                } catch (Exception ex) {System.out.println(ex);}
                                for (AbilityBar abilityBar : passive) {
                                    if(abilityBar.state.equals("unavailable")){
                                        abilityBar.state = "selectable";
                                        abilityBar.makeNormal();
                                    }
                                }
                                topPanel.removeAbility(3);
                            }
                            else{
                                //active, two chosen unchecking
                                if(topPanel.abilitySecond.isVisible()){
                                    try {
                                        state = "selectable";
                                        makeNormal();
                                        PlayerData playerData = DataManager.loadPlayerData();
                                        boolean isFirst = nameID.equals(playerData.getActiveAbility1());

                                        if(isFirst){                             
                                            playerData.setActiveAbility1(playerData.getActiveAbility2());
                                            playerData.removeActiveAbility2();
                                            DataManager.savePlayerData(playerData);
                                            for (AbilityBar abilityBar : active) {
                                                if(abilityBar.state.equals("unavailable")){
                                                    abilityBar.state = "selectable";
                                                    abilityBar.makeNormal();
                                                }
                                            }
                                            topPanel.removeAbility(1);
                                        }
                                        else{
                                            for (AbilityBar abilityBar : active) {
                                                if(abilityBar.state.equals("unavailable")){
                                                    abilityBar.state = "selectable";
                                                    abilityBar.makeNormal();
                                                }
                                            }
                                            playerData.removeActiveAbility2();
                                            DataManager.savePlayerData(playerData);
                                            topPanel.removeAbility(2);
                                        }

                                    } catch (Exception ex) {System.out.println("309: " + ex);}
                                }
                                else{//one chosen (this)
                                    state = "selectable";
                                    makeNormal();
                                    try {
                                        PlayerData playerData = DataManager.loadPlayerData();
                                        playerData.removeActiveAbility1();
                                        DataManager.savePlayerData(playerData);
                                    } catch (Exception ex) {System.out.println(ex);}
                                    topPanel.removeAbility(1);
                                }
                            }
                        }
                    }
                }
            });
            init();
        }
        public void init(){
            try {
                PlayerData playerData = DataManager.loadPlayerData();
                if(playerData.isAbilityUnlocked(title)){
                    String ab1 = "";
                    String ab2 = "";
                    String ab3 = "";
                    if(playerData.getActiveAbility1() != null){
                        ab1 = playerData.getActiveAbility1();
                    }
                    if(playerData.getActiveAbility2() != null){
                        ab2 = playerData.getActiveAbility2();
                    }
                    if(playerData.getPassiveAbility() != null){
                        ab3 = playerData.getPassiveAbility();
                    }
                    if(ab1.equals(title) ){
                        state = "chosen";
                        makeChecked();
                        topPanel.setAbility(1, iconForTopPanel);
                    }
                    if(ab2.equals(title)){
                        state = "chosen";
                        makeChecked();
                        topPanel.setAbility(2, iconForTopPanel);
                        fadeSecond = true;
                    }
                    if (ab3.equals(title)){
                        state = "chosen";
                        topPanel.setAbility(3, iconForTopPanel);
                        fadeThird = true;
                    }
                    else{
                        state = "selectable";
                    }
                }
                else{
                    state = "locked";
                }
                makeByState();
            } catch (Exception e) {System.out.println("376: " + e);}
        }

        public void makeLight(){
            image.setIcon(light);
        }

        public void makeDark(){
            image.setIcon(dark);
        }

        public void makeNormal(){
            image.setIcon(normal);
            overlay.setIcon(null);
        }

        public void makeLock(){
            state = "locked";
            overlay.setIcon(lock);
            makeDark();
        }

        public void makeChecked(){
            state = "chosen";
            overlay.setIcon(checked);
            makeLight();
        }

        public void makeByState(){
            if(state.equals("unavailable")){
                makeDark();
            }else if(state.equals("selectable")){
                makeNormal();
            }else if(state.equals("chosen")){
                makeChecked();
            }else if(state.equals("locked")){
                makeLock();
            }
        }
    }


}
